package com.sakurafish.pockettoushituryou.di;

import androidx.fragment.app.FragmentManager;

import com.sakurafish.pockettoushituryou.view.fragment.BaseFragment;

import dagger.Module;
import dagger.Provides;

@Module
public class FragmentModule {

    private final BaseFragment fragment;

    public FragmentModule(BaseFragment fragment) {
        this.fragment = fragment;
    }

    @Provides
    public BaseFragment provideFragment() {
        return fragment;
    }

    @Provides
    public FragmentManager provideFragmentManager() {
        return fragment.getFragmentManager();
    }

}
