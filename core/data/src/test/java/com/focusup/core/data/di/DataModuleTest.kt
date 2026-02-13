package com.focusup.core.data.di

import android.content.Context
import androidx.room.Room
import com.focusup.core.data.local.FocusUpDatabase
import com.focusup.core.data.local.StickerDao
import com.focusup.core.data.repository.StickerRepositoryImpl
import com.focusup.core.domain.repository.StickerRepository
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class DataModuleTest {

    private lateinit var mockContext: Context
    private lateinit var dataModule: DataModule

    @Before
    fun setup() {
        mockContext = mockk(relaxed = true)
        dataModule = DataModule

        // Mock the context's applicationContext to return itself
        every { mockContext.applicationContext } returns mockContext
    }

    @Test
    fun `provideFocusUpDatabase returns valid database instance`() {
        // When
        val database = dataModule.provideFocusUpDatabase(mockContext)

        // Then
        assertNotNull(database)
        assertTrue(database is FocusUpDatabase)
    }

    @Test
    fun `provideStickerDao returns valid dao from database`() {
        // Given
        val database = Room.inMemoryDatabaseBuilder(
            mockContext,
            FocusUpDatabase::class.java
        ).allowMainThreadQueries().build()

        // When
        val dao = dataModule.provideStickerDao(database)

        // Then
        assertNotNull(dao)
        assertTrue(dao is StickerDao)
    }

    @Test
    fun `provideStickerRepository returns StickerRepositoryImpl instance`() {
        // Given
        val mockDao: StickerDao = mockk()

        // When
        val repository = dataModule.provideStickerRepository(mockDao)

        // Then
        assertNotNull(repository)
        assertTrue(repository is StickerRepository)
        assertTrue(repository is StickerRepositoryImpl)
    }

    @Test
    fun `provideStickerDao returns same dao instance from same database`() {
        // Given
        val database = Room.inMemoryDatabaseBuilder(
            mockContext,
            FocusUpDatabase::class.java
        ).allowMainThreadQueries().build()

        // When
        val dao1 = dataModule.provideStickerDao(database)
        val dao2 = dataModule.provideStickerDao(database)

        // Then - Room should return the same DAO instance
        assertEquals(dao1, dao2)
    }

    @Test
    fun `integration test - full dependency chain works`() {
        // Given
        val database = Room.inMemoryDatabaseBuilder(
            mockContext,
            FocusUpDatabase::class.java
        ).allowMainThreadQueries().build()

        // When
        val dao = dataModule.provideStickerDao(database)
        val repository = dataModule.provideStickerRepository(dao)

        // Then
        assertNotNull(database)
        assertNotNull(dao)
        assertNotNull(repository)
        assertTrue(repository is StickerRepositoryImpl)
    }
}

