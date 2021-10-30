package com.sakurafish.pockettoushituryou.data.repository

import com.sakurafish.pockettoushituryou.data.db.entity.Kind

interface KindRepository {

    fun count(): Int

    fun insertAll(kinds: List<Kind>?)

    fun deleteAll()

    fun findById(id: Int): Kind?

    fun findByTypeId(typeId: Int): List<Kind>
}