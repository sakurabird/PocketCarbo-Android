package com.sakurafish.pockettoushituryou.view.fragment;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.github.jinatonic.confetti.ConfettiManager;
import com.github.jinatonic.confetti.ConfettiSource;
import com.github.jinatonic.confetti.ConfettoGenerator;
import com.github.jinatonic.confetti.confetto.BitmapConfetto;
import com.github.jinatonic.confetti.confetto.Confetto;
import com.sakurafish.pockettoushituryou.R;
import com.sakurafish.pockettoushituryou.databinding.FragmentSplashBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SplashFragment extends Fragment implements ConfettoGenerator {
    public static final String TAG = SplashFragment.class.getSimpleName();

    private FragmentSplashBinding binding;

    private int size;
    private int velocitySlow, velocityNormal;
    private Bitmap bitmap;
    private final List<ConfettiManager> activeConfettiManagers = new ArrayList<>();
//    protected ViewGroup container;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSplashBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final Resources res = getResources();
        size = res.getDimensionPixelSize(R.dimen.splash_bg_img);
        velocitySlow = res.getDimensionPixelOffset(R.dimen.default_velocity_slow);
        velocityNormal = res.getDimensionPixelOffset(R.dimen.default_velocity_normal);

        bitmap = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(res, R.drawable.img_splash_bg),
                size, size, false);

        binding.container.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
        activeConfettiManagers.add(generateInfinite());
//                CommonConfetti.rainingConfetti(binding.container, new int[]{Color.BLACK})
//                        .infinite();
            }
        });
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        container = (ViewGroup) findViewById(R.id.container);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    private ConfettiManager getConfettiManager() {
        final ConfettiSource source = new ConfettiSource(0, -size, binding.container.getWidth(), -size);
        return new ConfettiManager(getActivity(), this, source, binding.container)
                .setVelocityX(0, velocitySlow)
                .setVelocityY(velocityNormal, velocitySlow)
                .setRotationalVelocity(180, 90)
                .setTouchEnabled(true);
    }

    protected ConfettiManager generateInfinite() {
        return getConfettiManager().setNumInitialCount(10)
                .setEmissionDuration(ConfettiManager.INFINITE_DURATION)
                .setEmissionRate(20)
                .animate();
//        return getConfettiManager().setNumInitialCount(0)
//                .setEmissionDuration(ConfettiManager.INFINITE_DURATION)
//                .setEmissionRate(20)
//                .setConfettiAnimationListener(this)
//                .animate();
    }

    @Override
    public Confetto generateConfetto(Random random) {
        return new BitmapConfetto(bitmap);
    }
}
