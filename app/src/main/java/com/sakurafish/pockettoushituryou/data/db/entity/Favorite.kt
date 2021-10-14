package com.sakurafish.pockettoushituryou.data.db.entity

import androidx.room.*

/**
 * Entity class for Room since version 2.5.1
 */
@SuppressWarnings(
        RoomWarnings.PRIMARY_KEY_FROM_EMBEDDED_IS_DROPPED,
        RoomWarnings.INDEX_FROM_EMBEDDED_ENTITY_IS_DROPPED,
        RoomWarnings.INDEX_FROM_EMBEDDED_FIELD_IS_DROPPED
)
@Entity(indices = [Index(value = ["food_id", "created_at"])])
data class Favorite(

        @PrimaryKey
        @ColumnInfo(name = "food_id")
        var foodId: Int = 0,

        @Embedded
        var food: Food,

        @ColumnInfo(name = "created_at")
        val createdAt: Long = 0
)