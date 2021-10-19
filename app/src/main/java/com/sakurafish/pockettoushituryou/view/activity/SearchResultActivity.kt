package com.sakurafish.pockettoushituryou.view.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.sakurafish.pockettoushituryou.R
import com.sakurafish.pockettoushituryou.databinding.ActivitySearchresultBinding
import com.sakurafish.pockettoushituryou.shared.ext.replaceFragment
import com.sakurafish.pockettoushituryou.view.fragment.SearchResultFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchResultActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchresultBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val query = intent.getStringExtra(EXTRA_QUERY)

        binding = ActivitySearchresultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
        replaceFragment(SearchResultFragment.newInstance(query), R.id.content_view)
    }

    private fun initView() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title =
            getString(R.string.search_result_title) + " (" + intent.getStringExtra(EXTRA_QUERY) + ")"
        binding.toolbar.setNavigationOnClickListener { onBackPressed() }
    }

    companion object {
        const val EXTRA_QUERY = "query"

        fun createIntent(context: Context, query: String): Intent {
            val intent = Intent(context, SearchResultActivity::class.java)
            intent.putExtra(EXTRA_QUERY, query)
            return intent
        }
    }
}
