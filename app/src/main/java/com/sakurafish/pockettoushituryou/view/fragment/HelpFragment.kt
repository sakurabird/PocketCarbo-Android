package com.sakurafish.pockettoushituryou.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider

import com.sakurafish.pockettoushituryou.R
import com.sakurafish.pockettoushituryou.databinding.FragmentHelpBinding
import com.sakurafish.pockettoushituryou.di.Injectable
import com.sakurafish.pockettoushituryou.viewmodel.HelpViewModel

import javax.inject.Inject

class HelpFragment : Fragment(), Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var viewModel: HelpViewModel
    private lateinit var binding: FragmentHelpBinding

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? = DataBindingUtil.inflate<FragmentHelpBinding>(
            inflater,
            R.layout.fragment_help,
            container,
            false
    ).also {
        binding = it
    }.root

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        binding.lifecycleOwner = viewLifecycleOwner
        viewModel = ViewModelProvider(this@HelpFragment, viewModelFactory)
                .get(HelpViewModel::class.java)
        binding.viewModel = viewModel
    }
}
