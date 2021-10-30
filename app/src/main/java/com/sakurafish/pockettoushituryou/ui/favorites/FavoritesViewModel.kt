package com.sakurafish.pockettoushituryou.ui.favorites

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.sakurafish.pockettoushituryou.data.repository.FavoriteRepository
import com.sakurafish.pockettoushituryou.di.module.IoDispatcher
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    @IoDispatcher
    private val ioDispatcher: CoroutineDispatcher,
    private val favoriteRepository: FavoriteRepository
) : ViewModel() {

    var foods = liveData(ioDispatcher) {
        val favorites = favoriteRepository.findAll()
        _showEmpty.postValue(favorites.isEmpty())
        emit(favorites)
    }

    private val _showEmpty = MutableLiveData<Boolean>().apply { value = false }
    val showEmpty: LiveData<Boolean> = _showEmpty
}
