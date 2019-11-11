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
    @Column(indexed = true)
    @Json(name = "id")
    var id: Int = 0

    @Column(indexed = true)
    @Json(name = "name")
    var name: String? = null

    @Column
    @Json(name = "weight")
    var weight: Int = 0

    @Column
    @Json(name = "weight_hint")
    var weightHint: String? = null

    @Column(indexed = true)
    @Json(name = "carbohydrate_per_100g")
    var carbohydratePer100g: Float = 0.toFloat()

    @Column
    @Json(name = "carbohydrate_per_weight")
    var carbohydratePerWeight: Float = 0.toFloat()

    @Column
    @Json(name = "calory")
    var calory: Float = 0.toFloat()

    @Column
    @Json(name = "protein")
    var protein: Float = 0.toFloat()

    @Column
    @Json(name = "fat")
    var fat: Float = 0.toFloat()

    @Column
    @Json(name = "sodium")
    var sodium: Float = 0.toFloat()

    @Column(indexed = true)
    @Json(name = "search_word")
    var searchWord: String? = null

    @Column
    @Json(name = "notes")
    var notes: String? = null

    @Column(indexed = true)
    @Json(name = "type_id")
    var typeId: Int = 0

    @Column(indexed = true)
    @Json(name = "kind_id")
    var kindId: Int = 0

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
