package com.sakurafish.pockettoushituryou.di.module

import android.app.Application
import androidx.room.Room
import com.sakurafish.pockettoushituryou.data.db.AppDatabase
import com.sakurafish.pockettoushituryou.data.db.dao.FavoriteDao
import com.sakurafish.pockettoushituryou.data.db.dao.FoodDao
import com.sakurafish.pockettoushituryou.data.db.dao.KindDao
import com.sakurafish.pockettoushituryou.repository.*
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DatabaseModule(app: Application) {

    private val appDatabase: AppDatabase = Room.databaseBuilder(app, AppDatabase::class.java, "pocketcarbo_room.db")
            .build()

    @Singleton
    @Provides
    internal fun provideRoomDatabase(): AppDatabase {
        return appDatabase
    }

    @Singleton
    @Provides
    internal fun provideFoodDao(db: AppDatabase): FoodDao {
        return db.foodDao()
    }

    @Singleton
    @Provides
    internal fun foodRepository(dao: FoodDao, app: Application, moshi: Moshi): FoodRepository {
        return FoodDataSource(dao, app, moshi)
    }

    @Singleton
    @Provides
    internal fun provideKindDao(db: AppDatabase): KindDao {
        return db.kindDao()
    }

    @Singleton
    @Provides
    internal fun kindRepository(dao: KindDao): KindRepository {
        return KindDataSource(dao)
    }

    @Singleton
    @Provides
    internal fun provideFavoriteDao(db: AppDatabase): FavoriteDao {
        return db.favoriteDao()
    }

    @Singleton
    @Provides
    internal fun favoriteRepository(dao: FavoriteDao): FavoriteRepository {
        return FavoriteDataSource(dao)
    }
}