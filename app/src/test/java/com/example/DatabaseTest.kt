package com.example

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.AppDatabase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.junit.Assert.assertNotNull

@RunWith(RobolectricTestRunner::class)
class DatabaseTest {
    @Test
    fun testDbCreation() = runBlocking {
        val db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()
        assertNotNull(db.poruthamDao())
        
        val items = db.poruthamDao().getAllReports().first()
        assertNotNull(items)
    }
}
