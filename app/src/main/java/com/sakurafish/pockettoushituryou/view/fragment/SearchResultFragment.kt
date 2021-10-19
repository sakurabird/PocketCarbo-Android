package com.sakurafish.pockettoushituryou.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.sakurafish.pockettoushituryou.databinding.FragmentSearchResultBinding
import com.sakurafish.pockettoushituryou.repository.FavoriteRepository
import com.sakurafish.pockettoushituryou.repository.KindRepository
import com.sakurafish.pockettoushituryou.shared.events.Events
import com.sakurafish.pockettoushituryou.shared.events.HostClass
import com.sakurafish.pockettoushituryou.view.activity.SearchResultActivity.Companion.EXTRA_QUERY
import com.sakurafish.pockettoushituryou.view.adapter.FoodsAdapter
import com.sakurafish.pockettoushituryou.viewmodel.FoodItemViewModel
import com.sakurafish.pockettoushituryou.viewmodel.SearchResultViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SearchResultFragment : Fragment() {

    @Inject
    lateinit var kindRepository: KindRepository

    @Inject
    lateinit var favoriteRepository: FavoriteRepository

    @Inject
    lateinit var events: Events

    private var _binding: FragmentSearchResultBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SearchResultViewModel by viewModels()
    private lateinit var adapter: FoodsAdapter
    private var query: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSearchResultBinding.inflate(inflater, container, false)
        this.query = arguments?.getString(EXTRA_QUERY)!!
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        setupViewModel()

        if (this.query.isEmpty()) {
            this.query = savedInstanceState?.getString(EXTRA_QUERY) ?: ""
        }
        viewModel.searchFoods(query)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(EXTRA_QUERY, viewModel.lastQueryValue())
    }

    private fun initView() {
        binding.lifecycleOwner = viewLifecycleOwner
        adapter = FoodsAdapter(viewLifecycleOwner)
        binding.viewModel = viewModel
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun setupViewModel() {
        viewModel.foods.observe(viewLifecycleOwner, Observer {
            it ?: return@Observer

            val adapterItems = ArrayList<FoodItemViewModel>()
            it.forEach { food ->
                adapterItems += FoodItemViewModel(
                    requireContext(), favoriteRepository, food, events, HostClass.SEARCH
                )
            }

            adapter.run {
                items.clear()
                items.addAll(adapterItems)
                notifyDataSetChanged()
            }
        })
    }

    companion object {
        fun newInstance(query: String?): SearchResultFragment {
            val fragment = SearchResultFragment()
            val bundle = Bundle()
            bundle.putString(EXTRA_QUERY, query)
            fragment.arguments = bundle
            return fragment
        }
    }
}