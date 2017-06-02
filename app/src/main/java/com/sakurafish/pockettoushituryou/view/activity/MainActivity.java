package com.sakurafish.pockettoushituryou.view.activity;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.gson.Gson;
import com.sakurafish.pockettoushituryou.R;
import com.sakurafish.pockettoushituryou.databinding.ActivityMainBinding;
import com.sakurafish.pockettoushituryou.model.TypesData;
import com.sakurafish.pockettoushituryou.view.helper.ResourceResolver;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import br.com.mauker.materialsearchview.MaterialSearchView;
import timber.log.Timber;

import static com.sakurafish.pockettoushituryou.view.fragment.FoodListFragment.ListType;
import static com.sakurafish.pockettoushituryou.view.fragment.FoodListFragment.newInstance;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    ActivityMainBinding binding;

    @Inject
    ResourceResolver resourceResolver;

    public static Intent createIntent(Context context) {
        return new Intent(context, MainActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Timber.tag(TAG).d("onCreate");

        getComponent().inject(this);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        initView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                binding.searchView.openSearch();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void initView() {
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setTitle(R.string.list_title);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, binding.drawerLayout, binding.toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        binding.drawerLayout.setDrawerListener(toggle);
        toggle.syncState();

        binding.navView.setNavigationItemSelectedListener(this);

        // 音声入力による検索を行わない
        binding.searchView.setVoiceIcon(0);

        binding.searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (query.replaceAll("　", " ").trim().isEmpty()) {
                    Toast.makeText(MainActivity.this, getString(R.string.action_search_hint), Toast.LENGTH_SHORT).show();
                    return true;
                }
                startActivity(SearchResultActivity.createIntent(MainActivity.this, query));
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        binding.searchView.setOnItemClickListener((parent, view, position, id) -> {
            // the suggestion list is clicked.
            String suggestion = binding.searchView.getSuggestionAtPosition(position);
            binding.searchView.setQuery(suggestion, false);
        });

        initTabs();
    }

    private void initTabs() {

        final TabLayout.TabLayoutOnPageChangeListener pageChangeListener =
                new TabLayout.TabLayoutOnPageChangeListener(binding.tabLayout);
        binding.pager.addOnPageChangeListener(pageChangeListener);
        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                binding.pager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        final MyPagerAdapter adapter = new MyPagerAdapter(getSupportFragmentManager());

        final String json = resourceResolver.loadJSONFromAsset(resourceResolver.getString(R.string.types_file));
        final Gson gson = new Gson();
        TypesData typesData = gson.fromJson(json, TypesData.class);

        for (TypesData.Types type : typesData.types) {
            binding.tabLayout.addTab(binding.tabLayout.newTab().setText(type.name), false);
            adapter.addFragment(newInstance(ListType.NORMAL, type.id),
                    type.name);
        }

        binding.pager.setAdapter(adapter);

        final int curItem = binding.pager.getCurrentItem();
        if (curItem != binding.tabLayout.getSelectedTabPosition() && curItem < binding.tabLayout.getTabCount()) {
            binding.tabLayout.getTabAt(curItem).select();
        }
    }

    @Override
    public void onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
        } else if (binding.searchView.isOpen()) {
            binding.searchView.closeSearch();
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        switch (item.getItemId()) {
            case R.id.nav_home:
                // Do nothing
                break;
            case R.id.nav_favorite:
                startActivity(FavoritesActivity.createIntent(MainActivity.this));
                break;
            case R.id.nav_setting:
                startActivity(SettingsActivity.createIntent(MainActivity.this));
                break;
            case R.id.nav_share:
                final Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_text));
                intent.setType("text/plain");
                startActivity(intent);
                break;
            case R.id.nav_help:
                break;
        }

        binding.drawerLayout.closeDrawer(GravityCompat.START);
        return false;
    }

    private static class MyPagerAdapter extends FragmentStatePagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}
