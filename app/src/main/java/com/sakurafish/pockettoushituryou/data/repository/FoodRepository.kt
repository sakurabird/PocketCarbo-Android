package com.sakurafish.pockettoushituryou.data.repository

import com.sakurafish.pockettoushituryou.data.db.entity.Food
import com.sakurafish.pockettoushituryou.data.db.entity.FoodSortOrder
import com.sakurafish.pockettoushituryou.data.local.FoodsAndKinds

interface FoodRepository {

    fun count(): Int

    fun insertAll(foods: List<Food>?)

    fun deleteAll()

    fun findByType(
        typeId: Int,
        foodSortOrder: FoodSortOrder
    ): List<Food>

    fun findByTypeAndKind(
        typeId: Int,
        kindId: Int,
        foodSortOrder: FoodSortOrder
    ): List<Food>

    fun search(searchQuery: String?): List<Food>

    fun parseJsonToFoodsData(): FoodsAndKinds?
}