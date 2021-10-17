package com.sakurafish.pockettoushituryou.repository

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.sakurafish.pockettoushituryou.*
import com.sakurafish.pockettoushituryou.data.db.AppDatabase
import com.sakurafish.pockettoushituryou.data.db.dao.FoodDao
import com.sakurafish.pockettoushituryou.data.db.dao.KindDao
import com.sakurafish.pockettoushituryou.data.db.entity.FoodSortOrder
import com.squareup.moshi.Moshi
import org.hamcrest.Matchers
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4ClassRunner::class)
class FoodDataSourceTest {
    private lateinit var foodDao: FoodDao
    private lateinit var kindDao: KindDao
    private lateinit var foodDataSource: FoodDataSource
    private lateinit var db: AppDatabase

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, AppDatabase::class.java
        ).build()
        kindDao = db.kindDao()
        kindDao.insertAll(listOf(kindB, kindC, kindA))
        foodDao = db.foodDao()
        foodDataSource = FoodDataSource(
            foodDao,
            ApplicationProvider.getApplicationContext(),
            Moshi.Builder().build()
        )
        foodDao.insertAll(listOf(foodB, foodC, foodA, foodD))
    }

    @After
    @Throws(IOException::class)
    fun tearDown() {
        db.close()
    }

    @Test
    fun findByType() {
        var foods = foodDataSource.findByType(2, FoodSortOrder.NAME_ASC)
        Assert.assertThat(foods.size, Matchers.equalTo(2))
        Assert.assertThat(foods[0], Matchers.equalTo(foodC))

        foods = foodDataSource.findByType(1, FoodSortOrder.CARBOHYDRATE100G_ASC)
        Assert.assertThat(foods.size, Matchers.equalTo(2))
        Assert.assertThat(foods[0], Matchers.equalTo(foodA))
        Assert.assertThat(foods[1], Matchers.equalTo(foodB))

        foods = foodDataSource.findByType(1, FoodSortOrder.CARBOHYDRATE100G_DESC)
        Assert.assertThat(foods.size, Matchers.equalTo(2))
        Assert.assertThat(foods[0], Matchers.equalTo(foodB))
        Assert.assertThat(foods[1], Matchers.equalTo(foodA))
    }

    @Test
    fun findByTypeAndKind() {

        var foods = foodDataSource.findByTypeAndKind(2, 12, FoodSortOrder.NAME_ASC)
        Assert.assertThat(foods.size, Matchers.equalTo(0))

        foods = foodDataSource.findByTypeAndKind(2, 1, FoodSortOrder.NAME_ASC)
        Assert.assertThat(foods.size, Matchers.equalTo(2))
        Assert.assertThat(foods[0], Matchers.equalTo(foodC))
        Assert.assertThat(foods[1], Matchers.equalTo(foodD))

        foods = foodDataSource.findByTypeAndKind(2, 1, FoodSortOrder.NAME_DESC)
        Assert.assertThat(foods.size, Matchers.equalTo(2))
        Assert.assertThat(foods[0], Matchers.equalTo(foodD))
        Assert.assertThat(foods[1], Matchers.equalTo(foodC))

        foods = foodDataSource.findByTypeAndKind(2, 1, FoodSortOrder.FAT100G_ASC)
        Assert.assertThat(foods.size, Matchers.equalTo(2))
        Assert.assertThat(foods[0], Matchers.equalTo(foodC))
        Assert.assertThat(foods[1], Matchers.equalTo(foodD))

        foods = foodDataSource.findByTypeAndKind(2, 1, FoodSortOrder.FAT100G_DESC)
        Assert.assertThat(foods.size, Matchers.equalTo(2))
        Assert.assertThat(foods[0], Matchers.equalTo(foodD))
        Assert.assertThat(foods[1], Matchers.equalTo(foodC))
    }

    @Test
    fun search() {
        var foods = foodDataSource.search("たま")
        Assert.assertThat(foods.size, Matchers.equalTo(3))

        Assert.assertThat(foods[0], Matchers.equalTo(foodA))
        Assert.assertThat(foods[1], Matchers.equalTo(foodB))
        Assert.assertThat(foods[2], Matchers.equalTo(foodD))

        foods = foodDataSource.search("う")
        Assert.assertThat(foods.size, Matchers.equalTo(0))

        foods = foodDataSource.search("A")
        Assert.assertThat(foods.size, Matchers.equalTo(4))
        Assert.assertThat(foods[0], Matchers.equalTo(foodA))
        Assert.assertThat(foods[1], Matchers.equalTo(foodB))
        Assert.assertThat(foods[2], Matchers.equalTo(foodC))
        Assert.assertThat(foods[3], Matchers.equalTo(foodD))

        foods = foodDataSource.search("たま d")
        Assert.assertThat(foods.size, Matchers.equalTo(1))
        Assert.assertThat(foods[0], Matchers.equalTo(foodD))
    }

    @Test
    fun createSearchQueryString() {
        var string = foodDataSource.createSearchQueryString("たま")
        Assert.assertThat(
            string, Matchers.equalTo(
                "SELECT food.* FROM food INNER JOIN kind ON kind.id = food.kind_id" +
                        " WHERE ((food.name LIKE '%たま%' OR food.search_word LIKE '%たま%')" +
                        " OR (kind.name LIKE '%たま%' OR kind.search_word LIKE '%たま%'))" +
                        " ORDER BY food.name ASC"
            )
        )

        string = foodDataSource.createSearchQueryString(" 　たま 　  d ")
        Assert.assertThat(
            string, Matchers.equalTo(
                "SELECT food.* FROM food INNER JOIN kind ON kind.id = food.kind_id" +
                        " WHERE (((food.name LIKE '%たま%' OR food.search_word LIKE '%たま%')" +
                        " OR (kind.name LIKE '%たま%' OR kind.search_word LIKE '%たま%'))" +
                        " AND " +
                        "((food.name LIKE '%d%' OR food.search_word LIKE '%d%')" +
                        " OR (kind.name LIKE '%d%' OR kind.search_word LIKE '%d%')))" +
                        " ORDER BY food.name ASC"
            )
        )
    }
}