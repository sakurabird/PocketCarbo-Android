package com.sakurafish.pockettoushituryou.data.db.entity.orma

import com.github.gfx.android.orma.annotation.Column
import com.github.gfx.android.orma.annotation.PrimaryKey
import com.github.gfx.android.orma.annotation.Table
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * This class has been deprecated from version 2.5.1 due to migration with the Room library
 */
@Deprecated("This class is not used from version 2.5.1. Use 'Kind' class")
@JsonClass(generateAdapter = true)
@Table
class Kinds {

    @PrimaryKey(auto = false)
    @Column(value = "id", indexed = true)
    @Json(name = "id")
    var id: Int = 0

    @Column(value = "name", indexed = true)
    @Json(name = "name")
    var name: String? = null

    @Column(value = "search_word", indexed = true)
    @Json(name = "search_word")
    var searchWord: String? = null

    @Column(value = "type_id", indexed = true)
    @Json(name = "type_id")
    var typeId: Int = 0

    override fun toString(): String {
        return "Kinds id:$id name:$name searchWord:$searchWord typeId:$typeId"
    }

    companion object {
        const val ALL = 0
    }
}
