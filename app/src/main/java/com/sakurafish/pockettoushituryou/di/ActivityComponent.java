package com.sakurafish.pockettoushituryou.di;


import com.sakurafish.pockettoushituryou.view.activity.MainActivity;
import com.sakurafish.pockettoushituryou.view.activity.SplashActivity;

import dagger.Subcomponent;

@Subcomponent(modules = ActivityModule.class)
public interface ActivityComponent {

    void inject(SplashActivity activity);

    void inject(MainActivity activity);

    FragmentComponent plus(FragmentModule module);
}
