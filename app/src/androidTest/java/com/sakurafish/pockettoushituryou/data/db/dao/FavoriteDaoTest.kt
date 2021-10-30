package com.sakurafish.pockettoushituryou.data.db.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.sakurafish.pockettoushituryou.*
import com.sakurafish.pockettoushituryou.data.db.AppDatabase
import com.sakurafish.pockettoushituryou.data.db.entity.Favorite
import com.sakurafish.pockettoushituryou.data.db.entity.Food
import org.hamcrest.Matchers
import org.junit.After
import org.junit.Assert.assertNull
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4ClassRunner::class)
class FavoriteDaoTest {
    private lateinit var favoriteDao: FavoriteDao
    private lateinit var db: AppDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, AppDatabase::class.java
        ).build()
        favoriteDao = db.favoriteDao()
        favoriteDao.insertAll(listOf(favoriteA, favoriteB, favoriteC, favoriteD))
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun findAll() {
        favoriteDao.insertAll(listOf(favoriteA, favoriteB, favoriteC, favoriteD))
        val favorites = favoriteDao.findAll()
        assertThat(favorites.size, Matchers.equalTo(4))
        assertThat(favorites[0], Matchers.equalTo(favoriteD))
        assertThat(favorites[1], Matchers.equalTo(favoriteC))
        assertThat(favorites[2], Matchers.equalTo(favoriteB))
        assertThat(favorites[3], Matchers.equalTo(favoriteA))
    }

    @Test
    fun isFavorite() {
        var isFavorite = favoriteDao.isFavorite(1)
        assertThat(isFavorite, Matchers.equalTo(true))
        isFavorite = favoriteDao.isFavorite(4)
        assertThat(isFavorite, Matchers.equalTo(true))
        isFavorite = favoriteDao.isFavorite(0)
        assertThat(isFavorite, Matchers.equalTo(false))
        isFavorite = favoriteDao.isFavorite(5)
        assertThat(isFavorite, Matchers.equalTo(false))
        isFavorite = favoriteDao.isFavorite(-1)
        assertThat(isFavorite, Matchers.equalTo(false))
    }

    @Test
    fun findByFoodId() {
        var favorite = favoriteDao.findByFoodId(2)
        assertThat(favorite?.foodId, Matchers.equalTo(2))
        assertThat(favorite?.food, Matchers.equalTo(foodB))
        assertThat(favorite?.createdAt, Matchers.equalTo(100L))

        favorite = favoriteDao.findByFoodId(0)
        assertNull(favorite)
    }

    @Test
    fun save() {
        val food = Food(
            5,
            "E",
            154,
            "1個",
            49.8f,
            47.8f,
            371f,
            11f,
            16.4f,
            10.3f,
            1.1f,
            "たまちゃん",
            "備考1",
            2,
            1,
            kindA
        )

        val favorite = Favorite(5, food, 30)

        favoriteDao.save(favorite)
        var favorites = favoriteDao.findAll()
        assertThat(favorites.size, Matchers.equalTo(5))
        assertThat(favorites[0], Matchers.equalTo(favoriteD))
        assertThat(favorites[1], Matchers.equalTo(favoriteC))
        assertThat(favorites[2], Matchers.equalTo(favoriteB))
        assertThat(favorites[3], Matchers.equalTo(favorite))
        assertThat(favorites[4], Matchers.equalTo(favoriteA))
    }

    @Test
    fun delete() {
        favoriteDao.delete(favoriteB)
        var favorites = favoriteDao.findAll()
        assertThat(favorites.size, Matchers.equalTo(3))
        assertThat(favorites[0], Matchers.equalTo(favoriteD))
        assertThat(favorites[1], Matchers.equalTo(favoriteC))
        assertThat(favorites[2], Matchers.equalTo(favoriteA))
    }

    @Test
    fun deleteAll() {
        favoriteDao.deleteAll()
        var favorites = favoriteDao.findAll()
        assertThat(favorites.size, Matchers.equalTo(0))
    }
}