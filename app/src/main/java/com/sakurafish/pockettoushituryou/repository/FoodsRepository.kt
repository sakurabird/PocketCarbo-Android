package com.sakurafish.pockettoushituryou.repository

import android.content.Context
import android.database.Cursor
import androidx.annotation.IntRange
import androidx.annotation.WorkerThread
import com.sakurafish.pockettoushituryou.data.db.entity.Foods
import com.sakurafish.pockettoushituryou.data.db.entity.OrmaDatabase
import com.sakurafish.pockettoushituryou.data.local.FoodsData
import com.sakurafish.pockettoushituryou.data.local.LocalJsonResolver
import com.sakurafish.pockettoushituryou.shared.rxbus.FoodsUpdatedEvent
import com.sakurafish.pockettoushituryou.shared.rxbus.RxBus
import com.squareup.moshi.Moshi
import io.reactivex.Single
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.io.IOException
import java.util.*
import javax.inject.Inject

class FoodsRepository @Inject internal constructor(
        private val orma: OrmaDatabase,
        private val context: Context,
        private val moshi: Moshi
) {

    @WorkerThread
    fun findAllFromAssets(): Single<FoodsData> {
        val json: String
        return try {
            json = LocalJsonResolver.loadJsonFromAsset(context, "json/foods_and_kinds.json")
            val jsonAdapter = moshi.adapter(FoodsData::class.java)
            val foodsData = jsonAdapter.fromJson(json)
            Timber.tag("test2").e("loaded json foods:" + foodsData?.foods?.size)
            foodsData?.let {
                updateAllAsync(foodsData)
            }
            Single.create { emitter -> emitter.onSuccess(foodsData!!) }
        } catch (e: IOException) {
            e.printStackTrace()
            Single.create { emitter -> emitter.onError(e) }
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
        val list = ArrayList<Foods>(cursor.count)
        try {
            var pos = 0
            while (cursor.moveToPosition(pos)) {
                val foods = orma.relationOfFoods().selector().newModelFromCursor(cursor)
                list.add(foods)
                pos++
            }
        } finally {
            cursor.close()
        }
        return list
    }

    @WorkerThread
    private fun updateAllAsync(foodsData: FoodsData): Disposable {
        return orma.transactionAsCompletable {
            for (foods in foodsData.foods!!) {
                orma.relationOfFoods().upsert(foods)
            }
            for (kinds in foodsData.kinds!!) {
                orma.relationOfKinds().upsert(kinds)
            }
        }
                .subscribeOn(Schedulers.io())
                .doOnComplete {
                    Timber.tag(TAG).d("updateAllAsync completed")
                    val rxBus = RxBus.getIntanceBus()
                    rxBus.post(FoodsUpdatedEvent(EVENT_DB_UPDATED))
                }
                .subscribe()
    }

    companion object {
        private val TAG = FoodsRepository::class.java.simpleName

        const val EVENT_DB_UPDATED = "EVENT_DB_UPDATED"
    }
}