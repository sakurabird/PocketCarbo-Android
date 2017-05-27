package com.sakurafish.pockettoushituryou.di;

import com.sakurafish.pockettoushituryou.view.fragment.FoodListFragment;
import com.sakurafish.pockettoushituryou.view.fragment.WebViewFragment;

import dagger.Subcomponent;

@Subcomponent(modules = FragmentModule.class)
public interface FragmentComponent {

    void inject(FoodListFragment fragment);

    void inject(WebViewFragment fragment);
}
