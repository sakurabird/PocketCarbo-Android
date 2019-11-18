package com.sakurafish.pockettoushituryou.repository

import androidx.annotation.WorkerThread
import com.sakurafish.pockettoushituryou.data.db.entity.FavoriteFoods
import com.sakurafish.pockettoushituryou.data.db.entity.Foods
import com.sakurafish.pockettoushituryou.data.db.entity.OrmaDatabase
import com.sakurafish.pockettoushituryou.shared.rxbus.FavoritesUpdateEvent
import com.sakurafish.pockettoushituryou.shared.rxbus.RxBus
import com.sakurafish.pockettoushituryou.viewmodel.HostClass
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FavoriteFoodsRepository @Inject
internal constructor(private val orma: OrmaDatabase) {

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

        val rxBus = RxBus.getIntanceBus()
        rxBus.post(FavoritesUpdateEvent(hostClass, foods.id))
    }

    @WorkerThread
    fun save(foods: Foods, hostClass: HostClass) {
        orma.relationOfFavoriteFoods().deleter().foodsEq(foods.id).execute()
        orma.relationOfFavoriteFoods().inserter().execute(FavoriteFoods(foods, Date()))

        val rxBus = RxBus.getIntanceBus()
        rxBus.post(FavoritesUpdateEvent(hostClass, foods.id))
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