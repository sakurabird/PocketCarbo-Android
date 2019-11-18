package com.sakurafish.pockettoushituryou.view.activity

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.afollestad.materialdialogs.MaterialDialog
import com.google.android.material.navigation.NavigationView
import com.google.android.material.tabs.TabLayout
import com.sakurafish.pockettoushituryou.R
import com.sakurafish.pockettoushituryou.data.local.LocalJsonResolver
import com.sakurafish.pockettoushituryou.data.local.TypesData
import com.sakurafish.pockettoushituryou.databinding.ActivityMainBinding
import com.sakurafish.pockettoushituryou.shared.rxbus.EventWithMessage
import com.sakurafish.pockettoushituryou.shared.rxbus.RxBus
import com.sakurafish.pockettoushituryou.shared.AlarmUtils
import com.sakurafish.pockettoushituryou.shared.Pref
import com.sakurafish.pockettoushituryou.shared.ext.goBrowser
import com.sakurafish.pockettoushituryou.view.customview.MaterialSearchView
import com.sakurafish.pockettoushituryou.view.fragment.FoodsFragment
import com.sakurafish.pockettoushituryou.view.helper.ShowcaseHelper
import com.sakurafish.pockettoushituryou.view.helper.ShowcaseHelper.*
import com.squareup.moshi.Moshi
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import timber.log.Timber
import uk.co.deanwild.materialshowcaseview.IShowcaseListener
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig
import java.io.IOException
import java.util.*
import javax.inject.Inject

class MainActivity : AppCompatActivity(), HasAndroidInjector, NavigationView.OnNavigationItemSelectedListener {

    @Inject
    lateinit var androidInjector: DispatchingAndroidInjector<Any>
    @Inject
    lateinit var moshi: Moshi
    @Inject
    lateinit var pref: Pref
    @Inject
    lateinit var showcaseHelper: ShowcaseHelper

    private lateinit var binding: ActivityMainBinding

    val currentPagerPosition: Int
        get() = binding.pager.currentItem

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        initView()

        pleaseReview()

        showAppMessage()
    }

    private fun pleaseReview() {
        if (pref.getPrefBool(getString(R.string.PREF_ASK_REVIEW), false)
                || pref.getPrefInt(getString(R.string.PREF_LAUNCH_COUNT)) != 10) {
            return
        }
        //10回めの起動でレビュー誘導
        val dialog = MaterialDialog(this, MaterialDialog.DEFAULT_BEHAVIOR)
        dialog.title(null, getString(R.string.ask_review_title))
        dialog.message(null, getString(R.string.ask_review_message), null)
        dialog.positiveButton(null, getString(android.R.string.ok)) {
            // Google Play
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.app_url))))
        }
        dialog.show()

        pref.setPref(getString(R.string.PREF_ASK_REVIEW), true)
    }

    private fun showAppMessage() {
        scheduleNotification()
        val lastNo = pref.getPrefInt(getString(R.string.PREF_APP_MESSAGE_NO))
        val messageNo = resources.getInteger(R.integer.APP_MESSAGE_NO)
        val messageText = getString(R.string.APP_MESSAGE_TEXT)

        if (messageNo <= lastNo) {
            return
        }

        // インストール時点のメッセージは表示しない
        if (pref.getPrefInt(getString(R.string.PREF_LAUNCH_COUNT)) <= 1) {
            pref.setPref(getString(R.string.PREF_APP_MESSAGE_NO), messageNo)
            return
        }
        Timber.tag(TAG).d("no:$messageNo message:$messageText")

        val dialog = MaterialDialog(this, MaterialDialog.DEFAULT_BEHAVIOR)
        dialog.title(null, getString(R.string.announcement))
        dialog.message(null, messageText, null)
        dialog.positiveButton(null, getString(android.R.string.ok))
        dialog.show()

        pref.setPref(getString(R.string.PREF_APP_MESSAGE_NO), messageNo)
    }

    private fun scheduleNotification() {
        AlarmUtils.unregisterAlarm(this)
        AlarmUtils.registerAlarm(this)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        showTutorialOnce()
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_search -> {
                binding.searchView.openSearch()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun initView() {
        setSupportActionBar(binding.toolbar)
        supportActionBar!!.setTitle(R.string.list_title)

        val headerView = binding.navView.getHeaderView(0)
        headerView.setOnClickListener { goBrowser("http://www.pockettoushituryou.com") }

        val toggle = ActionBarDrawerToggle(
                this, binding.drawerLayout, binding.toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        binding.navView.setNavigationItemSelectedListener(this)

        // 音声入力による検索を行わない
        binding.searchView.setVoiceIcon(0)

        binding.searchView.setOnQueryTextListener(object : MaterialSearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                if (query.replace("　".toRegex(), " ").trim { it <= ' ' }.isEmpty()) {
                    Toast.makeText(this@MainActivity, getString(R.string.action_search_hint), Toast.LENGTH_SHORT).show()
                    return true
                }
                startActivity(SearchResultActivity.createIntent(this@MainActivity, query))
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                return false
            }
        })

        binding.searchView.setOnItemClickListener { _, _, position, _ ->
            // the suggestion list is clicked.
            val suggestion = binding.searchView.getSuggestionAtPosition(position)
            binding.searchView.setQuery(suggestion, false)
        }

        initTabs()
    }

    private fun initTabs() {
        val pageChangeListener = TabLayout.TabLayoutOnPageChangeListener(binding.tabLayout)
        binding.pager.addOnPageChangeListener(pageChangeListener)
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                binding.pager.currentItem = tab.position
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}

            override fun onTabReselected(tab: TabLayout.Tab) {}
        })

        val adapter = MyPagerAdapter(supportFragmentManager)
        val json: String
        try {
            json = LocalJsonResolver.loadJsonFromAsset(this, "json/type.json")
            val jsonAdapter = moshi.adapter(TypesData::class.java)
            val typesData = jsonAdapter.fromJson(json)

            for ((id, name) in typesData!!.types) {
                binding.tabLayout.addTab(binding.tabLayout.newTab().setText(name), false)
                adapter.addFragment(FoodsFragment.newInstance(id), name)
            }
            binding.pager.adapter = adapter

            val curItem = binding.pager.currentItem
            if (curItem != binding.tabLayout.selectedTabPosition && curItem < binding.tabLayout.tabCount) {
                binding.tabLayout.getTabAt(curItem)!!.select()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else if (binding.searchView.isOpen) {
            binding.searchView.closeSearch()
        } else {
            super.onBackPressed()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_home -> {
            }
            R.id.nav_favorite -> startActivity(FavoritesActivity.createIntent(this@MainActivity))
            R.id.nav_setting -> startActivity(SettingsActivity.createIntent(this@MainActivity))
            R.id.nav_announcement -> startActivity(WebViewActivity.createIntent(this@MainActivity,
                    "file:///android_asset/www/announcement.html", getString(R.string.announcement)))
            R.id.nav_share -> {
                val intent = Intent()
                intent.action = Intent.ACTION_SEND
                intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_text))
                intent.type = "text/plain"
                startActivity(intent)
            }
            R.id.nav_help -> startActivity(HelpActivity.createIntent(this@MainActivity))
        }// Do nothing

        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return false
    }

    private fun showTutorialOnce() {
        Handler().post {
            val config = ShowcaseConfig()
            config.delay = SHOWCASE_DELAY.toLong()

            val sequence = MaterialShowcaseSequence(this@MainActivity, SHOWCASE_ID_MAINACTIVITY)
            sequence.setConfig(config)

            // Menu tutorial
            val drawerIcon = binding.toolbar.getChildAt(1)
            sequence.addSequenceItem(
                    MaterialShowcaseView.Builder(this@MainActivity)
                            .setTarget(drawerIcon)
                            .setContentText(getString(R.string.tutorial_nav_text))
                            .setDismissText(getString(android.R.string.ok))
                            .setDismissOnTouch(true)
                            .build()
            )

            // Search tutorial
            val searchIcon = findViewById<View>(R.id.action_search)
            sequence.addSequenceItem(
                    MaterialShowcaseView.Builder(this@MainActivity)
                            .setTarget(searchIcon)
                            .setContentText(getString(R.string.tutorial_search_text))
                            .setDismissText(getString(android.R.string.ok))
                            .setDismissOnTouch(true)
                            .build()
            )

            // Tab tutorial
            sequence.addSequenceItem(
                    MaterialShowcaseView.Builder(this@MainActivity)
                            .setTarget(binding.tabLayout)
                            .setContentText(getString(R.string.tutorial_tab_text))
                            .setDismissText(getString(android.R.string.ok))
                            .withRectangleShape(true)
                            .setListener(object : IShowcaseListener {
                                override fun onShowcaseDisplayed(materialShowcaseView: MaterialShowcaseView) {

                                }

                                override fun onShowcaseDismissed(materialShowcaseView: MaterialShowcaseView) {
                                    showcaseHelper.setPrefShowcaseMainactivityFinished(true)
                                    val rxBus = RxBus.getIntanceBus()
                                    rxBus.post(EventWithMessage(EVENT_SHOWCASE_MAINACTIVITY_FINISHED))

                                }
                            })
                            .setDismissOnTouch(true)
                            .build()
            )
            sequence.start()
        }
    }

    override fun androidInjector(): AndroidInjector<Any> = androidInjector

    private class MyPagerAdapter internal constructor(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {
        private val mFragmentList = ArrayList<Fragment>()
        private val mFragmentTitleList = ArrayList<String>()

        internal fun addFragment(fragment: Fragment, title: String?) {
            mFragmentList.add(fragment)
            mFragmentTitleList.add(title!!)
        }

        override fun getItem(position: Int): Fragment {
            return mFragmentList[position]
        }

        override fun getCount(): Int {
            return mFragmentList.size
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return mFragmentTitleList[position]
        }
    }

    companion object {
        private val TAG = MainActivity::class.java.simpleName

        fun createIntent(context: Context): Intent {
            return Intent(context, MainActivity::class.java)
        }
    }
}
