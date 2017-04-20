package com.sakurafish.pockettoushituryou.di;

import android.app.Application;
import android.content.Context;

import com.google.gson.GsonBuilder;
import com.sakurafish.pockettoushituryou.R;
import com.sakurafish.pockettoushituryou.api.DummyDataService;
import com.sakurafish.pockettoushituryou.api.PocketCarboService;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.reactivex.disposables.CompositeDisposable;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

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
    public CompositeDisposable provideCompositeDisposable() {
        return new CompositeDisposable();
    }

    @Singleton
    @Provides
    public OkHttpClient provideHttpClient(Context context) {
        // header
        Interceptor header = chain -> {
            Request original = chain.request();
            Request.Builder req = original.newBuilder()
                    .header("Authorization", context.getString(R.string.pocketcarbo_http_header));
            return chain.proceed(req.build());
        };

        // logging
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC);

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(header)
                .addInterceptor(loggingInterceptor)
                .build();

        return okHttpClient;
    }

    @Singleton
    @Provides
    public PocketCarboService providePocketCarboService(OkHttpClient client) {
        return new Retrofit.Builder().client(client)
                .baseUrl("http://www.pockettoushituryou.com")
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(new GsonBuilder().serializeNulls().create()))
                .build()
                .create(PocketCarboService.class);
    }

//    TODO
    @Singleton
    @Provides
    public DummyDataService provideDummyDataService(OkHttpClient client) {
        return new Retrofit.Builder().client(client)
                .baseUrl("https://sakurabird1-grape-example.herokuapp.com")
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(new GsonBuilder().serializeNulls().create()))
                .build()
                .create(DummyDataService.class);
    }
}
