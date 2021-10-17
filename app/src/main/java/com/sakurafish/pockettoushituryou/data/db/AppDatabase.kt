package com.sakurafish.pockettoushituryou.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.sakurafish.pockettoushituryou.data.db.dao.FavoriteDao
import com.sakurafish.pockettoushituryou.data.db.dao.FoodDao
import com.sakurafish.pockettoushituryou.data.db.dao.KindDao
import com.sakurafish.pockettoushituryou.data.db.entity.Favorite
import com.sakurafish.pockettoushituryou.data.db.entity.Food
import com.sakurafish.pockettoushituryou.data.db.entity.Kind

/**
 * AppDatabase class for Room since version 2.5.1
 */
@Database(
    entities = [
        (Food::class),
        (Kind::class),
        (Favorite::class)
    ],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun foodDao(): FoodDao
    abstract fun kindDao(): KindDao
    abstract fun favoriteDao(): FavoriteDao
}