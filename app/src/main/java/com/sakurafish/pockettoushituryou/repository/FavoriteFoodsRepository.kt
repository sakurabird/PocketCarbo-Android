package com.sakurafish.pockettoushituryou.repository

import androidx.annotation.WorkerThread
import com.sakurafish.pockettoushituryou.data.db.entity.FavoriteFoods
import com.sakurafish.pockettoushituryou.data.db.entity.Foods
import com.sakurafish.pockettoushituryou.data.db.entity.OrmaDatabase
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
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
    fun delete(foods: Foods): Single<Int> {
        return orma.relationOfFavoriteFoods().deleter().foodsEq(foods.id).executeAsSingle()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    @WorkerThread
    fun save(foods: Foods): Completable {
        return orma.transactionAsCompletable {
            orma.relationOfFavoriteFoods().deleter().foodsEq(foods.id).execute()
            orma.relationOfFavoriteFoods().inserter().execute(FavoriteFoods(foods, Date()))
        }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
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