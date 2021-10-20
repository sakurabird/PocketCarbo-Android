package com.sakurafish.pockettoushituryou.data.repository

import androidx.annotation.WorkerThread
import com.sakurafish.pockettoushituryou.data.db.dao.FavoriteDao
import com.sakurafish.pockettoushituryou.data.db.entity.Favorite
import com.sakurafish.pockettoushituryou.data.db.entity.Food
import java.util.*
import javax.inject.Inject

@WorkerThread
class FavoriteDataSource @Inject
constructor(private val favoriteDao: FavoriteDao) : FavoriteRepository {

    override fun delete(favorite: Favorite) {
        favoriteDao.delete(favorite)
    }

    override fun save(favorite: Favorite) {
        favoriteDao.save(favorite)
    }

    override fun isFavorite(foodId: Int): Boolean {
        return favoriteDao.isFavorite(foodId)
    }

    override fun findByFoodId(foodId: Int): Favorite? {
        return favoriteDao.findByFoodId(foodId)
    }

    override fun findAll(): List<Food> {
        val foods = ArrayList<Food>()
        favoriteDao.findAll().map { favorite -> foods += favorite.food }
        return foods
    }

    override fun insertAll(favorites: List<Favorite>) {
        favoriteDao.insertAll(favorites)
    }
}