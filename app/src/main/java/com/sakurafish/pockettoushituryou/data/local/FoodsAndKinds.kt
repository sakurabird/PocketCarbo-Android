package com.sakurafish.pockettoushituryou.data.local

import com.sakurafish.pockettoushituryou.data.db.entity.Food
import com.sakurafish.pockettoushituryou.data.db.entity.Kind
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Entity class for Local data ('assets/json/foods_and_kinds.json') since version 2.5.1
 */
@JsonClass(generateAdapter = true)
data class FoodsAndKinds(
    @Json(name = "data_version")
    var dataVersion: Int = 0,
    var kinds: List<Kind>? = null,
    var foods: List<Food>? = null
)
