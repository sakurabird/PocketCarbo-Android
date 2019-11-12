package com.sakurafish.pockettoushituryou.data.db.entity

import com.github.gfx.android.orma.annotation.Column
import com.github.gfx.android.orma.annotation.PrimaryKey
import com.github.gfx.android.orma.annotation.Table
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
@Table
class Kinds {

    @PrimaryKey(auto = false)
    @Column(value = "id", indexed = true)
    @Json(name = "id")
    var id: Int = 0

    @Column(value = "name")
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
