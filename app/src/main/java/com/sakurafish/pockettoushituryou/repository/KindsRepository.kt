package com.sakurafish.pockettoushituryou.repository

import androidx.annotation.WorkerThread
import com.sakurafish.pockettoushituryou.data.db.entity.Kinds
import com.sakurafish.pockettoushituryou.data.db.entity.OrmaDatabase
import javax.inject.Inject

class KindsRepository @Inject internal constructor(private val orma: OrmaDatabase) {

    @WorkerThread
    fun count(): Int {
        return orma.relationOfKinds().count()
    }

    @WorkerThread
    fun findByType(typeId: Int): List<Kinds> {
        return orma.relationOfKinds().selector().typeIdEq(typeId).toList()
    }
}