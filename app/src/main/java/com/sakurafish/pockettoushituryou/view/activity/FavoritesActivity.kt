package com.sakurafish.pockettoushituryou.view.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.sakurafish.pockettoushituryou.R
import com.sakurafish.pockettoushituryou.databinding.ActivityFavoritesBinding
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import javax.inject.Inject

class FavoritesActivity : AppCompatActivity(), HasAndroidInjector {

    @Inject
    lateinit var androidInjector: DispatchingAndroidInjector<Any>

    private lateinit var binding: ActivityFavoritesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_favorites)
        initView()
    }

    private fun initView() {
        setSupportActionBar(binding.toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.title = getString(R.string.favorite)
        binding.toolbar.setNavigationOnClickListener { onBackPressed() }
    }

    override fun androidInjector(): AndroidInjector<Any> = androidInjector

    companion object {
        fun createIntent(context: Context): Intent {
            return Intent(context, FavoritesActivity::class.java)
        }
    }
}
