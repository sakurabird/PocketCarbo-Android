package com.sakurafish.pockettoushituryou.di.module

import android.content.Context
import androidx.room.Room
import com.sakurafish.pockettoushituryou.data.db.AppDatabase
import com.sakurafish.pockettoushituryou.data.db.dao.FavoriteDao
import com.sakurafish.pockettoushituryou.data.db.dao.FoodDao
import com.sakurafish.pockettoushituryou.data.db.dao.KindDao
import com.sakurafish.pockettoushituryou.data.db.entity.orma.OrmaDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun provideRoomDatabase(@ApplicationContext appContext: Context): AppDatabase {
        return Room.databaseBuilder(
            appContext,
            AppDatabase::class.java,
            "pocketcarbo_room.db"
        )
            .build()
    }

    @Singleton
    @Provides
    fun provideOrmaDatabase(@ApplicationContext appContext: Context): OrmaDatabase {
        return OrmaDatabase.builder(appContext).trace(false).build()
    }

    @Singleton
    @Provides
    fun provideFoodDao(db: AppDatabase): FoodDao {
        return db.foodDao()
    }

    @Singleton
    @Provides
    fun provideKindDao(db: AppDatabase): KindDao {
        return db.kindDao()
    }

    @Singleton
    @Provides
    fun provideFavoriteDao(db: AppDatabase): FavoriteDao {
        return db.favoriteDao()
    }
}