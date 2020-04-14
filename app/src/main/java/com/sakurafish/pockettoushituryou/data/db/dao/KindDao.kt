package com.sakurafish.pockettoushituryou.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sakurafish.pockettoushituryou.data.db.entity.Kind

/**
 * Data access object class for Room since version 2.5.1
 */
@Dao
interface KindDao {

    @Query("SELECT count(*) FROM kind")
    fun count(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(kinds: List<Kind>)

    @Query("DELETE FROM kind")
    fun deleteAll()

    @Query("SELECT * FROM kind WHERE id = :id")
    fun findById(id: Int): Kind?

    @Query("SELECT * FROM kind WHERE type_id = :typeId")
    fun findByTypeId(typeId: Int): List<Kind>
}
