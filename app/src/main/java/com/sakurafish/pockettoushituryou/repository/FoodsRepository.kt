package com.sakurafish.pockettoushituryou.repository

import android.content.Context
import android.database.Cursor
import androidx.annotation.IntRange
import androidx.annotation.WorkerThread
import com.sakurafish.pockettoushituryou.data.db.entity.Foods
import com.sakurafish.pockettoushituryou.data.db.entity.OrmaDatabase
import com.sakurafish.pockettoushituryou.data.local.FoodsData
import com.sakurafish.pockettoushituryou.data.local.LocalJsonResolver
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import timber.log.Timber
import java.util.*
import javax.inject.Inject

class FoodsRepository @Inject internal constructor(
        private val orma: OrmaDatabase,
        private val context: Context,
        private val moshi: Moshi
) {

    @WorkerThread
    fun count(): Int {
        return orma.relationOfFoods().count()
    }

    @WorkerThread
    fun insertAll(foodsData: FoodsData?) {
        foodsData.let {
            for (kinds in foodsData?.kinds!!) {
                orma.relationOfKinds().upsert(kinds)
            }
            for (foods in foodsData?.foods!!) {
                foods.kinds = orma.relationOfKinds().selector().idEq(foods.kindId).toList().firstOrNull()
                orma.relationOfFoods().upsert(foods)
            }
            Timber.tag(TAG).d("insertAll completed kinds size:" + it?.kinds!!.size + " foods size:" + it.foods!!.size)
        }
    }

    @WorkerThread
    fun findByTypeAndKind(@IntRange(from = 1, to = 6) typeId: Int, kindId: Int, sort: Int): List<Foods> {
        val selector = orma.relationOfFoods().selector()
        selector.typeIdEq(typeId)
        if (kindId != 0) {
            selector.kindIdEq(kindId)
        }

        when (sort) {
            0 ->
                // 食品名順(default)
                selector.orderByNameAsc()
            1 ->
                // 食品名逆順
                selector.orderByNameDesc()
            2 ->
                // 糖質量の少ない順
                selector.orderByCarbohydratePer100gAsc()
            3 ->
                // 糖質量の少ない順
                selector.orderByCarbohydratePer100gDesc()
        }
        return selector.toList()
    }

    @WorkerThread
    fun search(searchQuery: String?): List<Foods> {
        // TODO LIKE句 nameLike()などのメソッドを使うと部分一致検索が出来ないかも（完全に一致していれば検索できる。SQLを見ると検索語句が%で囲まれていない）
        var query = searchQuery
        if (query == null) {
            // 基本的にqueryがemptyなことはないが、特定の端末でNPEが発生するためこの処理を入れた(ver2.3)
            query = ""
        }
        val word = query.trim { it <= ' ' }.replace("　".toRegex(), " ").split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

        val builder = StringBuilder()
        builder.append("SELECT \"foods\".* FROM \"foods\" INNER JOIN \"kinds\" ON \"kinds\".\"id\" = \"foods\".\"kind_id\" WHERE (")
        for (i in word.indices) {
            val str = word[i]

            if (word.size > 1) {
                builder.append("(")
            }
            builder.append("(\"foods\".\"name\" LIKE \'%")
            builder.append(str)
            builder.append("%\' OR \"foods\".\"search_word\" LIKE \'%")
            builder.append(str)
            builder.append("%\')")
            builder.append(" OR ")
            builder.append("(\"kinds\".\"name\" LIKE \'%")
            builder.append(str)
            builder.append("%\' OR \"kinds\".\"search_word\" LIKE \'%")
            builder.append(str)
            builder.append("%\')")

            if (word.size > 1) {
                builder.append(")")
                if (i != word.size - 1) {
                    builder.append(" AND ")
                }
            }
        }
        builder.append(") ORDER BY \"foods\".\"name\" ASC")

        val cursor = orma.connection.rawQuery(builder.toString())
        return toFoodsList(cursor)
    }

    @WorkerThread
    private fun toFoodsList(cursor: Cursor): List<Foods> {
        val list = ArrayList<Foods>()
        cursor.use { c ->
            var pos = 0
            while (c.moveToPosition(pos)) {

                val model = Foods()
                model.name = if (c.isNull(0)) null else c.getString(0)
                model.weight = c.getInt(1)
                model.weightHint = if (c.isNull(2)) null else c.getString(2)
                model.carbohydratePer100g = c.getFloat(3)
                model.carbohydratePerWeight = c.getFloat(4)
                model.calory = c.getFloat(5)
                model.protein = c.getFloat(6)
                model.fat = c.getFloat(7)
                model.sodium = c.getFloat(8)
                model.searchWord = if (c.isNull(9)) null else c.getString(9)
                model.notes = if (c.isNull(10)) null else c.getString(10)
                model.typeId = c.getInt(11)
                model.kindId = c.getInt(12)
                model.kinds = orma.relationOfKinds().selector().idEq(model.kindId).toList().firstOrNull()
                // ver2.5.0 リレーションのフィールドが間に挟まるとnewModelFromCursorメソッドのcolumnIndexがずれてidのcolumnIndexが18となってクラッシュする
                // java.lang.IllegalStateException: Couldn't read row 0, col 18 from CursorWindow.  Make sure the Cursor is initialized correctly before accessing data from it.
                model.id = c.getInt(14)

                list += model
                pos++
            }
        }
        return list
    }

    @WorkerThread
    fun parseJsonToFoodsData(): FoodsData? {
        val json = LocalJsonResolver.loadJsonFromAsset(context, "json/foods_and_kinds.json")
        val adapter: JsonAdapter<FoodsData> = moshi.adapter(FoodsData::class.java)
        return adapter.fromJson(json)
    }

    companion object {
        private val TAG = FoodsRepository::class.java.simpleName
    }
}