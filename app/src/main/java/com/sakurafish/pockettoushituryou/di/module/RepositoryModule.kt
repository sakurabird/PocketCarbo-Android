package com.sakurafish.pockettoushituryou.di.module

import com.sakurafish.pockettoushituryou.repository.*
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Singleton
    @Binds
    abstract fun bindFoodRepository(impl: FoodDataSource): FoodRepository

    @Singleton
    @Binds
    abstract fun bindKindRepository(impl: KindDataSource): KindRepository

    @Singleton
    @Binds
    abstract fun bindFavoriteRepository(impl: FavoriteDataSource): FavoriteRepository
}
