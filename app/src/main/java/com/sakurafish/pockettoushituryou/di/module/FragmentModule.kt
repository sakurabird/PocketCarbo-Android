package com.sakurafish.pockettoushituryou.di.module

import androidx.lifecycle.ViewModel
import com.sakurafish.pockettoushituryou.di.ViewModelKey
import com.sakurafish.pockettoushituryou.view.fragment.*
import com.sakurafish.pockettoushituryou.viewmodel.*
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

@Module
internal abstract class FragmentModule {

    @Binds
    @IntoMap
    @ViewModelKey(FoodsViewModel::class)
    abstract fun bindFoodsViewModel(viewModel: FoodsViewModel): ViewModel

    @ContributesAndroidInjector
    internal abstract fun contributeFoodsFragment(): FoodsFragment

    @Binds
    @IntoMap
    @ViewModelKey(FavoritesViewModel::class)
    abstract fun bindFavoritesViewModel(viewModel: FavoritesViewModel): ViewModel

    @ContributesAndroidInjector
    internal abstract fun contributeFavoritesFragment(): FavoritesFragment

    @Binds
    @IntoMap
    @ViewModelKey(SearchResultViewModel::class)
    abstract fun bindSearchResultViewModel(viewModel: SearchResultViewModel): ViewModel

    @ContributesAndroidInjector
    internal abstract fun contributeSearchResultFragment(): SearchResultFragment

    @Binds
    @IntoMap
    @ViewModelKey(HelpViewModel::class)
    abstract fun bindHelpViewModel(viewModel: HelpViewModel): ViewModel

    @ContributesAndroidInjector
    internal abstract fun contributeHelpFragment(): HelpFragment

    @Binds
    @IntoMap
    @ViewModelKey(WebViewViewModel::class)
    abstract fun bindWebViewViewModel(viewModel: WebViewViewModel): ViewModel

    @ContributesAndroidInjector
    internal abstract fun contributeWebViewFragment(): WebViewFragment

    @ContributesAndroidInjector
    internal abstract fun contributeAdBannerFragment(): AdBannerFragment
}