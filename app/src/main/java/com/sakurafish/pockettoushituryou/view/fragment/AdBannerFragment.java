package com.sakurafish.pockettoushituryou.view.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.ads.AdListener;
import com.sakurafish.pockettoushituryou.databinding.FragmentAdbannerBinding;
import com.sakurafish.pockettoushituryou.di.Injectable;
import com.sakurafish.pockettoushituryou.view.helper.AdsHelper;

import javax.inject.Inject;

import static com.sakurafish.pockettoushituryou.view.helper.AdsHelper.ACTION_BANNER_CLICK;
import static com.sakurafish.pockettoushituryou.view.helper.AdsHelper.INTENT_EXTRAS_KEY_CLASS;

public class AdBannerFragment extends Fragment implements Injectable {
    private static final String TAG = AdBannerFragment.class.getSimpleName();

    private FragmentAdbannerBinding binding;
    private final Handler handler = new Handler();
    private Runnable runnable;
    private String parentActivityName;

    private BroadcastReceiver receiverADClick = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() == null
                    || !intent.getAction().equals(ACTION_BANNER_CLICK)
                    || intent.getStringExtra(INTENT_EXTRAS_KEY_CLASS).equals(parentActivityName)) {
                return;
            }
            finishInterval();
            startInterval(false);
        }
    };

    @Inject
    AdsHelper adsHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAdbannerBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        parentActivityName = getActivity().getClass().getSimpleName();
        setupAdView();
    }

    public void setupAdView() {
        if (adsHelper.isIntervalOK()) {
            binding.adView.setVisibility(View.VISIBLE);
            binding.adView.loadAd(adsHelper.getAdRequest());
        } else {
            startInterval(false);
        }

        binding.adView.setAdListener(new AdListener() {
            @Override
            public void onAdOpened() {
                startInterval(true);
            }
        });

        setADClickReceiver();
    }

    private void setADClickReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_BANNER_CLICK);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(receiverADClick, filter);
    }

    private void startInterval(final boolean clicked) {
        binding.adView.setVisibility(View.GONE);

        long now = System.currentTimeMillis();

        if (clicked) {
            adsHelper.setLastClickTimeMillis(now);
            Intent localIntent = new Intent(ACTION_BANNER_CLICK);
            localIntent.putExtra(INTENT_EXTRAS_KEY_CLASS, parentActivityName);
            LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(localIntent);
        }
        long intervalTimeMillis = adsHelper.getIntervalTimeMillis(now, adsHelper.getLastClickTimeMillis());

        runnable = () -> {
            binding.adView.setVisibility(View.VISIBLE);
            binding.adView.loadAd(adsHelper.getAdRequest());
        };
        handler.postDelayed(runnable, intervalTimeMillis);
    }

    private void finishInterval() {
        handler.removeCallbacks(runnable);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        finishInterval();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(receiverADClick);
    }
}
