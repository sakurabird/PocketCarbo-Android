package com.sakurafish.pockettoushituryou.viewmodel

import android.app.Application
import android.content.Context
import android.preference.PreferenceManager
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import com.sakurafish.pockettoushituryou.R
import com.sakurafish.pockettoushituryou.data.db.AppDatabase
import com.sakurafish.pockettoushituryou.data.db.entity.orma.FavoriteFoods
import com.sakurafish.pockettoushituryou.data.db.entity.orma.Foods
import com.sakurafish.pockettoushituryou.data.db.entity.orma.Kinds
import com.sakurafish.pockettoushituryou.data.db.entity.orma.OrmaDatabase
import com.sakurafish.pockettoushituryou.repository.*
import com.sakurafish.pockettoushituryou.shared.Pref
import com.sakurafish.pockettoushituryou.store.Dispatcher
import com.squareup.moshi.Moshi
import org.hamcrest.Matchers
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.*

class MainViewModelTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    private lateinit var context: Context
    private lateinit var mainViewModel: MainViewModel
    private lateinit var orma: OrmaDatabase
    private lateinit var appDatabase: AppDatabase
    private lateinit var pref: Pref
    private lateinit var favoriteRepository: FavoriteRepository
    private lateinit var favoriteFoodsA: FavoriteFoods
    private lateinit var favoriteFoodsB: FavoriteFoods

    @Before
    fun setUp() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
        val app = InstrumentationRegistry.getInstrumentation().targetContext
                .applicationContext as Application

        pref = Pref(PreferenceManager.getDefaultSharedPreferences(app))
        orma = OrmaDatabase.builder(app).name("orma-test.db").trace(true).build()
        appDatabase = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        val foodRepository: FoodRepository = FoodDataSource(appDatabase.foodDao(), app, Moshi.Builder().build())
        val kindRepository: KindRepository = KindDataSource(appDatabase.kindDao())
        favoriteRepository = FavoriteDataSource(appDatabase.favoriteDao())

        mainViewModel = MainViewModel(app, pref, foodRepository, kindRepository, favoriteRepository, Dispatcher(), orma)

        makeData()
        appDatabase.favoriteDao().deleteAll()
    }

    private fun makeData() {
        orma.relationOfKinds().deleter().execute()

        val kindsA = Kinds()
        kindsA.id = 3
        kindsA.name = "kindsA"
        kindsA.searchWord = "searchWordA"
        kindsA.typeId = 1
        orma.relationOfKinds().inserter().execute(kindsA)

        val kindsB = Kinds()
        kindsB.id = 5
        kindsB.name = "kindsB"
        kindsB.searchWord = "searchWordB"
        kindsB.typeId = 2
        orma.relationOfKinds().inserter().execute(kindsB)

        orma.relationOfFoods().deleter().execute()

        val foodsA = Foods()
        foodsA.id = 1
        foodsA.name = "A"
        foodsA.weight = 155
        foodsA.weightHint = "1個"
        foodsA.carbohydratePer100g = 30.8f
        foodsA.carbohydratePerWeight = 47.8f
        foodsA.calory = 371f
        foodsA.protein = 11f
        foodsA.fat = 14.4f
        foodsA.fatPer100g = 9.3f
        foodsA.sodium = 1.1f
        foodsA.searchWord = "たまご"
        foodsA.notes = "備考1"
        foodsA.typeId = 1
        foodsA.kindId = 3
        foodsA.kinds = kindsA
        orma.relationOfFoods().inserter().execute(foodsA)

        val foodsB = Foods()
        foodsB.id = 2
        foodsB.name = "B"
        foodsB.weight = 34
        foodsB.weightHint = "1杯"
        foodsB.carbohydratePer100g = 3.3f
        foodsB.carbohydratePerWeight = 10.8f
        foodsB.calory = 67f
        foodsB.protein = 1.2f
        foodsB.fat = 0.1f
        foodsB.fatPer100g = 0.3f
        foodsB.sodium = 0f
        foodsB.searchWord = "苺ドリンク"
        foodsB.notes = "備考2"
        foodsB.typeId = 2
        foodsB.kindId = 5
        foodsB.kinds = kindsB
        orma.relationOfFoods().inserter().execute(foodsB)

        orma.relationOfFavoriteFoods().deleter().execute()

        favoriteFoodsA = FavoriteFoods()
        favoriteFoodsA.id = 1
        favoriteFoodsA.foods = foodsA
        favoriteFoodsA.createdAt = Date(2020, 4, 13)
        orma.relationOfFavoriteFoods().inserter().execute(favoriteFoodsA)


        favoriteFoodsB = FavoriteFoods()
        favoriteFoodsB.id = 2
        favoriteFoodsB.foods = foodsB
        favoriteFoodsB.createdAt = Date(2020, 5, 30)
        orma.relationOfFavoriteFoods().inserter().execute(favoriteFoodsB)
    }

    @After
    fun tearDown() {
        appDatabase.close()
    }

    @Test
    fun mustMigrateToRoom() {
        pref.setPref(context.getString(R.string.PREF_FINISH_MIGRATE_ROOM), false)
        val favoritesSize = orma.relationOfFavoriteFoods().count()
        assertThat(favoritesSize, Matchers.equalTo(2))
        assertTrue(mainViewModel.mustMigrateToRoom(orma))

        pref.setPref(context.getString(R.string.PREF_FINISH_MIGRATE_ROOM), true)
        assertFalse(mainViewModel.mustMigrateToRoom(orma))

        pref.setPref(context.getString(R.string.PREF_FINISH_MIGRATE_ROOM), false)
        orma.relationOfFavoriteFoods().deleter().execute()
        assertFalse(mainViewModel.mustMigrateToRoom(orma))
    }

    @Test
    fun migrateToRoom() {
        var favorites = appDatabase.favoriteDao().findAll()
        assertThat(favorites.size, Matchers.equalTo(0))

        mainViewModel.migrateToRoom(orma, favoriteRepository)

        favorites = appDatabase.favoriteDao().findAll()
        assertThat(favorites.size, Matchers.equalTo(2))
        assertThat(favorites[0].foodId, Matchers.equalTo(2))
        assertThat(favorites[0].food.name, Matchers.equalTo("B"))
        val date = Date(favorites[0].createdAt)
        assertThat(date.year, Matchers.equalTo(2020))
        assertThat(date.month, Matchers.equalTo(5))
        assertThat(date.date, Matchers.equalTo(30))

        assertThat(favorites[1].foodId, Matchers.equalTo(1))
        assertThat(favorites[1].food.name, Matchers.equalTo("A"))
    }

    @Test
    fun createFoodObject() {
        val food = mainViewModel.createFoodObject(favoriteFoodsA)
        assertNotNull(food)
        assertThat(food.id, Matchers.equalTo(1))
        assertThat(food.name, Matchers.equalTo("A"))
        assertThat(food.weight, Matchers.equalTo(155))
        assertThat(food.weightHint, Matchers.equalTo("1個"))
        assertThat(food.carbohydratePer100g, Matchers.equalTo(30.8f))
        assertThat(food.carbohydratePerWeight, Matchers.equalTo(47.8f))
        assertThat(food.calory, Matchers.equalTo(371f))
        assertThat(food.protein, Matchers.equalTo(11f))
        assertThat(food.fat, Matchers.equalTo(14.4f))
        assertThat(food.fatPer100g, Matchers.equalTo(9.3f))
        assertThat(food.sodium, Matchers.equalTo(1.1f))
        assertThat(food.searchWord, Matchers.equalTo("たまご"))
        assertThat(food.notes, Matchers.equalTo("備考1"))
        assertThat(food.typeId, Matchers.equalTo(1))
        assertThat(food.kindId, Matchers.equalTo(3))
        assertNotNull(food.kind)
        assertThat(food.kind!!.id, Matchers.equalTo(3))
        assertThat(food.kind!!.name, Matchers.equalTo("kindsA"))
        assertThat(food.kind!!.searchWord, Matchers.equalTo("searchWordA"))
        assertThat(food.kind!!.typeId, Matchers.equalTo(1))
    }
}