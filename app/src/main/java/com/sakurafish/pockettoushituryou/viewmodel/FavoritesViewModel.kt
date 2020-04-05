package com.sakurafish.pockettoushituryou.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.sakurafish.pockettoushituryou.repository.orma.FavoriteFoodsRepository
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

class FavoritesViewModel @Inject constructor(
        private val favoriteFoodsRepository: FavoriteFoodsRepository
) : ViewModel() {

    var foods = liveData(Dispatchers.IO) {
        val favorites = favoriteFoodsRepository.findAll()
        _showEmpty.postValue(favorites.isEmpty())
        emit(favorites)
    }

    private val _showEmpty = MutableLiveData<Boolean>().apply { value = false }
    val showEmpty: LiveData<Boolean> = _showEmpty
}
