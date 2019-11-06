package com.sakurafish.pockettoushituryou.view.fragment;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sakurafish.pockettoushituryou.databinding.FragmentSplashBinding;

public class SplashFragment extends Fragment {
    public static final String TAG = SplashFragment.class.getSimpleName();

    private FragmentSplashBinding binding;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSplashBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
