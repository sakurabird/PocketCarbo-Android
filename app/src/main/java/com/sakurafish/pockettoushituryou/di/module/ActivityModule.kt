package com.sakurafish.pockettoushituryou.di.module

import androidx.lifecycle.ViewModelProvider
import com.sakurafish.pockettoushituryou.di.ViewModelFactory
import com.sakurafish.pockettoushituryou.view.activity.*
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
internal abstract class ActivityModule {

    @Binds
    abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory

    @ContributesAndroidInjector
    internal abstract fun contributeSplashActivity(): SplashActivity

    @ContributesAndroidInjector
    internal abstract fun contributeMainActivity(): MainActivity

    @ContributesAndroidInjector
    internal abstract fun contributeSearchResultActivity(): SearchResultActivity

    @ContributesAndroidInjector
    internal abstract fun contributeFavoritesActivity(): FavoritesActivity

    @ContributesAndroidInjector
    internal abstract fun contributeHelpActivity(): HelpActivity
}