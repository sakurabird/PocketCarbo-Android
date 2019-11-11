package com.sakurafish.pockettoushituryou.di;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.sakurafish.pockettoushituryou.data.db.entity.OrmaDatabase;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.reactivex.disposables.CompositeDisposable;

@Module
public class ApplicationModule {
    private final Application application;

    public ApplicationModule(Application application) {
        this.application = application;
    }

    @Provides
    @Singleton
    Context provideApplicationContext() {
        return application.getApplicationContext();
    }

    @Provides
    public SharedPreferences provideSharedPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Provides
    public CompositeDisposable provideCompositeDisposable() {
        return new CompositeDisposable();
    }

    @Singleton
    @Provides
    public OrmaDatabase provideOrmaDatabase(Context context) {
        return OrmaDatabase.builder(context).trace(false).build();
    }
}
