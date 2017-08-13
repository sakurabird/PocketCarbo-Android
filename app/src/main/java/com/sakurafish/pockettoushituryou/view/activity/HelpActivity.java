package com.sakurafish.pockettoushituryou.view.activity;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;

import com.sakurafish.pockettoushituryou.R;
import com.sakurafish.pockettoushituryou.databinding.ActivityHelpBinding;

public class HelpActivity extends BaseActivity {

    private static final String TAG = HelpActivity.class.getSimpleName();

    ActivityHelpBinding binding;

    public static Intent createIntent(Context context) {
        return new Intent(context, HelpActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getComponent().inject(this);
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
}
