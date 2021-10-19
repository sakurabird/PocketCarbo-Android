package com.sakurafish.pockettoushituryou.view.activity

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.lifecycle.lifecycleScope
import com.afollestad.materialdialogs.MaterialDialog
import com.google.android.material.navigation.NavigationView
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.analytics.FirebaseAnalytics
import com.sakurafish.pockettoushituryou.R
import com.sakurafish.pockettoushituryou.data.local.LocalJsonResolver
import com.sakurafish.pockettoushituryou.data.local.TypesData
import com.sakurafish.pockettoushituryou.databinding.ActivityMainBinding
import com.sakurafish.pockettoushituryou.shared.AlarmUtils
import com.sakurafish.pockettoushituryou.shared.Pref
import com.sakurafish.pockettoushituryou.shared.events.Events
import com.sakurafish.pockettoushituryou.shared.events.ShowcaseState
import com.sakurafish.pockettoushituryou.shared.ext.goBrowser
import com.sakurafish.pockettoushituryou.view.adapter.MainPagerAdapter
import com.sakurafish.pockettoushituryou.view.customview.MaterialSearchView
import com.sakurafish.pockettoushituryou.view.fragment.FoodsFragment
import com.sakurafish.pockettoushituryou.view.helper.ShowcaseHelper
import com.sakurafish.pockettoushituryou.viewmodel.MainViewModel
import com.squareup.moshi.Moshi
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    @Inject
    lateinit var moshi: Moshi

    @Inject
    lateinit var pref: Pref

    @Inject
    lateinit var showcaseHelper: ShowcaseHelper

    @Inject
    lateinit var events: Events

    private lateinit var binding: ActivityMainBinding
    private val mainViewModel: MainViewModel by viewModels()
    private lateinit var firebaseAnalytics: FirebaseAnalytics

    val currentPagerPosition: Int
        get() = binding.pager.currentItem

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)

        firebaseAnalytics = FirebaseAnalytics.getInstance(this)
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.APP_OPEN, Bundle())

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.lifecycleOwner = this@MainActivity
        binding.viewModel = mainViewModel

        setLaunchCount()
        initView()
        setupObservers()
        showAppMessage()
        pleaseReview()
    }

    private fun setupObservers() {
        // Show tutorial
        lifecycleScope.launchWhenStarted {
            events.showcaseState.collect { showcaseState ->
                if (showcaseState == ShowcaseState.READY) {
                    mainViewModel.enablePreventClick(false)
                    showcaseHelper.showTutorialOnce(this@MainActivity, binding)
                }
            }
        }
    }

    private fun setLaunchCount() {
        var launchCount = pref.getPrefInt(getString(R.string.PREF_LAUNCH_COUNT))
        pref.setPref(getString(R.string.PREF_LAUNCH_COUNT), ++launchCount)
    }

    private fun pleaseReview() {
        if (pref.getPrefBool(getString(R.string.PREF_ASK_REVIEW), false)
            || pref.getPrefInt(getString(R.string.PREF_LAUNCH_COUNT)) != 10
        ) {
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
        dialog.negativeButton(R.string.cancel)
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
        if (!showcaseHelper.isShowcaseMainActivityFinished) {
            mainViewModel.enablePreventClick(true)
        }

        setSupportActionBar(binding.toolbar)
        supportActionBar!!.setTitle(R.string.list_title)

        val headerView = binding.navView.getHeaderView(0)
        headerView.setOnClickListener { goBrowser(getString(R.string.app_url)) }

        val toggle = ActionBarDrawerToggle(
            this,
            binding.drawerLayout,
            binding.toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        binding.navView.setNavigationItemSelectedListener(this)

        // 音声入力による検索を行わない
        binding.searchView.setVoiceIcon(0)

        binding.searchView.setOnQueryTextListener(object : MaterialSearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                if (query.replace("　".toRegex(), " ").trim { it <= ' ' }.isEmpty()) {
                    Toast.makeText(
                        this@MainActivity,
                        getString(R.string.action_search_hint),
                        Toast.LENGTH_SHORT
                    ).show()
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
        val adapter = MainPagerAdapter(this@MainActivity)

        val json: String
        try {
            json = LocalJsonResolver.loadJsonFromAsset(this, "json/type.json")
            val jsonAdapter = moshi.adapter(TypesData::class.java)
            val typesData = jsonAdapter.fromJson(json)

            for ((id, name) in typesData!!.types) {
                binding.tabLayout.addTab(binding.tabLayout.newTab().setText(name), false)
                adapter.addFragment(FoodsFragment.newInstance(id))
            }
            binding.pager.adapter = adapter

            TabLayoutMediator(binding.tabLayout, binding.pager) { tab, position ->
                tab.text = typesData.types[position].name
            }.attach()

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
            R.id.nav_announcement -> startActivity(
                WebViewActivity.createIntent(
                    this@MainActivity,
                    "file:///android_asset/www/announcement.html", getString(R.string.announcement)
                )
            )
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

    companion object {
        private val TAG = MainActivity::class.java.simpleName

        fun createIntent(context: Context): Intent {
            return Intent(context, MainActivity::class.java)
        }
    }
}
