package com.focusup.core.data.local

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FocusUpDatabaseTest {

    private lateinit var database: FocusUpDatabase
    private lateinit var stickerDao: StickerDao

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(
            context,
            FocusUpDatabase::class.java
        ).allowMainThreadQueries().build()

        stickerDao = database.stickerDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun databaseCreatesSuccessfully() {
        assertNotNull(database)
        assertNotNull(stickerDao)
    }

    @Test
    fun databaseReturnsCorrectDaoInstance() {
        // When
        val dao1 = database.stickerDao()
        val dao2 = database.stickerDao()

        // Then
        assertEquals(dao1, dao2)
    }

    @Test
    fun insertAndRetrieveSticker() = runBlocking {
        // Given
        val sticker = StickerEntity(
            id = 0,
            name = "Test Sticker",
            emoji = "🎯",
            earnedAt = System.currentTimeMillis()
        )

        // When
        stickerDao.insertSticker(sticker)

        // Then
        stickerDao.getAllStickers().test {
            val stickers = awaitItem()
            assertEquals(1, stickers.size)
            assertEquals("Test Sticker", stickers[0].name)
            assertEquals("🎯", stickers[0].emoji)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun insertMultipleStickers() = runBlocking {
        // Given
        val stickers = listOf(
            StickerEntity(0, "Star", "⭐", 1000L),
            StickerEntity(0, "Trophy", "🏆", 2000L),
            StickerEntity(0, "Fire", "🔥", 3000L)
        )

        // When
        stickers.forEach { stickerDao.insertSticker(it) }

        // Then
        stickerDao.getAllStickers().test {
            val result = awaitItem()
            assertEquals(3, result.size)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun stickersAreOrderedByEarnedAtDesc() = runBlocking {
        // Given - Insert in non-chronological order
        stickerDao.insertSticker(StickerEntity(0, "Middle", "🎯", 2000L))
        stickerDao.insertSticker(StickerEntity(0, "First", "⭐", 3000L))
        stickerDao.insertSticker(StickerEntity(0, "Last", "🏆", 1000L))

        // Then
        stickerDao.getAllStickers().test {
            val stickers = awaitItem()
            assertEquals("First", stickers[0].name)
            assertEquals("Middle", stickers[1].name)
            assertEquals("Last", stickers[2].name)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun getStickerCountReturnsCorrectValue() = runBlocking {
        // Given
        assertEquals(0, stickerDao.getStickerCount())

        // When
        stickerDao.insertSticker(StickerEntity(0, "Star", "⭐", 1000L))
        stickerDao.insertSticker(StickerEntity(0, "Trophy", "🏆", 2000L))

        // Then
        assertEquals(2, stickerDao.getStickerCount())
    }

    @Test
    fun replaceStickerOnConflict() = runBlocking {
        // Given
        stickerDao.insertSticker(StickerEntity(1, "Original", "⭐", 1000L))

        // When - Insert with same ID
        stickerDao.insertSticker(StickerEntity(1, "Updated", "🏆", 2000L))

        // Then
        stickerDao.getAllStickers().test {
            val stickers = awaitItem()
            assertEquals(1, stickers.size)
            assertEquals("Updated", stickers[0].name)
            assertEquals("🏆", stickers[0].emoji)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun flowEmitsUpdatesOnInsert() = runBlocking {
        // When/Then
        stickerDao.getAllStickers().test {
            // Initial empty state
            assertEquals(0, awaitItem().size)

            // Insert sticker
            stickerDao.insertSticker(StickerEntity(0, "Star", "⭐", 1000L))

            // Should emit updated list
            assertEquals(1, awaitItem().size)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun emptyDatabaseReturnsEmptyList() = runBlocking {
        stickerDao.getAllStickers().test {
            val stickers = awaitItem()
            assertTrue(stickers.isEmpty())
            cancelAndIgnoreRemainingEvents()
        }
    }
}

