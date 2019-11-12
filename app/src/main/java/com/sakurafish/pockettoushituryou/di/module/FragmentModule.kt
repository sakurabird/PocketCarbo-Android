package com.sakurafish.pockettoushituryou.di.module

import com.sakurafish.pockettoushituryou.view.fragment.AdBannerFragment
import com.sakurafish.pockettoushituryou.view.fragment.FoodListFragment
import com.sakurafish.pockettoushituryou.view.fragment.HelpFragment
import com.sakurafish.pockettoushituryou.view.fragment.WebViewFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
internal abstract class FragmentModule {
    @ContributesAndroidInjector
    internal abstract fun contributeFoodListFragment(): FoodListFragment

    @ContributesAndroidInjector
    internal abstract fun contributeHelpFragment(): HelpFragment

    @ContributesAndroidInjector
    internal abstract fun contributeWebViewFragment(): WebViewFragment

    @ContributesAndroidInjector
    internal abstract fun contributeAdBannerFragment(): AdBannerFragment
}