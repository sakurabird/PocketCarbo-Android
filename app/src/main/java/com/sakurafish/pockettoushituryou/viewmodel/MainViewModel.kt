package com.sakurafish.pockettoushituryou.viewmodel

import android.content.Context
import androidx.annotation.WorkerThread
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sakurafish.pockettoushituryou.R
import com.sakurafish.pockettoushituryou.repository.FoodsRepository
import com.sakurafish.pockettoushituryou.repository.KindsRepository
import com.sakurafish.pockettoushituryou.shared.Pref
import com.sakurafish.pockettoushituryou.store.Action
import com.sakurafish.pockettoushituryou.store.Dispatcher
import com.sakurafish.pockettoushituryou.store.FoodsStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@ExperimentalCoroutinesApi
@Singleton
class MainViewModel @Inject constructor(
        private val context: Context,
        private val pref: Pref,
        private val foodsRepository: FoodsRepository,
        private val kindsRepository: KindsRepository,
        private val dispatcher: Dispatcher
) : ViewModel() {

    init {
        viewModelScope.launch(Dispatchers.IO) {
            if (mustPopulate()) {
                dispatcher.dispatch(Action.FoodsLoadingStateChanged(FoodsStore.PopulateState.Populate))
                populateDB()
                dispatcher.dispatch(Action.FoodsLoadingStateChanged(FoodsStore.PopulateState.Populated))
            } else {
                dispatcher.dispatch(Action.FoodsLoadingStateChanged(FoodsStore.PopulateState.Populated))
            }
        }
    }

    @WorkerThread
    private fun mustPopulate(): Boolean {
        val latestDataVersion = context.resources.getInteger(R.integer.data_version)
        val savedDataVersion = pref.getPrefInt(context.getString(R.string.PREF_DATA_VERSION))
        Timber.tag(TAG).d("DB is maintained latest version:%d", latestDataVersion)
        return foodsRepository.count() == 0
                || kindsRepository.count() == 0
                || latestDataVersion != savedDataVersion
    }

    @WorkerThread
    private fun populateDB() {
        // Insert initial data or Update all data
        val json = foodsRepository.parseJsonToFoodsData()
        json?.let {
            foodsRepository.insertAll(it)
            pref.setPref(context.getString(R.string.PREF_DATA_VERSION), it.dataVersion)
        }
    }

    companion object {
        private val TAG = MainViewModel::class.java.simpleName
    }
}