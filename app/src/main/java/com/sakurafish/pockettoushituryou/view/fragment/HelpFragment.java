package com.sakurafish.pockettoushituryou.view.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.sakurafish.pockettoushituryou.R;
import com.sakurafish.pockettoushituryou.databinding.FragmentHelpBinding;
import com.sakurafish.pockettoushituryou.di.Injectable;
import com.sakurafish.pockettoushituryou.viewmodel.HelpViewModel;

import javax.inject.Inject;

public class HelpFragment extends Fragment implements Injectable {
    public static final String TAG = HelpFragment.class.getSimpleName();

    private FragmentHelpBinding binding;

    @Inject
    HelpViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_help, container, false);
        binding.setViewModel(viewModel);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
