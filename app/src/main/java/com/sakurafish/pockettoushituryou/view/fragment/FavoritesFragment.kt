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
import com.sakurafish.pockettoushituryou.databinding.FragmentFavoritesBinding
import com.sakurafish.pockettoushituryou.di.Injectable
import com.sakurafish.pockettoushituryou.repository.FavoriteRepository
import com.sakurafish.pockettoushituryou.repository.KindRepository
import com.sakurafish.pockettoushituryou.shared.events.Events
import com.sakurafish.pockettoushituryou.shared.events.HostClass
import com.sakurafish.pockettoushituryou.view.adapter.FoodsAdapter
import com.sakurafish.pockettoushituryou.viewmodel.FavoritesViewModel
import com.sakurafish.pockettoushituryou.viewmodel.FoodItemViewModel
import javax.inject.Inject

class FavoritesFragment : Fragment(), Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var kindRepository: KindRepository

    @Inject
    lateinit var favoriteRepository: FavoriteRepository

    @Inject
    lateinit var events: Events

    private lateinit var viewModel: FavoritesViewModel
    private lateinit var binding: FragmentFavoritesBinding
    private lateinit var adapter: FoodsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = DataBindingUtil.inflate<FragmentFavoritesBinding>(
        inflater,
        R.layout.fragment_favorites,
        container,
        false
    ).also {
        binding = it
    }.root

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = ViewModelProvider(this@FavoritesFragment, viewModelFactory)
            .get(FavoritesViewModel::class.java)

        initView()
        setupViewModel()
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
                    requireContext(), favoriteRepository, food, events, HostClass.FAVORITES
                )
            }

            adapter.run {
                items.clear()
                items.addAll(adapterItems)
                notifyDataSetChanged()
            }
        })
    }
}