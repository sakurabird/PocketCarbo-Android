package com.sakurafish.pockettoushituryou.repository.orma

import androidx.annotation.WorkerThread
import com.sakurafish.pockettoushituryou.data.db.entity.orma.Kinds
import com.sakurafish.pockettoushituryou.data.db.entity.orma.OrmaDatabase
import javax.inject.Inject

/**
 * Kinds data access class with Orma
 * This class has been deprecated from version 2.5.1 due to migration with the Room library
 */
@Deprecated("This class is not used from version 2.5.1. Use 'KindRepository' and 'KindDao' class")
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