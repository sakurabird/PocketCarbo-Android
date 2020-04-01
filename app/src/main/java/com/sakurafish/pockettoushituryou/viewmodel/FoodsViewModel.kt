package com.sakurafish.pockettoushituryou.viewmodel

import androidx.annotation.IntRange
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sakurafish.pockettoushituryou.data.db.entity.orma.Foods
import com.sakurafish.pockettoushituryou.data.db.entity.orma.Kinds
import com.sakurafish.pockettoushituryou.repository.FoodsRepository
import com.sakurafish.pockettoushituryou.repository.KindsRepository
import com.sakurafish.pockettoushituryou.store.Action
import com.sakurafish.pockettoushituryou.store.Dispatcher
import com.sakurafish.pockettoushituryou.view.helper.ShowcaseHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class FoodsViewModel @Inject constructor(
        private val foodsRepository: FoodsRepository,
        private val kindsRepository: KindsRepository,
        private val dispatcher: Dispatcher,
        private val showcaseHelper: ShowcaseHelper
) : ViewModel() {

    private var typeId = 1

    private val _foods = MutableLiveData<List<Foods>>()
    val foods: LiveData<List<Foods>> = _foods

    private val _kinds = MutableLiveData<List<Kinds>>()
    val kinds: LiveData<List<Kinds>> = _kinds

    private val _isLoading = MutableLiveData<Boolean>().apply {
        value = true
    }
    val isLoading: LiveData<Boolean> = _isLoading

    fun setTypeId(@IntRange(from = 1, to = 6) typeId: Int) {
        this.typeId = typeId
    }

    fun findKinds() {
        viewModelScope.launch(Dispatchers.IO) {
            val kinds = kindsRepository.findByType(typeId)
            _kinds.postValue(kinds)
            Timber.tag(TAG).d("type:" + typeId + " kinds size:" + kinds.size)
        }
    }

    fun findFoods(kindId: Int, @IntRange(from = 0, to = 5) sort: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            enableIsLoading(true)
            val foods = foodsRepository.findByTypeAndKind(typeId, kindId, sort)
            _foods.postValue(foods)
            Timber.tag(TAG).d(" foods size:" + foods.size)
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
