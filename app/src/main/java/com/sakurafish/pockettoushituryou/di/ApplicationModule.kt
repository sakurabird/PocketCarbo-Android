package com.sakurafish.pockettoushituryou.di

import android.app.Application
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.sakurafish.pockettoushituryou.data.db.entity.orma.OrmaDatabase
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Singleton

@Module
class ApplicationModule(val application: Application) {

    @Provides
    fun provideSharedPreferences(): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(application)
    }

    @Provides
    fun provideCompositeDisposable(): CompositeDisposable {
        return CompositeDisposable()
    }

    @Singleton
    @Provides
    fun provideOrmaDatabase(): OrmaDatabase {
        return OrmaDatabase.builder(application).trace(false).build()
    }

    @Provides
    fun provideMoshi(): Moshi {
        val moshi = Moshi.Builder()
            .addLast(KotlinJsonAdapterFactory())
            .build()
        return moshi
    }
}
