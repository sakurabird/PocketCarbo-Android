package com.sakurafish.pockettoushituryou.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.sakurafish.pockettoushituryou.R
import com.sakurafish.pockettoushituryou.databinding.ViewWebviewBinding
import com.sakurafish.pockettoushituryou.di.Injectable
import com.sakurafish.pockettoushituryou.view.activity.WebViewActivity.Companion.EXTRA_URL
import com.sakurafish.pockettoushituryou.viewmodel.WebViewViewModel
import javax.inject.Inject

class WebViewFragment : Fragment(), Injectable {

    @Inject
    internal var viewModel: WebViewViewModel? = null

    private var binding: ViewWebviewBinding? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.view_webview, container, false)
        binding = DataBindingUtil.bind(view)
        binding!!.viewModel = viewModel

        return binding!!.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel!!.url = arguments!!.getString(EXTRA_URL, "")
    }

    override fun onPause() {
        super.onPause()
        binding!!.webView.onPause()
    }

    override fun onResume() {
        super.onResume()
        binding!!.webView.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding!!.webView.destroy()
        viewModel!!.destroy()
    }

    companion object {

        val TAG = WebViewFragment::class.java.simpleName

        fun newInstance(url: String): WebViewFragment {
            val fragment = WebViewFragment()
            val bundle = Bundle()
            bundle.putString(EXTRA_URL, url)
            fragment.arguments = bundle
            return fragment
        }
    }
}
