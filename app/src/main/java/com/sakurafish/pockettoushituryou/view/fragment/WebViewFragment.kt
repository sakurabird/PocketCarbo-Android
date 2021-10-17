package com.sakurafish.pockettoushituryou.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.sakurafish.pockettoushituryou.databinding.FragmentWebviewBinding
import com.sakurafish.pockettoushituryou.di.Injectable
import com.sakurafish.pockettoushituryou.viewmodel.WebViewViewModel
import javax.inject.Inject

class WebViewFragment : Fragment(), Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private var _binding: FragmentWebviewBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: WebViewViewModel

    private var url: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentWebviewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.lifecycleOwner = viewLifecycleOwner
        viewModel = ViewModelProvider(this@WebViewFragment, viewModelFactory)
            .get(WebViewViewModel::class.java)
        binding.viewModel = viewModel

        viewModel.initAction.observe(viewLifecycleOwner, Observer {
            if (it == false) return@Observer
            viewModel.setUrl(url)
            viewModel.enableInitAction(false)
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun setUrl(url: String) {
        this.url = url
    }
}
