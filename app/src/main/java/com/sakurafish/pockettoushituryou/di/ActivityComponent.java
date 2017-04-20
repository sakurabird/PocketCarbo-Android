package com.sakurafish.pockettoushituryou.di;


import com.sakurafish.pockettoushituryou.view.activity.MainActivity;

import dagger.Subcomponent;

@Subcomponent(modules = ActivityModule.class)
public interface ActivityComponent {

    void inject(MainActivity activity);

    FragmentComponent plus(FragmentModule module);
}
