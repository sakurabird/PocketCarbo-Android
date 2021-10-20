package com.sakurafish.pockettoushituryou.ui.favorites

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.sakurafish.pockettoushituryou.data.repository.FavoriteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val favoriteRepository: FavoriteRepository
) : ViewModel() {

    var foods = liveData(Dispatchers.IO) {
        val favorites = favoriteRepository.findAll()
        _showEmpty.postValue(favorites.isEmpty())
        emit(favorites)
    }

    private val _showEmpty = MutableLiveData<Boolean>().apply { value = false }
    val showEmpty: LiveData<Boolean> = _showEmpty
}
