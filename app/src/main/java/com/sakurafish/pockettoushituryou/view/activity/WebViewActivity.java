package com.sakurafish.pockettoushituryou.view.activity;

import android.content.Context;
import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.annotation.NonNull;

import com.sakurafish.pockettoushituryou.R;
import com.sakurafish.pockettoushituryou.databinding.ActivityWebviewBinding;
import com.sakurafish.pockettoushituryou.view.fragment.WebViewFragment;

public class WebViewActivity extends BaseActivity {

    private static final String TAG = WebViewActivity.class.getSimpleName();
    public static final String EXTRA_URL = "url";
    public static final String EXTRA_TITLE = "title";

    ActivityWebviewBinding binding;

    public static Intent createIntent(@NonNull Context context, @NonNull String url, @NonNull String title) {
        Intent intent = new Intent(context, WebViewActivity.class);
        intent.putExtra(EXTRA_URL, url);
        intent.putExtra(EXTRA_TITLE, title);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final String url = getIntent().getStringExtra(EXTRA_URL);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_webview);
        initView();
        replaceFragment(WebViewFragment.newInstance(url), R.id.content_view);
    }

    private void initView() {
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(getIntent().getStringExtra(EXTRA_TITLE));
        binding.toolbar.setNavigationOnClickListener(view -> onBackPressed());
    }
}
