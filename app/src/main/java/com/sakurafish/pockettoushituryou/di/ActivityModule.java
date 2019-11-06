package com.sakurafish.pockettoushituryou.di;

import androidx.appcompat.app.AppCompatActivity;

import dagger.Module;
import dagger.Provides;

@Module
public class ActivityModule {

    final AppCompatActivity activity;

    public ActivityModule(AppCompatActivity activity) {
        this.activity = activity;
    }

    @Provides
    public AppCompatActivity activity() {
        return activity;
    }
}
