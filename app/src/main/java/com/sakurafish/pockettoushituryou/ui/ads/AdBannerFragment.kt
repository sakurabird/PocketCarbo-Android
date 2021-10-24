package com.sakurafish.pockettoushituryou.ui.ads

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.WorkerThread
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.sakurafish.pockettoushituryou.R
import com.sakurafish.pockettoushituryou.databinding.FragmentAdbannerBinding
import com.sakurafish.pockettoushituryou.di.module.IoDispatcher
import com.sakurafish.pockettoushituryou.di.module.MainDispatcher
import com.sakurafish.pockettoushituryou.ui.ads.AdsHelper.Companion.ACTION_BANNER_CLICK
import com.sakurafish.pockettoushituryou.ui.ads.AdsHelper.Companion.INTENT_EXTRAS_KEY_CLASS
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class AdBannerFragment : Fragment() {

    @Inject
    lateinit var adsHelper: AdsHelper

    @Inject
    @IoDispatcher
    lateinit var ioDispatcher: CoroutineDispatcher

    @Inject
    @MainDispatcher
    lateinit var mainDispatcher: CoroutineDispatcher

    private var _binding: FragmentAdbannerBinding? = null
    private val binding get() = _binding!!

    private var adView: AdView? = null

    private lateinit var job: Job
    private var parentActivityName: String? = null

    private val receiverADClick = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == null
                || intent.action != ACTION_BANNER_CLICK
                || intent.getStringExtra(INTENT_EXTRAS_KEY_CLASS) == parentActivityName
            ) {
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        parentActivityName = requireActivity().javaClass.simpleName
        setupAdView()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupAdView() {

        adView = AdView(requireContext())
        binding.adViewContainer.addView(adView)
        val bannerId: String = getString(R.string.admob_banner_ad_unit_id)
        adView?.adUnitId = bannerId

        if (adsHelper.isIntervalOK) {
            binding.adViewContainer.visibility = View.VISIBLE
            loadBanner()
        } else {
            startInterval(false)
        }

        adView?.let {
            it.adListener = object : AdListener() {
                override fun onAdOpened() {
                    Timber.d("test!!!!!onAdOpened")
                    startInterval(true)
                }
            }
        }

        setADClickReceiver()
    }

    private fun loadBanner() {
        val adSize: AdSize = getAdSize()
        adView!!.adSize = adSize

        // Start loading the ad in the background.
        adView!!.loadAd(adsHelper.adRequest)
    }

    private fun getAdSize(): AdSize {

        // Determine the screen width to use for the ad width.
        val outMetrics = DisplayMetrics()

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            val display = activity?.display
            display?.getRealMetrics(outMetrics)
        } else {
            @Suppress("DEPRECATION")
            val display = activity?.windowManager?.defaultDisplay
            if (display != null) {
                @Suppress("DEPRECATION")
                display.getMetrics(outMetrics)
            }
        }
        val widthPixels = outMetrics.widthPixels.toFloat()
        val density = outMetrics.density

        // you can also pass your selected width here in dp
        val adWidth = (widthPixels / density).toInt()

        // return the optimal size depends on your orientation (landscape or portrait)
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(requireContext(), adWidth)
    }

    private fun setADClickReceiver() {
        val filter = IntentFilter()
        filter.addAction(ACTION_BANNER_CLICK)
        LocalBroadcastManager.getInstance(requireContext())
            .registerReceiver(receiverADClick, filter)
    }

    private fun startInterval(clicked: Boolean) {
        binding.adViewContainer.visibility = View.GONE

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
        withContext(ioDispatcher) {
            delay(intervalTimeMillis)
            withContext(mainDispatcher) {
                setupAdView()
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
