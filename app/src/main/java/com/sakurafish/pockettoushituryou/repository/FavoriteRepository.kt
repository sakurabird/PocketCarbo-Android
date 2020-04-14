package com.sakurafish.pockettoushituryou.repository

import com.sakurafish.pockettoushituryou.data.db.entity.Favorite
import com.sakurafish.pockettoushituryou.data.db.entity.Food

interface FavoriteRepository {

    fun findAll(): List<Food>

    fun isFavorite(foodId: Int): Boolean

    fun findByFoodId(foodId: Int): Favorite?

    fun save(favorite: Favorite)

    fun delete(favorite: Favorite)

    fun insertAll(favorites: List<Favorite>)
}