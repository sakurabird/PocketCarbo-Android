package com.sakurafish.pockettoushituryou.data.local

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TypesData(
    val types: List<Type>
)

data class Type(
    val id: Int,
    val name: String?
)
