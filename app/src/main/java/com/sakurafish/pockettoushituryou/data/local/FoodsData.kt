package com.sakurafish.pockettoushituryou.data.local

import com.sakurafish.pockettoushituryou.data.db.entity.orma.Foods
import com.sakurafish.pockettoushituryou.data.db.entity.orma.Kinds
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Local json data access class with
 * This class has been deprecated from version 3.5.1 due to migration with the Room library
 */
@Deprecated("This class is not used from version 3.5.1. Use 'FoodsAndKinds' class")
@JsonClass(generateAdapter = true)
data class FoodsData(
        @Json(name = "data_version")
        var dataVersion: Int = 0,
        var kinds: List<Kinds>? = null,
        var foods: List<Foods>? = null
)
