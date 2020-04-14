package com.sakurafish.pockettoushituryou.data.db.dao

import android.content.Context
import androidx.room.Room
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.test.core.app.ApplicationProvider
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.sakurafish.pockettoushituryou.*
import com.sakurafish.pockettoushituryou.data.db.AppDatabase
import org.hamcrest.Matchers
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4ClassRunner::class)
class FoodDaoTest {
    private lateinit var foodDao: FoodDao
    private lateinit var db: AppDatabase

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
                context, AppDatabase::class.java).build()
        val kindDao = db.kindDao()
        kindDao.insertAll(listOf(kindB, kindC, kindA))
        foodDao = db.foodDao()
        foodDao.insertAll(listOf(foodB, foodC, foodA, foodD))
    }

    @After
    @Throws(IOException::class)
    fun tearDown() {
        db.close()
    }

    @Test
    fun count() {
        val count = foodDao.count()
        Assert.assertThat(count, Matchers.equalTo(4))
    }

    @Test
    fun deleteAll() {
        foodDao.deleteAll()
        val count = foodDao.count()
        Assert.assertThat(count, Matchers.equalTo(0))
    }

    @Test
    fun findByType() {
        var queryString = "SELECT * FROM food WHERE type_id = 2 ORDER BY name ASC"
        var foods = foodDao.findByType(SimpleSQLiteQuery(queryString))
        Assert.assertThat(foods.size, Matchers.equalTo(2))
        Assert.assertThat(foods[0], Matchers.equalTo(foodC))

        queryString = "SELECT * FROM food WHERE type_id = 1 ORDER BY carbohydrate_per_100g DESC"
        foods = foodDao.findByType(SimpleSQLiteQuery(queryString))
        Assert.assertThat(foods.size, Matchers.equalTo(2))
        Assert.assertThat(foods[0], Matchers.equalTo(foodB))
        Assert.assertThat(foods[1], Matchers.equalTo(foodA))
    }

    @Test
    fun findByTypeAndKind() {
        var queryString = "SELECT * FROM food WHERE type_id = 2 AND kind_id = 1 ORDER BY name DESC"
        var foods = foodDao.findByType(SimpleSQLiteQuery(queryString))
        Assert.assertThat(foods.size, Matchers.equalTo(2))
        Assert.assertThat(foods[0], Matchers.equalTo(foodD))
        Assert.assertThat(foods[1], Matchers.equalTo(foodC))
    }

    @Test
    fun search() {
        var queryString = ""

        queryString += "SELECT food.* FROM food INNER JOIN kind ON kind.id = food.kind_id"
        queryString += " WHERE ((food.name LIKE '%たま%' OR food.search_word LIKE '%たま%')"
        queryString += " OR "
        queryString += "(kind.name LIKE '%たま%' OR kind.search_word LIKE '%たま%')"
        queryString += ") ORDER BY food.name DESC"

        var foods = foodDao.search(SimpleSQLiteQuery(queryString))

        Assert.assertThat(foods.size, Matchers.equalTo(3))
        Assert.assertThat(foods[0], Matchers.equalTo(foodD))
        Assert.assertThat(foods[1], Matchers.equalTo(foodB))
        Assert.assertThat(foods[2], Matchers.equalTo(foodA))

        queryString = ""
        queryString += "SELECT food.* FROM food INNER JOIN kind ON kind.id = food.kind_id"
        queryString += " WHERE ((food.name LIKE '%たま%' OR food.search_word LIKE '%たま%')"
        queryString += " OR "
        queryString += "(kind.name LIKE '%たま%' OR kind.search_word LIKE '%たま%')"
        queryString += ") ORDER BY food.name ASC"

        foods = foodDao.search(SimpleSQLiteQuery(queryString))
        Assert.assertThat(foods.size, Matchers.equalTo(3))
        Assert.assertThat(foods[0], Matchers.equalTo(foodA))
        Assert.assertThat(foods[1], Matchers.equalTo(foodB))
        Assert.assertThat(foods[2], Matchers.equalTo(foodD))

        // "A" Kind.name
        queryString = ""
        queryString += "SELECT food.* FROM food INNER JOIN kind ON kind.id = food.kind_id"
        queryString += " WHERE ((food.name LIKE '%A%' OR food.search_word LIKE '%A%')"
        queryString += " OR "
        queryString += "(kind.name LIKE '%A%' OR kind.search_word LIKE '%A%')"
        queryString += ") ORDER BY food.name ASC"

        foods = foodDao.search(SimpleSQLiteQuery(queryString))
        Assert.assertThat(foods.size, Matchers.equalTo(4))
        Assert.assertThat(foods[0], Matchers.equalTo(foodA)) // Food.name = "A"
        Assert.assertThat(foods[1], Matchers.equalTo(foodB))
        Assert.assertThat(foods[2], Matchers.equalTo(foodC))
        Assert.assertThat(foods[3], Matchers.equalTo(foodD))
    }
}