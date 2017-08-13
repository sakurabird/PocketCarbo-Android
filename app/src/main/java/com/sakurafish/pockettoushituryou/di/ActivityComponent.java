package com.sakurafish.pockettoushituryou.di;


import com.sakurafish.pockettoushituryou.view.activity.FavoritesActivity;
import com.sakurafish.pockettoushituryou.view.activity.HelpActivity;
import com.sakurafish.pockettoushituryou.view.activity.MainActivity;
import com.sakurafish.pockettoushituryou.view.activity.SearchResultActivity;
import com.sakurafish.pockettoushituryou.view.activity.SplashActivity;

import dagger.Subcomponent;

@Subcomponent(modules = ActivityModule.class)
public interface ActivityComponent {

    void inject(SplashActivity activity);

    void inject(MainActivity activity);

    void inject(SearchResultActivity activity);

    void inject(FavoritesActivity activity);

    void inject(HelpActivity activity);

    FragmentComponent plus(FragmentModule module);
}
