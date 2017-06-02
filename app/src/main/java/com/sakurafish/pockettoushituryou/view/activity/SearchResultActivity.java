package com.sakurafish.pockettoushituryou.view.activity;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;

import com.sakurafish.pockettoushituryou.R;
import com.sakurafish.pockettoushituryou.databinding.ActivitySearchresultBinding;
import com.sakurafish.pockettoushituryou.view.fragment.FoodListFragment;
import com.sakurafish.pockettoushituryou.view.fragment.FoodListFragment.ListType;

public class SearchResultActivity extends BaseActivity {

    private static final String TAG = SearchResultActivity.class.getSimpleName();
    public static final String EXTRA_QUERY = "query";

    ActivitySearchresultBinding binding;

    public static Intent createIntent(Context context, String query) {
        Intent intent = new Intent(context, SearchResultActivity.class);
        intent.putExtra(EXTRA_QUERY, query);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final String query = getIntent().getStringExtra(EXTRA_QUERY);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_searchresult);
        initView();
        replaceFragment(FoodListFragment.newInstance(ListType.SEARCH_RESULT, query), R.id.content_view);
    }

    private void initView() {
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.search_result_title) + " (" + getIntent().getStringExtra(EXTRA_QUERY) + ")");
        binding.toolbar.setNavigationOnClickListener(view -> onBackPressed());
    }
}
