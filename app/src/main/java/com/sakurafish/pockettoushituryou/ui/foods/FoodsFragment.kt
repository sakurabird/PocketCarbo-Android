package com.sakurafish.pockettoushituryou.ui.foods

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.annotation.IntRange
import androidx.browser.browseractions.BrowserActionsIntent.EXTRA_TYPE
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.sakurafish.pockettoushituryou.data.db.entity.FoodSortOrder
import com.sakurafish.pockettoushituryou.data.db.entity.KindCompanion.KIND_ALL
import com.sakurafish.pockettoushituryou.data.repository.FavoriteRepository
import com.sakurafish.pockettoushituryou.data.repository.KindRepository
import com.sakurafish.pockettoushituryou.databinding.FragmentFoodsBinding
import com.sakurafish.pockettoushituryou.di.module.IoDispatcher
import com.sakurafish.pockettoushituryou.di.module.MainDispatcher
import com.sakurafish.pockettoushituryou.shared.events.Events
import com.sakurafish.pockettoushituryou.shared.events.HostClass
import com.sakurafish.pockettoushituryou.shared.events.PopulateState
import com.sakurafish.pockettoushituryou.shared.events.ShowcaseState
import com.sakurafish.pockettoushituryou.ui.shared.helper.ShowcaseHelper
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@AndroidEntryPoint
class FoodsFragment : Fragment() {

    @Inject
    lateinit var kindRepository: KindRepository

    @Inject
    lateinit var favoriteRepository: FavoriteRepository

    @Inject
    lateinit var showcaseHelper: ShowcaseHelper

    @Inject
    lateinit var events: Events

    @Inject
    @IoDispatcher
    lateinit var ioDispatcher: CoroutineDispatcher

    @Inject
    @MainDispatcher
    lateinit var mainDispatcher: CoroutineDispatcher

    private var _binding: FragmentFoodsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: FoodsViewModel by viewModels()
    private lateinit var adapter: FoodsAdapter
    private lateinit var kindSpinnerAdapter: KindSpinnerAdapter
    private var typeId: Int = 0
    private var kindId: Int = KIND_ALL
    private var sortOrder: FoodSortOrder = FoodSortOrder.DEFAULT_SORT_ORDER // デフォルトは名前順
    private var sortToast: Boolean = false
    private var kindSpinnerInit: Boolean = true
    private var sortSpinnerInit: Boolean = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFoodsBinding.inflate(inflater, container, false)
        this.typeId = arguments?.getInt(EXTRA_TYPE)!!
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        setupObservers()
        setupViewModel()

        if (savedInstanceState != null) {
            this.typeId = savedInstanceState.getInt(EXTRA_TYPE)
            this.kindId = savedInstanceState.getInt("kindId")
            this.sortOrder = savedInstanceState.getSerializable("sort") as FoodSortOrder
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(EXTRA_TYPE, this.typeId)
        outState.putInt("kindId", this.kindId)
        outState.putSerializable("sort", this.sortOrder)
    }

    private fun loadDB() {
        val state = events.dataPopulateState.value
        if (state == PopulateState.POPULATED) {
            viewModel.findKinds()
            viewModel.findFoods(this.kindId, this.sortOrder)
        }
    }

    private fun initView() {
        viewModel.setTypeId(this.typeId)
        kindSpinnerInit = true
        sortSpinnerInit = true

        binding.lifecycleOwner = viewLifecycleOwner
        adapter = FoodsAdapter(ioDispatcher, mainDispatcher, viewLifecycleOwner)
        binding.viewModel = viewModel
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())

        initKindsSpinner()
        initSortSpinner()
    }

    private fun setupObservers() {
        lifecycleScope.launchWhenStarted {
            events.dataPopulateState.collect { populateState ->
                when (populateState) {
                    PopulateState.NONE -> {
                    }
                    PopulateState.POPULATE -> viewModel.enableIsLoading(true)
                    PopulateState.POPULATED -> {
                        viewModel.setTypeId(typeId)
                        loadDB()
                    }
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            events.favoritesClickedEvent.collectLatest { hostClass ->
                when (hostClass) {
                    HostClass.FOODS -> {
                    }
                    HostClass.FAVORITES -> adapter.refreshFavoriteStatus()
                    HostClass.SEARCH -> adapter.refreshFavoriteStatus()
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            events.showcaseState.collect { showcaseState ->
                if (showcaseState == ShowcaseState.PROCEEDED) {
                    showcaseHelper.showTutorialOnce(this@FoodsFragment, binding, typeId)
                }
            }
        }
    }

    private fun setupViewModel() {
        viewModel.foods.observe(viewLifecycleOwner, Observer {
            it ?: return@Observer

            val adapterItems = ArrayList<FoodItemViewModel>()
            it.forEach { food ->
                adapterItems += FoodItemViewModel(
                    ioDispatcher,
                    requireContext(),
                    favoriteRepository,
                    food,
                    events,
                    HostClass.FOODS
                )
            }

            adapter.run {
                items.clear()
                items.addAll(adapterItems)
                notifyDataSetChanged()
            }
        })

        viewModel.kinds.observe(viewLifecycleOwner, Observer {
            it ?: return@Observer
            kindSpinnerAdapter.run {
                setData(it)
            }
        })
    }

    private fun initKindsSpinner() {
        kindSpinnerAdapter = KindSpinnerAdapter(requireContext())
        binding.kindSpinner.adapter = kindSpinnerAdapter
        binding.kindSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (kindSpinnerInit) {
                    kindSpinnerInit = false
                    return
                }
                kindId = if (position == 0) {
                    KIND_ALL
                } else {
                    viewModel.kinds.value?.get(position - 1)?.id!!
                }
                loadDB()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun initSortSpinner() {
        val adapter = SortSpinnerAdapter(requireContext())
        binding.sortSpinner.adapter = adapter
        binding.sortSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (sortSpinnerInit) {
                    sortSpinnerInit = false
                    return
                }
                sortOrder = FoodSortOrder.fromInt(position) ?: FoodSortOrder.DEFAULT_SORT_ORDER
                if (sortToast) {
                    val message = FoodSortOrder.values()[position].title + "にソートしました。"
                    Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
                } else {
                    sortToast = true
                }
                loadDB()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    companion object {
        private val TAG = FoodsFragment::class.java.simpleName

        fun newInstance(@IntRange(from = 1, to = 6) type: Int): FoodsFragment {
            val fragment = FoodsFragment()
            val bundle = Bundle()
            bundle.putInt(EXTRA_TYPE, type)
            fragment.arguments = bundle
            return fragment
        }
    }
}