package com.sakurafish.pockettoushituryou.repository

import android.app.Application
import androidx.annotation.VisibleForTesting
import androidx.annotation.WorkerThread
import androidx.sqlite.db.SimpleSQLiteQuery
import com.sakurafish.pockettoushituryou.data.db.dao.FoodDao
import com.sakurafish.pockettoushituryou.data.db.entity.Food
import com.sakurafish.pockettoushituryou.data.db.entity.FoodSortOrder
import com.sakurafish.pockettoushituryou.data.local.FoodsAndKinds
import com.sakurafish.pockettoushituryou.data.local.LocalJsonResolver
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import javax.inject.Inject


@WorkerThread
class FoodDataSource @Inject
constructor(private val foodDao: FoodDao,
            private val app: Application,
            private val moshi: Moshi
) : FoodRepository {

    override fun count(): Int {
        return foodDao.count()
    }

    override fun insertAll(foods: List<Food>?) {
        foodDao.insertAll(foods)
    }

    override fun deleteAll() {
        foodDao.deleteAll()
    }

    override fun findByType(typeId: Int, foodSortOrder: FoodSortOrder): List<Food> {
        var queryString = ""

        queryString += "SELECT * FROM food WHERE type_id = "
        queryString += typeId.toString()
        queryString += " ORDER BY "
        queryString += foodSortOrder.columnName
        queryString += " "
        queryString += foodSortOrder.order

        val simpleSQLiteQuery = SimpleSQLiteQuery(queryString)
        return foodDao.findByTypeAndKind(simpleSQLiteQuery)
    }

    override fun findByTypeAndKind(typeId: Int, kindId: Int, foodSortOrder: FoodSortOrder): List<Food> {
        var queryString = ""

        queryString += "SELECT * FROM food WHERE type_id = "
        queryString += typeId.toString()
        queryString += " AND kind_id ="
        queryString += kindId.toString()
        queryString += " ORDER BY "
        queryString += foodSortOrder.columnName
        queryString += " "
        queryString += foodSortOrder.order

        val simpleSQLiteQuery = SimpleSQLiteQuery(queryString)
        return foodDao.findByTypeAndKind(simpleSQLiteQuery)
    }

    override fun search(searchQuery: String?): List<Food> {
        val queryString = createSearchQueryString(searchQuery ?: "")
        val simpleSQLiteQuery = SimpleSQLiteQuery(queryString)
        return foodDao.search(simpleSQLiteQuery)
    }

    @VisibleForTesting
    fun createSearchQueryString(searchQuery: String): String {
        val word = searchQuery.trim()
                .replace("\\s+".toRegex(), " ")
                .split(" ")
                .dropLastWhile { it.isEmpty() }
                .toTypedArray()

        var queryString = ""
        queryString += "SELECT food.* FROM food INNER JOIN kind ON kind.id = food.kind_id"
        queryString += " WHERE ("
        for (i in word.indices) {
            val str = word[i]

            if (word.size > 1) {
                queryString += "("
            }
            queryString += "(food.name LIKE '%"
            queryString += str
            queryString += "%' OR food.search_word LIKE '%"
            queryString += str
            queryString += "%')"
            queryString += " OR "
            queryString += "(kind.name LIKE '%"
            queryString += str
            queryString += "%' OR kind.search_word LIKE '%"
            queryString += str
            queryString += "%')"

            if (word.size > 1) {
                queryString += ")"
                if (i != word.size - 1) {
                    queryString += " AND "
                }
            }
        }
        queryString += ") ORDER BY food.name ASC"

        return queryString
    }

    override fun parseJsonToFoodsData(): FoodsAndKinds? {
        val json = LocalJsonResolver.loadJsonFromAsset(app, "json/foods_and_kinds.json")
        val adapter: JsonAdapter<FoodsAndKinds> = moshi.adapter(FoodsAndKinds::class.java)
        return adapter.fromJson(json)
    }
}
