package com.sakurafish.pockettoushituryou.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.sakurafish.pockettoushituryou.R
import com.sakurafish.pockettoushituryou.databinding.FragmentWebviewBinding
import com.sakurafish.pockettoushituryou.di.Injectable
import com.sakurafish.pockettoushituryou.viewmodel.WebViewViewModel
import javax.inject.Inject

class WebViewFragment : Fragment(), Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var viewModel: WebViewViewModel
    private lateinit var binding: FragmentWebviewBinding

    private var url: String = ""

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? = DataBindingUtil.inflate<FragmentWebviewBinding>(
            inflater,
            R.layout.fragment_webview,
            container,
            false
    ).also {
        binding = it
    }.root

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        binding.lifecycleOwner = this@WebViewFragment
        viewModel = ViewModelProvider(this@WebViewFragment, viewModelFactory)
                .get(WebViewViewModel::class.java)
        binding.viewModel = viewModel

        viewModel.initAction.observe(viewLifecycleOwner, Observer {
            if (it == false) return@Observer
            viewModel.setUrl(url)
            viewModel.enableInitAction(false)
        })
    }

    fun setUrl(url: String) {
        this.url = url
    }
}
