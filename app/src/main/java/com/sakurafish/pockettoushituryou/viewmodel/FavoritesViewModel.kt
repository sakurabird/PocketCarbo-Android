package com.sakurafish.pockettoushituryou.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sakurafish.pockettoushituryou.data.db.entity.Foods
import com.sakurafish.pockettoushituryou.repository.FavoriteFoodsRepository
import javax.inject.Inject

class FavoritesViewModel @Inject constructor(
        private val favoriteFoodsRepository: FavoriteFoodsRepository
) : ViewModel() {

    val foods: LiveData<List<Foods>> by lazy {
        val liveData = MutableLiveData<List<Foods>>()
        val favorites = favoriteFoodsRepository.findAll()
        _showEmpty.value = favorites.size == 0
        liveData.value = favorites
        return@lazy liveData
    }

    private val _showEmpty = MutableLiveData<Boolean>().apply { value = false }
    val showEmpty: LiveData<Boolean> = _showEmpty
}
