package com.sakurafish.pockettoushituryou.repository

import androidx.annotation.WorkerThread
import com.sakurafish.pockettoushituryou.data.db.entity.Kinds
import com.sakurafish.pockettoushituryou.data.db.entity.OrmaDatabase
import javax.inject.Inject

class KindsRepository @Inject internal constructor(private val orma: OrmaDatabase) {

    @WorkerThread
    fun findByType(typeId: Int): List<Kinds> {
        return orma.relationOfKinds().selector().typeIdEq(typeId).toList()
    }

    @WorkerThread
    fun findName(kindId: Int): String {
        val kinds = orma.relationOfKinds().selector().idEq(kindId).executeAsObservable().toList().blockingGet()
        if (kinds == null || kinds.size == 0) return ""
        val name = kinds[0].name
        return name ?: ""
    }
}