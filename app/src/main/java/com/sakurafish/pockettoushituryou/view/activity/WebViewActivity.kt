package com.sakurafish.pockettoushituryou.view.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.sakurafish.pockettoushituryou.R
import com.sakurafish.pockettoushituryou.databinding.ActivityWebviewBinding
import com.sakurafish.pockettoushituryou.view.fragment.WebViewFragment

class WebViewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWebviewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_webview)
        initView()
    }

    private fun initView() {
        setSupportActionBar(binding.toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.title = intent.getStringExtra(EXTRA_TITLE)
        binding.toolbar.setNavigationOnClickListener { onBackPressed() }

        val url = intent.getStringExtra(EXTRA_URL)
        val fragment: Fragment? = supportFragmentManager.findFragmentById(R.id.fragment_webview)
        if (fragment is WebViewFragment) {
            // TODO Replace Navigation safe args
            fragment.setUrl(url)
        }
    }

    companion object {
        const val EXTRA_URL = "url"
        const val EXTRA_TITLE = "title"

        fun createIntent(context: Context, url: String, title: String): Intent {
            val intent = Intent(context, WebViewActivity::class.java)
            intent.putExtra(EXTRA_URL, url)
            intent.putExtra(EXTRA_TITLE, title)
            return intent
        }
    }
}
