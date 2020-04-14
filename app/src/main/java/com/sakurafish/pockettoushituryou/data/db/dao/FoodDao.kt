package com.sakurafish.pockettoushituryou.data.db.dao

import androidx.room.*
import androidx.sqlite.db.SupportSQLiteQuery
import com.sakurafish.pockettoushituryou.data.db.entity.Food

/**
 * Data access object class for Room since version 3.5.1
 */
@Dao
interface FoodDao {

    @Query("SELECT count(*) FROM food")
    fun count(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(foods: List<Food>?)

    @Query("DELETE FROM food")
    fun deleteAll()

    @RawQuery
    fun findByType(supportSQLiteQuery: SupportSQLiteQuery): List<Food>

    @RawQuery
    fun findByTypeAndKind(supportSQLiteQuery: SupportSQLiteQuery): List<Food>

    @RawQuery
    fun search(supportSQLiteQuery: SupportSQLiteQuery): List<Food>
}
