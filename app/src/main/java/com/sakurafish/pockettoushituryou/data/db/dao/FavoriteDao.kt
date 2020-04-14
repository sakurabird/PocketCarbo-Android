package com.sakurafish.pockettoushituryou.data.db.dao

import androidx.room.*
import com.sakurafish.pockettoushituryou.data.db.entity.Favorite

/**
 * Data access object class for Room since version 3.5.1
 */
@Dao
interface FavoriteDao {

    @Query("SELECT * FROM favorite ORDER BY created_at DESC")
    fun findAll(): List<Favorite>

    @Query("SELECT EXISTS (SELECT * FROM favorite WHERE food_id = :foodId)")
    fun isFavorite(foodId: Int): Boolean

    @Query("SELECT * FROM favorite WHERE food_id = :foodId")
    fun findByFoodId(foodId: Int): Favorite?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(favorite: Favorite)

    @Delete
    fun delete(favorite: Favorite)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(favorites: List<Favorite>)

    @Query("DELETE FROM favorite")
    fun deleteAll()
}
