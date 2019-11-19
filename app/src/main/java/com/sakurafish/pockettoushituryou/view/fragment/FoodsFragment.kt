package com.sakurafish.pockettoushituryou.view.fragment

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.annotation.IntRange
import androidx.browser.browseractions.BrowserActionsIntent.EXTRA_TYPE
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.sakurafish.pockettoushituryou.R
import com.sakurafish.pockettoushituryou.data.db.entity.Kinds
import com.sakurafish.pockettoushituryou.databinding.FragmentFoodsBinding
import com.sakurafish.pockettoushituryou.di.Injectable
import com.sakurafish.pockettoushituryou.repository.FavoriteFoodsRepository
import com.sakurafish.pockettoushituryou.repository.FoodsRepository
import com.sakurafish.pockettoushituryou.repository.KindsRepository
import com.sakurafish.pockettoushituryou.shared.rxbus.EventWithMessage
import com.sakurafish.pockettoushituryou.shared.rxbus.FavoritesUpdateEvent
import com.sakurafish.pockettoushituryou.shared.rxbus.FoodsUpdatedEvent
import com.sakurafish.pockettoushituryou.shared.rxbus.RxBus
import com.sakurafish.pockettoushituryou.view.adapter.FoodsAdapter
import com.sakurafish.pockettoushituryou.view.adapter.KindSpinnerAdapter
import com.sakurafish.pockettoushituryou.view.adapter.SortSpinnerAdapter
import com.sakurafish.pockettoushituryou.view.helper.ShowcaseHelper
import com.sakurafish.pockettoushituryou.viewmodel.FoodItemViewModel
import com.sakurafish.pockettoushituryou.viewmodel.FoodsViewModel
import com.sakurafish.pockettoushituryou.viewmodel.HostClass
import io.reactivex.functions.Consumer
import timber.log.Timber
import javax.inject.Inject


class FoodsFragment : Fragment(), Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    @Inject
    lateinit var kindsRepository: KindsRepository
    @Inject
    lateinit var favoriteFoodsRepository: FavoriteFoodsRepository
    @Inject
    lateinit var showcaseHelper: ShowcaseHelper

    private lateinit var viewModel: FoodsViewModel
    private lateinit var binding: FragmentFoodsBinding
    private lateinit var adapter: FoodsAdapter
    private lateinit var kindSpinnerAdapter: KindSpinnerAdapter
    private var typeId: Int = 0
    private var kindId: Int = Kinds.ALL
    private var sort: Int = 0 // デフォルトは名前順;
    private var sortToast: Boolean = false
    private var rxBus: RxBus? = null

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? = DataBindingUtil.inflate<FragmentFoodsBinding>(
            inflater,
            R.layout.fragment_foods,
            container,
            false
    ).also {
        binding = it
        this.typeId = arguments?.getInt(EXTRA_TYPE)!!
    }.root

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = ViewModelProvider(this@FoodsFragment, viewModelFactory)
                .get(FoodsViewModel::class.java)

        initView()
        setupViewModel()

        if (savedInstanceState != null) {
            this.typeId = savedInstanceState.getInt(EXTRA_TYPE)
            this.kindId = savedInstanceState.getInt("kindId")
            this.sort = savedInstanceState.getInt("sort")
        }
        loadDB()
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterRxBus()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(EXTRA_TYPE, this.typeId)
        outState.putInt("kindId", this.kindId)
        outState.putInt("sort", this.sort)
    }

    private fun loadDB() {
        viewModel.setTypeId(typeId)
        viewModel.findFoods(kindId, sort)
    }

    private fun initView() {
        viewModel.setTypeId(this.typeId)

        initRxBus()

        binding.lifecycleOwner = viewLifecycleOwner
        adapter = FoodsAdapter(viewLifecycleOwner)
        binding.viewModel = viewModel
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())

        initKindsSpinner()
        initSortSpinner()
    }

    private fun setupViewModel() {
        viewModel.foods.observe(viewLifecycleOwner, Observer {
            it ?: return@Observer

            val adapterItems = ArrayList<FoodItemViewModel>()
            it.forEach { food ->
                adapterItems += FoodItemViewModel(
                        requireContext(), kindsRepository, favoriteFoodsRepository, food, HostClass.FOODS)
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
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                kindId = if (position == 0) {
                    Kinds.ALL
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
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                sort = position
                if (position < 0 || position > 3) sort = 0
                if (sortToast) {
                    Toast.makeText(requireContext(), SortSpinnerAdapter.texts[position] + "にソートしました。", Toast.LENGTH_SHORT).show()
                } else {
                    sortToast = true
                }
                loadDB()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun initRxBus() {
        this.rxBus = RxBus.getIntanceBus()

        registerRxBus(FoodsUpdatedEvent::class.java, Consumer { rxBusMessage ->
            // DBに食品データが全て追加された
            if (TextUtils.equals(rxBusMessage.message, FoodsRepository.EVENT_DB_UPDATED)) {
                loadDB()
            }
        })

        registerRxBus(FavoritesUpdateEvent::class.java, Consumer {
            if (it.hostClass == HostClass.FAVORITES || it.hostClass == HostClass.SEARCH) {
                adapter.refreshFavoriteStatus(it.foodsId)
            }
        })

        registerRxBus(EventWithMessage::class.java, Consumer { rxBusMessage ->
            // MainActivityのチュートリアルが表示済みになった
            if (TextUtils.equals(rxBusMessage.message, ShowcaseHelper.EVENT_SHOWCASE_MAINACTIVITY_FINISHED)) {
                showcaseHelper.showTutorialOnce(this@FoodsFragment, binding, typeId)
            }
        })
    }

    private fun <T> registerRxBus(eventType: Class<T>, action: Consumer<T>) {
        val disposable = rxBus?.doSubscribe(eventType, action,
                Consumer { throwable -> Timber.tag(TAG).e(throwable, "Failed to register RxBus") })
        rxBus?.addSubscription(this, disposable)
    }

    private fun unregisterRxBus() {
        rxBus?.unSubscribe(this)
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