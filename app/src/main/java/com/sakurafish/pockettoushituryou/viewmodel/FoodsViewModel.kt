package com.sakurafish.pockettoushituryou.viewmodel

import androidx.annotation.IntRange
import androidx.lifecycle.*
import com.sakurafish.pockettoushituryou.data.db.entity.Foods
import com.sakurafish.pockettoushituryou.repository.FoodsRepository
import com.sakurafish.pockettoushituryou.repository.KindsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class FoodsViewModel @Inject constructor(
        private val foodsRepository: FoodsRepository,
        private val kindsRepository: KindsRepository
) : ViewModel() {

    private val _foods = MutableLiveData<List<Foods>>()
    val foods: LiveData<List<Foods>> = _foods

    private var typeId = 1

    val kinds = liveData(Dispatchers.IO) {
        val result = kindsRepository.findByType(typeId)
        emit(result)
    }

    private val _isLoading = MutableLiveData<Boolean>().apply {
        value = true
    }
    val isLoading: LiveData<Boolean> = _isLoading

    fun setTypeId(@IntRange(from = 1, to = 6) typeId: Int) {
        this.typeId = typeId
    }

    fun findFoods(kindId: Int, sort: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            enableIsLoading(true)
            val foods = foodsRepository.findByTypeAndKind(typeId, kindId, sort)
            _foods.postValue(foods)
            Timber.tag(TAG).d("type:" + typeId + " foods size:" + foods.size)
            enableIsLoading(false)
        }
    }

    fun enableIsLoading(enable: Boolean) {
        _isLoading.postValue(enable)
    }

    companion object {
        internal val TAG = FoodsViewModel::class.java.simpleName
    }
}
