package com.sakurafish.pockettoushituryou.data.db.entity

import androidx.room.*
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.io.Serializable

/**
 * Entity class for Room since version 2.5.1
 */
@SuppressWarnings(RoomWarnings.PRIMARY_KEY_FROM_EMBEDDED_IS_DROPPED)
@Entity(indices = [Index(value = ["name", "carbohydrate_per_100g", "fat_per100g"])])
@JsonClass(generateAdapter = true)
data class Food(

    @PrimaryKey
    @ColumnInfo(name = "id")
    @Json(name = "id")
    var id: Int = 0,

    @ColumnInfo(name = "name")
    @Json(name = "name")
    var name: String? = null,

    @ColumnInfo(name = "weight")
    @Json(name = "weight")
    var weight: Int = 0,

    @ColumnInfo(name = "weight_hint")
    @Json(name = "weight_hint")
    var weightHint: String? = null,

    @ColumnInfo(name = "carbohydrate_per_100g")
    @Json(name = "carbohydrate_per_100g")
    var carbohydratePer100g: Float = 0.toFloat(),

    @ColumnInfo(name = "carbohydrate_per_weight")
    @Json(name = "carbohydrate_per_weight")
    var carbohydratePerWeight: Float = 0.toFloat(),

    @ColumnInfo(name = "calory")
    @Json(name = "calory")
    var calory: Float = 0.toFloat(),

    @ColumnInfo(name = "protein")
    @Json(name = "protein")
    var protein: Float = 0.toFloat(),

    @ColumnInfo(name = "fat")
    @Json(name = "fat")
    var fat: Float = 0.toFloat(),

    @ColumnInfo(name = "fat_per100g")
    @Json(name = "fat_per100g")
    var fatPer100g: Float? = 0.toFloat(),

    @ColumnInfo(name = "sodium")
    @Json(name = "sodium")
    var sodium: Float = 0.toFloat(),

    @ColumnInfo(name = "search_word")
    @Json(name = "search_word")
    var searchWord: String? = null,

    @ColumnInfo(name = "notes")
    @Json(name = "notes")
    var notes: String? = null,

    @ColumnInfo(name = "type_id")
    @Json(name = "type_id")
    var typeId: Int = 0,

    @ColumnInfo(name = "kind_id")
    @Json(name = "kind_id")
    var kindId: Int = 0,

    @Embedded(prefix = "e_")
    @Transient
    var kind: Kind? = null

) : Serializable

enum class FoodSortOrder(
    val sortNumber: Int,
    val columnName: String,
    val order: String,
    val title: String
) {
    NAME_ASC(0, "name", "ASC", "食品名順"),
    NAME_DESC(1, "name", "DESC", "食品名逆順"),
    CARBOHYDRATE100G_ASC(2, "carbohydrate_per_100g", "ASC", "糖質量の少ない順"),
    CARBOHYDRATE100G_DESC(3, "carbohydrate_per_100g", "DESC", "糖質量の多い順"),
    FAT100G_ASC(4, "fat_per100g", "ASC", "脂質量の少ない順"),
    FAT100G_DESC(5, "fat_per100g", "DESC", "脂質量の多い順");

    companion object {
        val DEFAULT_SORT_ORDER = NAME_ASC

        private val map = values().associateBy(FoodSortOrder::sortNumber)
        fun fromInt(sortNumber: Int) = map[sortNumber]
    }
}
