package com.sakurafish.pockettoushituryou.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.sakurafish.pockettoushituryou.R
import com.sakurafish.pockettoushituryou.databinding.FragmentSearchResultBinding
import com.sakurafish.pockettoushituryou.di.Injectable
import com.sakurafish.pockettoushituryou.repository.FavoriteRepository
import com.sakurafish.pockettoushituryou.repository.KindRepository
import com.sakurafish.pockettoushituryou.shared.events.Events
import com.sakurafish.pockettoushituryou.shared.events.HostClass
import com.sakurafish.pockettoushituryou.view.activity.SearchResultActivity.Companion.EXTRA_QUERY
import com.sakurafish.pockettoushituryou.view.adapter.FoodsAdapter
import com.sakurafish.pockettoushituryou.viewmodel.FoodItemViewModel
import com.sakurafish.pockettoushituryou.viewmodel.SearchResultViewModel
import javax.inject.Inject

class SearchResultFragment : Fragment(), Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var kindRepository: KindRepository

    @Inject
    lateinit var favoriteRepository: FavoriteRepository

    @Inject
    lateinit var events: Events

    private lateinit var viewModel: SearchResultViewModel
    private lateinit var binding: FragmentSearchResultBinding
    private lateinit var adapter: FoodsAdapter
    private var query: String = ""

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? = DataBindingUtil.inflate<FragmentSearchResultBinding>(
            inflater,
            R.layout.fragment_search_result,
            container,
            false
    ).also {
        binding = it
        this.query = arguments?.getString(EXTRA_QUERY)!!
    }.root

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = ViewModelProvider(this@SearchResultFragment, viewModelFactory)
                .get(SearchResultViewModel::class.java)

        initView()
        setupViewModel()

        if (this.query.isEmpty()) {
            this.query = savedInstanceState?.getString(EXTRA_QUERY) ?: ""
        }
        viewModel.searchFoods(query)
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
                    requireContext(), favoriteRepository, food, events, HostClass.SEARCH)
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