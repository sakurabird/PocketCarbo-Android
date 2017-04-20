package com.sakurafish.pockettoushituryou.view.activity;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.gson.Gson;
import com.sakurafish.pockettoushituryou.R;
import com.sakurafish.pockettoushituryou.databinding.ActivityMainBinding;
import com.sakurafish.pockettoushituryou.model.TypesData;
import com.sakurafish.pockettoushituryou.repository.FoodsRepository;
import com.sakurafish.pockettoushituryou.view.fragment.FoodListFragment;
import com.sakurafish.pockettoushituryou.view.helper.ResourceResolver;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static timber.log.Timber.tag;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    ActivityMainBinding binding;

    @Inject
    CompositeDisposable compositeDisposable;

    @Inject
    FoodsRepository foodsRepository;

    @Inject
    ResourceResolver resourceResolver;

    @Override
    protected void onStop() {
        super.onStop();
        compositeDisposable.dispose();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getComponent().inject(this);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        initView();
        loadPocketCarboData();
    }

    public void loadPocketCarboData() {
        Disposable disposable = Single.zip(foodsRepository.findKindsData(), foodsRepository.findFoodsData(),
                (kindsData, foodsData) -> io.reactivex.Observable.empty())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observable -> {
                            tag(TAG).d("Succeeded in loading sessions.");
                            initTabs();
                        },
                        throwable -> {
                            tag(TAG).e(throwable, "Failed to load sessions.");
                        });
        compositeDisposable.add(disposable);
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
            adapter.addFragment(FoodListFragment.newInstance(type.id), type.name);
        }

        binding.pager.setAdapter(adapter);

        final int curItem = binding.pager.getCurrentItem();
        if (curItem != binding.tabLayout.getSelectedTabPosition() && curItem < binding.tabLayout.getTabCount()) {
            binding.tabLayout.getTabAt(curItem).select();
        }
    }

    private void initView() {
        setSupportActionBar(binding.toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, binding.drawerLayout, binding.toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        binding.drawerLayout.setDrawerListener(toggle);
        toggle.syncState();

        binding.navView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        binding.drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private static class MyPagerAdapter extends FragmentPagerAdapter {
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
