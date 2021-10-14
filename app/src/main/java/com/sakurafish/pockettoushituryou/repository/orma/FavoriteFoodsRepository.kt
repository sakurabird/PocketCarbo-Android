package com.sakurafish.pockettoushituryou.repository.orma

import androidx.annotation.WorkerThread
import com.sakurafish.pockettoushituryou.data.db.entity.orma.FavoriteFoods
import com.sakurafish.pockettoushituryou.data.db.entity.orma.Foods
import com.sakurafish.pockettoushituryou.data.db.entity.orma.OrmaDatabase
import com.sakurafish.pockettoushituryou.shared.events.Events
import com.sakurafish.pockettoushituryou.shared.events.HostClass
import kotlinx.coroutines.*
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * FavoritesFoods data access class with Orma
 * This class has been deprecated from version 2.5.1 due to migration with the Room library
 */
@Deprecated("This class is not used from version 2.5.1. Use 'FavoriteRepository' and 'FavoriteDao' class")
@Singleton
class FavoriteFoodsRepository @Inject
internal constructor(
        private val orma: OrmaDatabase,
        private val events: Events) {

    // TODO CoroutineScopeはinjectする
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    @WorkerThread
    fun isFavorite(foodsId: Int): Boolean {
        return !orma.relationOfFavoriteFoods().selector().foodsEq(foodsId).isEmpty
    }

    @WorkerThread
    fun findAll(): List<Foods> {
        val foods = ArrayList<Foods>()
        orma.relationOfFavoriteFoods().selector().orderByCreatedAtDesc().toList()
                .map { favorite -> foods += favorite.foods }
        return foods
    }

    @WorkerThread
    fun delete(foods: Foods, hostClass: HostClass) {
        orma.relationOfFavoriteFoods().deleter().foodsEq(foods.id).execute()
        scope.launch {
            events.invokeFavoritesClickedEvent(hostClass)
        }
    }

    @WorkerThread
    fun save(foods: Foods, hostClass: HostClass) {
        orma.relationOfFavoriteFoods().deleter().foodsEq(foods.id).execute()
        orma.relationOfFavoriteFoods().inserter().execute(FavoriteFoods(foods, Date()))
        scope.launch {
            events.invokeFavoritesClickedEvent(hostClass)
        }
    }

    @WorkerThread
    fun toString(foodsId: Int): String {
        val favoriteFoods = orma.relationOfFavoriteFoods().selector().foodsEq(foodsId).value()
        return favoriteFoods.toString()
    }

    companion object {
        private val TAG = FavoriteFoodsRepository::class.java.simpleName
    }
}