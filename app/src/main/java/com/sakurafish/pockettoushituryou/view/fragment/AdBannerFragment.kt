package com.sakurafish.pockettoushituryou.view.fragment

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.WorkerThread
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.ads.AdListener
import com.sakurafish.pockettoushituryou.databinding.FragmentAdbannerBinding
import com.sakurafish.pockettoushituryou.di.Injectable
import com.sakurafish.pockettoushituryou.view.helper.AdsHelper
import com.sakurafish.pockettoushituryou.view.helper.AdsHelper.Companion.ACTION_BANNER_CLICK
import com.sakurafish.pockettoushituryou.view.helper.AdsHelper.Companion.INTENT_EXTRAS_KEY_CLASS
import kotlinx.coroutines.*
import javax.inject.Inject

class AdBannerFragment : Fragment(), Injectable {

    @Inject
    lateinit var adsHelper: AdsHelper

    private var _binding: FragmentAdbannerBinding? = null
    private val binding get() = _binding!!

    private lateinit var job: Job
    private var parentActivityName: String? = null

    private val receiverADClick = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == null
                    || intent.action != ACTION_BANNER_CLICK
                    || intent.getStringExtra(INTENT_EXTRAS_KEY_CLASS) == parentActivityName) {
                return
            }
            finishInterval()
            startInterval(false)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAdbannerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        parentActivityName = requireActivity().javaClass.simpleName
        setupAdView()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupAdView() {
        if (adsHelper.isIntervalOK) {
            binding.adView.visibility = View.VISIBLE
            binding.adView.loadAd(adsHelper.adRequest)
        } else {
            startInterval(false)
        }

        binding.adView.adListener = object : AdListener() {
            override fun onAdOpened() {
                startInterval(true)
            }
        }
        setADClickReceiver()
    }

    private fun setADClickReceiver() {
        val filter = IntentFilter()
        filter.addAction(ACTION_BANNER_CLICK)
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(receiverADClick, filter)
    }

    private fun startInterval(clicked: Boolean) {
        binding.adView.visibility = View.GONE

        val now = System.currentTimeMillis()

        if (clicked) {
            adsHelper.lastClickTimeMillis = now
            val localIntent = Intent(ACTION_BANNER_CLICK)
            localIntent.putExtra(INTENT_EXTRAS_KEY_CLASS, parentActivityName)
            LocalBroadcastManager.getInstance(requireContext()).sendBroadcast(localIntent)
        }
        val intervalTimeMillis = adsHelper.getIntervalTimeMillis(now, adsHelper.lastClickTimeMillis)

        job = GlobalScope.launch {
            waitInterval(intervalTimeMillis)
        }
    }

    @WorkerThread
    suspend fun waitInterval(intervalTimeMillis: Long) {
        withContext(Dispatchers.IO) {
            delay(intervalTimeMillis)
            withContext(Dispatchers.Main) {
                binding.adView.visibility = View.VISIBLE
                binding.adView.loadAd(adsHelper.adRequest)
            }
        }
    }

    private fun finishInterval() {
        if (::job.isInitialized && job.isActive) job.cancel()
    }

    override fun onDestroy() {
        super.onDestroy()
        finishInterval()
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(receiverADClick)
    }
}
