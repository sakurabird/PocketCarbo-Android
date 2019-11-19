package com.sakurafish.pockettoushituryou.data.db.entity

import com.github.gfx.android.orma.annotation.Column
import com.github.gfx.android.orma.annotation.PrimaryKey
import com.github.gfx.android.orma.annotation.Table
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
@Table
class Foods {

    @PrimaryKey(auto = false)
    @Column(value = "id", indexed = true)
    @Json(name = "id")
    var id: Int = 0

    @Column(value = "name", indexed = true)
    @Json(name = "name")
    var name: String? = null

    @Column(value = "weight")
    @Json(name = "weight")
    var weight: Int = 0

    @Column(value = "weight_hint")
    @Json(name = "weight_hint")
    var weightHint: String? = null

    @Column(value = "carbohydrate_per_100g", indexed = true)
    @Json(name = "carbohydrate_per_100g")
    var carbohydratePer100g: Float = 0.toFloat()

    @Column(value = "carbohydrate_per_weight")
    @Json(name = "carbohydrate_per_weight")
    var carbohydratePerWeight: Float = 0.toFloat()

    @Column(value = "calory")
    @Json(name = "calory")
    var calory: Float = 0.toFloat()

    @Column(value = "protein")
    @Json(name = "protein")
    var protein: Float = 0.toFloat()

    @Column(value = "fat")
    @Json(name = "fat")
    var fat: Float = 0.toFloat()

    @Column(value = "sodium")
    @Json(name = "sodium")
    var sodium: Float = 0.toFloat()

    @Column(value = "search_word", indexed = true)
    @Json(name = "search_word")
    var searchWord: String? = null

    @Column(value = "notes")
    @Json(name = "notes")
    var notes: String? = null

    @Column(value = "type_id", indexed = true)
    @Json(name = "type_id")
    var typeId: Int = 0

    @Column(value = "kind_id", indexed = true)
    @Json(name = "kind_id")
    var kindId: Int = 0

    @Column(value = "kinds", indexed = true)
    @Transient
    var kinds: Kinds? = null

    override fun toString(): String {
        val builder = StringBuilder()
        builder.append("Foods id:")
        builder.append(id)
        builder.append(" name:")
        builder.append(name)
        builder.append(" weight:")
        builder.append(weight)
        builder.append(" weightHint:")
        builder.append(weightHint)
        builder.append(" carbohydratePer100g:")
        builder.append(carbohydratePer100g)
        builder.append(" carbohydratePerWeight:")
        builder.append(carbohydratePerWeight)
        builder.append(" calory:")
        builder.append(calory)
        builder.append(" protein:")
        builder.append(protein)
        builder.append(" fat:")
        builder.append(fat)
        builder.append(" sodium:")
        builder.append(sodium)
        builder.append(" searchWord:")
        builder.append(searchWord)
        builder.append(" notes:")
        builder.append(notes)
        builder.append(" typeId:")
        builder.append(typeId)
        builder.append(" kindId:")
        builder.append(kindId)

        return builder.toString()
    }
}
