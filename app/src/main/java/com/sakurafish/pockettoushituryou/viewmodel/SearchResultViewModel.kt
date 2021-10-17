package com.sakurafish.pockettoushituryou.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sakurafish.pockettoushituryou.data.db.entity.Food
import com.sakurafish.pockettoushituryou.repository.FoodRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class SearchResultViewModel @Inject constructor(
        private val foodRepository: FoodRepository
) : ViewModel() {

    private val query = MutableLiveData<String>()

    private val _foods = MutableLiveData<List<Food>>()
    val foods: LiveData<List<Food>> = _foods

    private val _showEmpty = MutableLiveData<Boolean>().apply { value = false }
    val showEmpty: LiveData<Boolean> = _showEmpty

    fun searchFoods(queryString: String) {
        query.value = queryString
        if (queryString.isBlank()) {
            _showEmpty.value = true
            return
        }
        viewModelScope.launch(Dispatchers.IO) {
            val foods = foodRepository.search(queryString)
            when (foods.size) {
                0 -> _showEmpty.postValue(true)
                else -> _showEmpty.postValue(false)
            }
            _foods.postValue(foods)
        }
    }

    fun lastQueryValue(): String? = query.value

    companion object {
        internal val TAG = SearchResultViewModel::class.java.simpleName
    }
}
