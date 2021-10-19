package com.sakurafish.pockettoushituryou.viewmodel

import android.content.Context
import androidx.annotation.VisibleForTesting
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sakurafish.pockettoushituryou.R
import com.sakurafish.pockettoushituryou.data.db.entity.Favorite
import com.sakurafish.pockettoushituryou.data.db.entity.Food
import com.sakurafish.pockettoushituryou.data.db.entity.Kind
import com.sakurafish.pockettoushituryou.data.db.entity.orma.FavoriteFoods
import com.sakurafish.pockettoushituryou.data.db.entity.orma.OrmaDatabase
import com.sakurafish.pockettoushituryou.repository.FavoriteRepository
import com.sakurafish.pockettoushituryou.repository.FoodRepository
import com.sakurafish.pockettoushituryou.repository.KindRepository
import com.sakurafish.pockettoushituryou.shared.Pref
import com.sakurafish.pockettoushituryou.shared.events.Events
import com.sakurafish.pockettoushituryou.shared.events.PopulateState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    @ApplicationContext private val appContext: Context,
    private val pref: Pref,
    private val foodRepository: FoodRepository,
    private val kindRepository: KindRepository,
    private val favoriteRepository: FavoriteRepository,
    private val events: Events,
    private val orma: OrmaDatabase
) : ViewModel() {

    private val _preventClick = MutableLiveData<Boolean>().apply {
        value = false
    }
    val preventClick: LiveData<Boolean> = _preventClick

    init {
        viewModelScope.launch(Dispatchers.IO) {
            if (mustMigrateToRoom(orma)) {
                migrateToRoom(orma, favoriteRepository)
            }
            if (mustPopulate()) {
                events.setDataPopulateState(PopulateState.POPULATE)
                populateDB()
                events.setDataPopulateState(PopulateState.POPULATED)
            } else {
                events.setDataPopulateState(PopulateState.POPULATED)
            }
        }
    }

    @WorkerThread
    private fun mustPopulate(): Boolean {
        val latestDataVersion = appContext.resources.getInteger(R.integer.data_version)
        val savedDataVersion = pref.getPrefInt(appContext.getString(R.string.PREF_DATA_VERSION))
        Timber.tag(TAG).d("DB is maintained latest version:%d", latestDataVersion)
        return foodRepository.count() == 0
                || kindRepository.count() == 0
                || latestDataVersion != savedDataVersion
    }

    @WorkerThread
    private fun populateDB() {
        // Insert initial data or Update all data
        val json = foodRepository.parseJsonToFoodsData()
        json?.let {
            kindRepository.deleteAll()
            kindRepository.insertAll(it.kinds)
            foodRepository.deleteAll()
            it.foods?.let { foods ->
                for (food in foods) {
                    val kind = kindRepository.findById(food.kindId)
                    food.kind = kind
                }
            }
            foodRepository.insertAll(it.foods)

            pref.setPref(appContext.getString(R.string.PREF_DATA_VERSION), it.dataVersion)
        }
    }

    @WorkerThread
    @VisibleForTesting
    fun mustMigrateToRoom(orma: OrmaDatabase): Boolean {
        val oldFavorites = orma.relationOfFavoriteFoods().selector().toList()
        if (oldFavorites.size == 0) pref.setPref(
            appContext.getString(R.string.PREF_FINISH_MIGRATE_ROOM),
            true
        )
        val finishMigrate =
            pref.getPrefBool(appContext.getString(R.string.PREF_FINISH_MIGRATE_ROOM), false)
        return !finishMigrate
    }

    @WorkerThread
    @VisibleForTesting
    fun migrateToRoom(orma: OrmaDatabase, favoriteRepository: FavoriteRepository) {
        val oldFavorites = orma.relationOfFavoriteFoods().selector().toList()
        oldFavorites.forEach {
            val food = createFoodObject(it)
            var createdAt = 0L
            it.createdAt?.let { date -> createdAt = date.time }
            val favorite = Favorite(it.foods.id, food, createdAt)
            favoriteRepository.save(favorite)
        }
        pref.setPref(appContext.getString(R.string.PREF_FINISH_MIGRATE_ROOM), true)
    }

    @VisibleForTesting
    fun createFoodObject(favoriteFoods: FavoriteFoods): Food {
        val food = Food()
        food.id = favoriteFoods.foods.id
        food.name = favoriteFoods.foods.name
        food.weight = favoriteFoods.foods.weight
        food.weightHint = favoriteFoods.foods.weightHint
        food.carbohydratePer100g = favoriteFoods.foods.carbohydratePer100g
        food.carbohydratePerWeight = favoriteFoods.foods.carbohydratePerWeight
        food.calory = favoriteFoods.foods.calory
        food.protein = favoriteFoods.foods.protein
        food.fat = favoriteFoods.foods.fat
        food.fatPer100g = favoriteFoods.foods.fatPer100g
        food.sodium = favoriteFoods.foods.sodium
        food.searchWord = favoriteFoods.foods.searchWord
        food.notes = favoriteFoods.foods.notes
        food.typeId = favoriteFoods.foods.typeId
        food.kindId = favoriteFoods.foods.kindId
        val kind = Kind()
        kind.id = favoriteFoods.foods.kinds!!.id
        kind.name = favoriteFoods.foods.kinds!!.name
        kind.searchWord = favoriteFoods.foods.kinds!!.searchWord
        kind.typeId = favoriteFoods.foods.kinds!!.typeId
        food.kind = kind
        return food
    }

    fun enablePreventClick(enable: Boolean) {
        _preventClick.value = enable
    }

    companion object {
        private val TAG = MainViewModel::class.java.simpleName
    }
}