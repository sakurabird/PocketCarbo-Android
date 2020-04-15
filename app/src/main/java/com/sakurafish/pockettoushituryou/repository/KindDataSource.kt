package com.sakurafish.pockettoushituryou.repository

import androidx.annotation.WorkerThread
import com.sakurafish.pockettoushituryou.data.db.dao.KindDao
import com.sakurafish.pockettoushituryou.data.db.entity.Kind
import javax.inject.Inject

@WorkerThread
class KindDataSource @Inject
constructor(private val kindDao: KindDao) : KindRepository {
    override fun count(): Int {
        return kindDao.count()
    }

    override fun deleteAll() {
        kindDao.deleteAll()
    }

    override fun insertAll(kinds: List<Kind>?) {
        kinds ?: return
        kindDao.insertAll(kinds)
    }

    override fun findById(id: Int): Kind? {
        return kindDao.findById(id)
    }

    override fun findByTypeId(typeId: Int): List<Kind> {
        return kindDao.findByTypeId(typeId)
    }
}