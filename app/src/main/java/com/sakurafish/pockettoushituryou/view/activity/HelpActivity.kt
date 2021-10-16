package com.sakurafish.pockettoushituryou.view.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.sakurafish.pockettoushituryou.R
import com.sakurafish.pockettoushituryou.databinding.ActivityHelpBinding
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import javax.inject.Inject

class HelpActivity : AppCompatActivity(), HasAndroidInjector {

    @Inject
    lateinit var androidInjector: DispatchingAndroidInjector<Any>

    private lateinit var binding: ActivityHelpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHelpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
    }

    private fun initView() {
        setSupportActionBar(binding.toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.title = getString(R.string.help)
        binding.toolbar.setNavigationOnClickListener { onBackPressed() }
    }

    override fun androidInjector(): AndroidInjector<Any> = androidInjector

    companion object {
        fun createIntent(context: Context): Intent {
            return Intent(context, HelpActivity::class.java)
        }
    }
}
