package com.sakurafish.pockettoushituryou.view.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.sakurafish.pockettoushituryou.R;
import com.sakurafish.pockettoushituryou.data.local.LocalJsonResolver;
import com.sakurafish.pockettoushituryou.data.local.Type;
import com.sakurafish.pockettoushituryou.data.local.TypesData;
import com.sakurafish.pockettoushituryou.databinding.ActivityMainBinding;
import com.sakurafish.pockettoushituryou.pref.Pref;
import com.sakurafish.pockettoushituryou.rxbus.EventWithMessage;
import com.sakurafish.pockettoushituryou.rxbus.RxBus;
import com.sakurafish.pockettoushituryou.util.AlarmUtils;
import com.sakurafish.pockettoushituryou.view.customview.MaterialSearchView;
import com.sakurafish.pockettoushituryou.view.helper.ShowcaseHelper;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasAndroidInjector;
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

public class MainActivity extends BaseActivity implements HasAndroidInjector, NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    ActivityMainBinding binding;

    @Inject
    DispatchingAndroidInjector<Object> androidInjector;

    @Inject
    Moshi moshi;

    @Inject
    Pref pref;

    @Inject
    ShowcaseHelper showcaseHelper;

    public static Intent createIntent(Context context) {
        return new Intent(context, MainActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Timber.tag(TAG).d("onCreate");

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        initView();

        pleaseReview();

        showAppMessage();
    }

    private void pleaseReview() {
        if (pref.getPrefBool(getString(R.string.PREF_ASK_REVIEW), false) || pref.getPrefInt(getString(R.string.PREF_LAUNCH_COUNT)) != 10) {
            return;
        }
        //10回めの起動でレビュー誘導
        // TODO Rewrite with Kotlin
        MaterialDialog dialog = new MaterialDialog(this, MaterialDialog.getDEFAULT_BEHAVIOR());
        dialog.title(null, getString(R.string.ask_review_title));
        dialog.message(null, getString(R.string.ask_review_message), null);
        dialog.positiveButton(null, getString(android.R.string.ok), materialDialog -> {
            // Google Play
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.app_url))));
            return null;
        });
        dialog.show();

        pref.setPref(getString(R.string.PREF_ASK_REVIEW), true);
    }

    private void showAppMessage() {
        scheduleNotification();
        final int lastNo = pref.getPrefInt(getString(R.string.PREF_APP_MESSAGE_NO));
        int messageNo = getResources().getInteger(R.integer.APP_MESSAGE_NO);
        String messageText = getString(R.string.APP_MESSAGE_TEXT);

        if (messageNo <= lastNo) {
            return;
        }

        // インストール時点のメッセージは表示しない
        if (pref.getPrefInt(getString(R.string.PREF_LAUNCH_COUNT)) <= 1) {
            pref.setPref(getString(R.string.PREF_APP_MESSAGE_NO), messageNo);
            return;
        }
        Timber.tag(TAG).d("no:" + messageNo + " message:" + messageText);

        // TODO Rewrite with Kotlin
        MaterialDialog dialog = new MaterialDialog(this, MaterialDialog.getDEFAULT_BEHAVIOR());
        dialog.title(null, getString(R.string.announcement));
        dialog.message(null, messageText, null);
        dialog.positiveButton(null, getString(android.R.string.ok), materialDialog -> null);
        dialog.show();

        pref.setPref(getString(R.string.PREF_APP_MESSAGE_NO), messageNo);
    }

    private void scheduleNotification() {
        AlarmUtils.unregisterAlarm(this);
        AlarmUtils.registerAlarm(this);
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
        final String json;
        try {
            json = LocalJsonResolver.loadJsonFromAsset(this, "json/type.json");
            JsonAdapter<TypesData> jsonAdapter = moshi.adapter(TypesData.class);
            TypesData typesData = jsonAdapter.fromJson(json);

            for (Type type : typesData.getTypes()) {
                binding.tabLayout.addTab(binding.tabLayout.newTab().setText(type.getName()), false);
                adapter.addFragment(newInstance(ListType.NORMAL, type.getId()),
                        type.getName());
            }
            binding.pager.setAdapter(adapter);

            final int curItem = binding.pager.getCurrentItem();
            if (curItem != binding.tabLayout.getSelectedTabPosition() && curItem < binding.tabLayout.getTabCount()) {
                binding.tabLayout.getTabAt(curItem).select();
            }
        } catch (IOException e) {
            e.printStackTrace();
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
                startActivity(WebViewActivity.Companion.createIntent(MainActivity.this
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
                startActivity(HelpActivity.Companion.createIntent(MainActivity.this));
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
                            .setContentText(getString(R.string.tutorial_nav_text))
                            .setDismissText(getString(android.R.string.ok))
                            .setDismissOnTouch(true)
                            .build()
            );

            // Search tutorial
            View searchIcon = findViewById(R.id.action_search);
            sequence.addSequenceItem(
                    new MaterialShowcaseView.Builder(MainActivity.this)
                            .setTarget(searchIcon)
                            .setContentText(getString(R.string.tutorial_search_text))
                            .setDismissText(getString(android.R.string.ok))
                            .setDismissOnTouch(true)
                            .build()
            );

            // Tab tutorial
            sequence.addSequenceItem(
                    new MaterialShowcaseView.Builder(MainActivity.this)
                            .setTarget(binding.tabLayout)
                            .setContentText(getString(R.string.tutorial_tab_text))
                            .setDismissText(getString(android.R.string.ok))
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

    @Override
    public AndroidInjector<Object> androidInjector() {
        return androidInjector;
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
