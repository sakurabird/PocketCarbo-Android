package com.sakurafish.pockettoushituryou.ui.shared.web

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.sakurafish.pockettoushituryou.databinding.FragmentWebviewBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WebViewFragment : Fragment() {

    private var _binding: FragmentWebviewBinding? = null
    private val binding get() = _binding!!

    private val viewModel: WebViewViewModel by viewModels()

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
