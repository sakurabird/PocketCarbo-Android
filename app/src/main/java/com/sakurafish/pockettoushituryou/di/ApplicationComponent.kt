package com.sakurafish.pockettoushituryou.di

import android.app.Application
import android.content.Context
import com.sakurafish.pockettoushituryou.MainApplication
import com.sakurafish.pockettoushituryou.di.module.ActivityModule
import com.sakurafish.pockettoushituryou.di.module.FragmentModule
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

        fun applicationModule(applicationModule: ApplicationModule): Builder

        fun build(): ApplicationComponent
    }

    override fun inject(app: MainApplication)
}