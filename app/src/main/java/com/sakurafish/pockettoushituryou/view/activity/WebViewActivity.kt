package com.sakurafish.pockettoushituryou.view.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.sakurafish.pockettoushituryou.R
import com.sakurafish.pockettoushituryou.databinding.ActivityWebviewBinding
import com.sakurafish.pockettoushituryou.view.fragment.WebViewFragment

class WebViewActivity : BaseActivity() {

    private lateinit var binding: ActivityWebviewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val url = intent.getStringExtra(EXTRA_URL)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_webview)
        initView()
        replaceFragment(WebViewFragment.newInstance(url), R.id.content_view)
    }

    private fun initView() {
        setSupportActionBar(binding.toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.title = intent.getStringExtra(EXTRA_TITLE)
        binding.toolbar.setNavigationOnClickListener { view -> onBackPressed() }
    }

    companion object {

        private val TAG = WebViewActivity::class.java.simpleName
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
