package com.sakurafish.pockettoushituryou.di

import android.app.Application
import android.content.Context
import com.sakurafish.pockettoushituryou.MainApplication
import com.sakurafish.pockettoushituryou.data.db.dao.FavoriteDao
import com.sakurafish.pockettoushituryou.data.db.dao.FoodDao
import com.sakurafish.pockettoushituryou.data.db.dao.KindDao
import com.sakurafish.pockettoushituryou.di.module.ActivityModule
import com.sakurafish.pockettoushituryou.di.module.DatabaseModule
import com.sakurafish.pockettoushituryou.di.module.FragmentModule
import com.sakurafish.pockettoushituryou.repository.FavoriteRepository
import com.sakurafish.pockettoushituryou.repository.FoodRepository
import com.sakurafish.pockettoushituryou.repository.KindRepository
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@Component(
        modules = [
            AndroidSupportInjectionModule::class,
            ApplicationModule::class,
            DatabaseModule::class,
            ActivityModule::class,
            FragmentModule::class
        ]
)
interface ApplicationComponent : AndroidInjector<MainApplication> {

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder

        @BindsInstance
        fun context(context: Context): Builder

        fun databaseModule(databaseModule: DatabaseModule): Builder

        fun applicationModule(applicationModule: ApplicationModule): Builder

        fun build(): ApplicationComponent
    }

    override fun inject(app: MainApplication)

    fun foodDao(): FoodDao
    fun foodRepository(): FoodRepository
    fun kindDao(): KindDao
    fun kindRepository(): KindRepository
    fun favoriteDao(): FavoriteDao
    fun favoriteRepository(): FavoriteRepository
}