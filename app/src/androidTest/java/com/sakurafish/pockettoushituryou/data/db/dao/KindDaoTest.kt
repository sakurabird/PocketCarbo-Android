package com.sakurafish.pockettoushituryou.data.db.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.sakurafish.pockettoushituryou.data.db.AppDatabase
import com.sakurafish.pockettoushituryou.kindA
import com.sakurafish.pockettoushituryou.kindB
import com.sakurafish.pockettoushituryou.kindC
import org.hamcrest.Matchers
import org.junit.After
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4ClassRunner::class)
class KindDaoTest {
    private lateinit var kindDao: KindDao
    private lateinit var db: AppDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
                context, AppDatabase::class.java).build()
        kindDao = db.kindDao()
        kindDao.insertAll(listOf(kindB, kindC, kindA))
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun count() {
        val count = kindDao.count()
        assertThat(count, Matchers.equalTo(3))
    }

    @Test
    fun deleteAll() {
        kindDao.deleteAll()
        val count = kindDao.count()
        assertThat(count, Matchers.equalTo(0))
    }

    @Test
    fun findById() {
        val kind = kindDao.findById(2)
        assertThat(kind, Matchers.equalTo(kindB))
    }

    @Test
    fun findByTypeId() {
        val kinds = kindDao.findByTypeId(2)
        assertThat(kinds.size, Matchers.equalTo(2))
        assertThat(kinds[0], Matchers.equalTo(kindB))
        assertThat(kinds[1], Matchers.equalTo(kindC))
    }
}