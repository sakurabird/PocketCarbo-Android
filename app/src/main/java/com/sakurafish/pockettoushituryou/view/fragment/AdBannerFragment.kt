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
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.ads.AdListener
import com.sakurafish.pockettoushituryou.R
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

    private lateinit var binding: FragmentAdbannerBinding
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
    ): View? = DataBindingUtil.inflate<FragmentAdbannerBinding>(
            inflater,
            R.layout.fragment_adbanner,
            container,
            false
    ).also {
        binding = it
    }.root

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        parentActivityName = activity!!.javaClass.simpleName
        setupAdView()
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
        LocalBroadcastManager.getInstance(activity!!).registerReceiver(receiverADClick, filter)
    }

    private fun startInterval(clicked: Boolean) {
        binding.adView.visibility = View.GONE

        val now = System.currentTimeMillis()

        if (clicked) {
            adsHelper.lastClickTimeMillis = now
            val localIntent = Intent(ACTION_BANNER_CLICK)
            localIntent.putExtra(INTENT_EXTRAS_KEY_CLASS, parentActivityName)
            LocalBroadcastManager.getInstance(activity!!).sendBroadcast(localIntent)
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
        job.cancel()
    }

    override fun onDestroy() {
        super.onDestroy()
        finishInterval()
        LocalBroadcastManager.getInstance(activity!!).unregisterReceiver(receiverADClick)
    }
}
