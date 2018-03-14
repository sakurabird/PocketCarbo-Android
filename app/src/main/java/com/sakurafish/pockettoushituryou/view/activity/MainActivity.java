package com.sakurafish.pockettoushituryou.view.activity;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.google.gson.Gson;
import com.sakurafish.pockettoushituryou.BuildConfig;
import com.sakurafish.pockettoushituryou.R;
import com.sakurafish.pockettoushituryou.databinding.ActivityMainBinding;
import com.sakurafish.pockettoushituryou.model.TypesData;
import com.sakurafish.pockettoushituryou.pref.Pref;
import com.sakurafish.pockettoushituryou.repository.ReleasedVersionRepository;
import com.sakurafish.pockettoushituryou.rxbus.EventWithMessage;
import com.sakurafish.pockettoushituryou.rxbus.RxBus;
import com.sakurafish.pockettoushituryou.view.helper.AdsHelper;
import com.sakurafish.pockettoushituryou.view.helper.ResourceResolver;
import com.sakurafish.pockettoushituryou.view.helper.ShowcaseHelper;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import br.com.mauker.materialsearchview.MaterialSearchView;
import timber.log.Timber;
import uk.co.deanwild.materialshowcaseview.IShowcaseListener;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;

import static com.sakurafish.pockettoushituryou.view.fragment.FoodListFragment.ListType;
import static com.sakurafish.pockettoushituryou.view.fragment.FoodListFragment.newInstance;
import static com.sakurafish.pockettoushituryou.view.helper.ShowcaseHelper.EVENT_SHOWCASE_MAINACTIVITY_FINISHED;
import static com.sakurafish.pockettoushituryou.view.helper.ShowcaseHelper.SHOWCASE_DELAY;
import static com.sakurafish.pockettoushituryou.view.helper.ShowcaseHelper.SHOWCASE_ID_MAINACTIVITY;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    ActivityMainBinding binding;

    @Inject
    ResourceResolver resourceResolver;

    @Inject
    Pref pref;

    @Inject
    ReleasedVersionRepository releasedVersionRepository;

    @Inject
    ShowcaseHelper showcaseHelper;

    @Inject
    AdsHelper adsHelper;

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

        releasedVersionRepository.checkReleasedVersion();

        pleaseReview();
    }


    private void pleaseReview() {
        if (pref.getPrefBool(getString(R.string.PREF_ASK_REVIEW), false) || pref.getPrefInt(getString(R.string.PREF_LAUNCH_COUNT)) != 10) {
            return;
        }
        //10回めの起動でレビュー誘導
        new MaterialDialog.Builder(this)
                .theme(Theme.LIGHT)
                .title(getString(R.string.ask_review_title))
                .content(getString(R.string.ask_review_message))
                .positiveText(getString(android.R.string.ok))
                .negativeText(getString(android.R.string.cancel))
                .onPositive((dialog, which) -> {
                    // Google Play
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.app_url))));
                })
                .show();
        pref.setPref(getString(R.string.PREF_ASK_REVIEW), true);
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);

        showTutorialOnce();
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

        View headerView = binding.navView.getHeaderView(0);
        headerView.setOnClickListener(view -> goBrowser("http://www.pockettoushituryou.com"));

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, binding.drawerLayout, binding.toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        binding.drawerLayout.addDrawerListener(toggle);
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

        // 広告
        if (!BuildConfig.DEBUG) {
            binding.adView.loadAd(adsHelper.getAdRequest());
        }
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

    public int getCurrentPagerPosition() {
        return binding.pager.getCurrentItem();
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
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
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
            case R.id.nav_announcement:
                startActivity(WebViewActivity.createIntent(MainActivity.this
                        , "file:///android_asset/www/announcement.html"
                        , getString(R.string.announcement)));
                break;
            case R.id.nav_share:
                final Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_text));
                intent.setType("text/plain");
                startActivity(intent);
                break;
            case R.id.nav_help:
                startActivity(HelpActivity.createIntent(MainActivity.this));
                break;
        }

        binding.drawerLayout.closeDrawer(GravityCompat.START);
        return false;
    }

    private void showTutorialOnce() {
        new Handler().post(() -> {
            ShowcaseConfig config = new ShowcaseConfig();
            config.setDelay(SHOWCASE_DELAY);

            MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(MainActivity.this, SHOWCASE_ID_MAINACTIVITY);
            sequence.setConfig(config);

            // Menu tutorial
            View drawerIcon = binding.toolbar.getChildAt(1);
            sequence.addSequenceItem(
                    new MaterialShowcaseView.Builder(MainActivity.this)
                            .setTarget(drawerIcon)
                            .setContentText(R.string.tutorial_nav_text)
                            .setDismissText(android.R.string.ok)
                            .setDismissOnTouch(true)
                            .build()
            );

            // Search tutorial
            View searchIcon = findViewById(R.id.action_search);
            sequence.addSequenceItem(
                    new MaterialShowcaseView.Builder(MainActivity.this)
                            .setTarget(searchIcon)
                            .setContentText(R.string.tutorial_search_text)
                            .setDismissText(android.R.string.ok)
                            .setDismissOnTouch(true)
                            .build()
            );

            // Tab tutorial
            sequence.addSequenceItem(
                    new MaterialShowcaseView.Builder(MainActivity.this)
                            .setTarget(binding.tabLayout)
                            .setContentText(R.string.tutorial_tab_text)
                            .setDismissText(android.R.string.ok)
                            .withRectangleShape(true)
                            .setListener(new IShowcaseListener() {
                                @Override
                                public void onShowcaseDisplayed(MaterialShowcaseView materialShowcaseView) {

                                }

                                @Override
                                public void onShowcaseDismissed(MaterialShowcaseView materialShowcaseView) {
                                    showcaseHelper.setPrefShowcaseMainactivityFinished(true);
                                    RxBus rxBus = RxBus.getIntanceBus();
                                    rxBus.post(new EventWithMessage(EVENT_SHOWCASE_MAINACTIVITY_FINISHED));

                                }
                            })
                            .setDismissOnTouch(true)
                            .build()
            );
            sequence.start();
        });
    }

    private static class MyPagerAdapter extends FragmentStatePagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        void addFragment(Fragment fragment, String title) {
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
