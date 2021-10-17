package com.sakurafish.pockettoushituryou.data.db.entity.orma

import com.github.gfx.android.orma.annotation.Column
import com.github.gfx.android.orma.annotation.PrimaryKey
import com.github.gfx.android.orma.annotation.Table
import java.util.*

/**
 * This class has been deprecated from version 2.5.1 due to migration with the Room library
 */
@Deprecated("This class is not used from version 2.5.1. Use 'Favorite' class")
@Table
class FavoriteFoods {

    @PrimaryKey
    @Column(value = "id", indexed = true)
    var id: Int = 0

    @Column(value = "foods", indexed = true, unique = true)
    var foods: Foods = Foods()

    @Column(value = "createdAt", indexed = true)
    var createdAt: Date? = null

    constructor()

    constructor(foods: Foods, createdAt: Date) {
        this.foods = foods
        this.createdAt = createdAt
    }

    override fun toString(): String {
        return "foods_id:" + this.foods.id + " name:" + this.foods.name + " created date:" + this.createdAt!!.toString()
    }
}
