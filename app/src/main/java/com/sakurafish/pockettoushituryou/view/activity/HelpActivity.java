package com.sakurafish.pockettoushituryou.view.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;

import com.sakurafish.pockettoushituryou.R;
import com.sakurafish.pockettoushituryou.databinding.ActivityHelpBinding;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasAndroidInjector;

public class HelpActivity extends BaseActivity implements HasAndroidInjector {

    private static final String TAG = HelpActivity.class.getSimpleName();

    @Inject
    DispatchingAndroidInjector<Object> androidInjector;

    ActivityHelpBinding binding;

    public static Intent createIntent(Context context) {
        return new Intent(context, HelpActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_help);
        initView();
    }

    private void initView() {
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.help));
        binding.toolbar.setNavigationOnClickListener(view -> onBackPressed());
    }

    @Override
    public AndroidInjector<Object> androidInjector() {
        return androidInjector;
    }
}
