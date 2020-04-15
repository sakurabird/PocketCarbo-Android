package com.sakurafish.pockettoushituryou.viewmodel

import androidx.annotation.IntRange
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sakurafish.pockettoushituryou.data.db.entity.Food
import com.sakurafish.pockettoushituryou.data.db.entity.FoodSortOrder
import com.sakurafish.pockettoushituryou.data.db.entity.Kind
import com.sakurafish.pockettoushituryou.data.db.entity.KindCompanion
import com.sakurafish.pockettoushituryou.repository.FoodRepository
import com.sakurafish.pockettoushituryou.repository.KindRepository
import com.sakurafish.pockettoushituryou.store.Action
import com.sakurafish.pockettoushituryou.store.Dispatcher
import com.sakurafish.pockettoushituryou.view.helper.ShowcaseHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class FoodsViewModel @Inject constructor(
        private val foodRepository: FoodRepository,
        private val kindRepository: KindRepository,
        private val dispatcher: Dispatcher,
        private val showcaseHelper: ShowcaseHelper
) : ViewModel() {

    private var typeId = 1

    private val _foods = MutableLiveData<List<Food>>()
    val foods: LiveData<List<Food>> = _foods

    private val _kinds = MutableLiveData<List<Kind>>()
    val kinds: LiveData<List<Kind>> = _kinds

    private val _isLoading = MutableLiveData<Boolean>().apply {
        value = true
    }
    val isLoading: LiveData<Boolean> = _isLoading

    fun setTypeId(@IntRange(from = 1, to = 6) typeId: Int) {
        this.typeId = typeId
    }

    fun findKinds() {
        viewModelScope.launch(Dispatchers.IO) {
            val kinds = kindRepository.findByTypeId(typeId)
            _kinds.postValue(kinds)
        }
    }

    fun findFoods(kindId: Int, sortOrder: FoodSortOrder) {
        viewModelScope.launch(Dispatchers.IO) {
            enableIsLoading(true)

            if (kindId == KindCompanion.KIND_ALL) {
                val foods = foodRepository.findByType(typeId, sortOrder)
                _foods.postValue(foods)
            } else {
                val foods = foodRepository.findByTypeAndKind(typeId, kindId, sortOrder)
                _foods.postValue(foods)
            }
            enableIsLoading(false)

            if (!showcaseHelper.isShowcaseMainActivityFinished
                    && !showcaseHelper.isShowcaseFoodListFragmentFinished)
                dispatcher.dispatch(Action.ShowcaseReady)
        }
    }

    fun enableIsLoading(enable: Boolean) {
        _isLoading.postValue(enable)
    }

    companion object {
        internal val TAG = FoodsViewModel::class.java.simpleName
    }
}
