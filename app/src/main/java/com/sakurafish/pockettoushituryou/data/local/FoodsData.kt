package com.sakurafish.pockettoushituryou.data.local

import com.sakurafish.pockettoushituryou.data.db.entity.Foods
import com.sakurafish.pockettoushituryou.data.db.entity.Kinds
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class FoodsData(
        @Json(name = "data_version")
        var dataVersion: Int = 0,
        var kinds: List<Kinds>? = null,
        var foods: List<Foods>? = null
)
