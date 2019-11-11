package com.sakurafish.pockettoushituryou.data.db.entity

import com.github.gfx.android.orma.annotation.Column
import com.github.gfx.android.orma.annotation.PrimaryKey
import com.github.gfx.android.orma.annotation.Table
import java.util.*

@Table
class FavoriteFoods {

    @PrimaryKey
    @Column(indexed = true)
    var id: Int = 0

    @Column(indexed = true, unique = true)
    var foods: Foods = Foods()

    @Column(indexed = true)
    var createdAt: Date? = null

    constructor() {}

    constructor(foods: Foods, createdAt: Date) {
        this.foods = foods
        this.createdAt = createdAt
    }

    override fun toString(): String {
        return "foods_id:" + this.foods.id + " name:" + this.foods.name + " created date:" + this.createdAt!!.toString()
    }
}
