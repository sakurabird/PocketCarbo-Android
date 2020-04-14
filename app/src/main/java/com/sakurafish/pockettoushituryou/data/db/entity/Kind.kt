package com.sakurafish.pockettoushituryou.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.io.Serializable

/**
 * Entity class for Room since version 3.5.1
 */
@Entity
@JsonClass(generateAdapter = true)
data class Kind(

        @PrimaryKey
        @ColumnInfo(name = "id")
        @Json(name = "id")
        var id: Int = 0,

        @ColumnInfo(name = "name")
        @Json(name = "name")
        var name: String? = null,

        @ColumnInfo(name = "search_word")
        @Json(name = "search_word")
        var searchWord: String? = null,

        @ColumnInfo(name = "type_id")
        @Json(name = "type_id")
        var typeId: Int = 0

) : Serializable

object KindCompanion {
    const val KIND_ALL = 0 // すべての種類
}