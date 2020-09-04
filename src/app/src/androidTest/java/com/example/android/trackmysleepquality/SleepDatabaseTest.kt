/*
 * Copyright 2018, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.trackmysleepquality

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.android.trackmysleepquality.database.SleepDatabase
import com.example.android.trackmysleepquality.database.SleepDatabaseDao
import com.example.android.trackmysleepquality.database.SleepNight
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException


/**
 * This is not meant to be a full set of tests. For simplicity, most of your samples do not
 * include tests. However, when building the Room, it is helpful to make sure it works before
 * adding the UI.
 */

@RunWith(AndroidJUnit4::class)
class SleepDatabaseTest {

    private lateinit var sleepDao: SleepDatabaseDao
    private lateinit var db: SleepDatabase

    @get:Rule val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun createDb() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        // Using an in-memory database because the information stored here disappears when the
        // process is killed.
        db = Room.inMemoryDatabaseBuilder(context, SleepDatabase::class.java)
                // Allowing main thread queries, just for testing.
                .allowMainThreadQueries()
                .build()
        sleepDao = db.sleepDatabaseDao
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun insertAndGetNight() {
        //Arrange
        val night = SleepNight()

        //Act
        sleepDao.insert(night)
        val tonight = sleepDao.getTonight()

        //Assert
        assertEquals(-1, tonight?.sleepQuality)
    }

    @Test
    @Throws(Exception::class)
    fun insertAndGetNightById() {
        //Arrange
        val night = SleepNight()

        //Act
        val id = sleepDao.insert(night)
        val tonight = sleepDao.get(id)

        //Assert
        assertEquals(-1, tonight?.sleepQuality)
    }

    @Test
    @Throws(Exception::class)
    fun getAllNights() {
        //Arrange
        val night = SleepNight()
        val nightTwo = SleepNight()

        var nights: List<SleepNight> = emptyList()
        sleepDao.getAllNights().observeForever {
            nights = it
        }

        //Act
        sleepDao.insert(night)
        sleepDao.insert(nightTwo)

        //Assert
        assertEquals(2, nights.size)
    }


    @Test
    @Throws(Exception::class)
    fun clear() {
        //Arrange
        sleepDao.insert(SleepNight())

        //Act
        sleepDao.clear()
        var night = sleepDao.getTonight()

        //Assert
        assertNull(night)
    }

    @Test
    @Throws(Exception::class)
    fun update() {
        //Arrange
        sleepDao.insert(SleepNight())

        //Act
        var night = sleepDao.getTonight()
        night!!.sleepQuality = 5
        sleepDao.update(night!!)

        //Assert
        var updatedNight = sleepDao.getTonight()
        assertEquals(5, updatedNight!!.sleepQuality)
    }
}

