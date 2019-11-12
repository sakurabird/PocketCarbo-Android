package com.sakurafish.pockettoushituryou.view.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.databinding.DataBindingUtil;

import com.sakurafish.pockettoushituryou.R;
import com.sakurafish.pockettoushituryou.databinding.ActivityFavoritesBinding;
import com.sakurafish.pockettoushituryou.view.fragment.FoodListFragment;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasAndroidInjector;

public class FavoritesActivity extends BaseActivity implements HasAndroidInjector {

    private static final String TAG = FavoritesActivity.class.getSimpleName();

    @Inject
    DispatchingAndroidInjector<Object> androidInjector;

    ActivityFavoritesBinding binding;

    public static Intent createIntent(Context context) {
        return new Intent(context, FavoritesActivity.class);
    }

    static {
        // For Android 5.0 (API level 21) below (https://stackoverflow.com/a/38012842)
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_favorites);
        initView();
        replaceFragment(FoodListFragment.newInstance(FoodListFragment.ListType.FAVORITES), R.id.content_view);
    }

    private void initView() {
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.favorite));
        binding.toolbar.setNavigationOnClickListener(view -> onBackPressed());
    }

    @Override
    public AndroidInjector<Object> androidInjector() {
        return androidInjector;
    }
}
