package com.focusup.core.data.repository

import app.cash.turbine.test
import com.focusup.core.data.local.StickerDao
import com.focusup.core.data.local.StickerEntity
import com.focusup.core.domain.model.Sticker
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class StickerRepositoryImplTest {

    private lateinit var stickerDao: StickerDao
    private lateinit var repository: StickerRepositoryImpl

    @Before
    fun setup() {
        stickerDao = mockk()
        repository = StickerRepositoryImpl(stickerDao)
    }

    @Test
    fun `getAllStickers returns mapped stickers from dao`() = runTest {
        // Given
        val stickerEntities = listOf(
            StickerEntity(
                id = 1,
                name = "Star",
                emoji = "⭐",
                earnedAt = 1000L
            ),
            StickerEntity(
                id = 2,
                name = "Trophy",
                emoji = "🏆",
                earnedAt = 2000L
            )
        )
        every { stickerDao.getAllStickers() } returns flowOf(stickerEntities)

        // When
        repository.getAllStickers().test {
            val stickers = awaitItem()

            // Then
            assertEquals(2, stickers.size)
            assertEquals("Star", stickers[0].name)
            assertEquals("⭐", stickers[0].emoji)
            assertEquals(1000L, stickers[0].earnedAt)
            assertEquals("Trophy", stickers[1].name)
            assertEquals("🏆", stickers[1].emoji)
            assertEquals(2000L, stickers[1].earnedAt)
            awaitComplete()
        }
    }

    @Test
    fun `getAllStickers returns empty list when dao returns empty`() = runTest {
        // Given
        every { stickerDao.getAllStickers() } returns flowOf(emptyList())

        // When
        repository.getAllStickers().test {
            val stickers = awaitItem()

            // Then
            assertTrue(stickers.isEmpty())
            awaitComplete()
        }
    }

    @Test
    fun `addSticker calls dao insertSticker with correct entity`() = runTest {
        // Given
        val sticker = Sticker(
            id = 0,
            name = "Diamond",
            emoji = "💎",
            earnedAt = 5000L
        )
        coEvery { stickerDao.insertSticker(any()) } returns Unit

        // When
        repository.addSticker(sticker)

        // Then
        coVerify {
            stickerDao.insertSticker(
                match {
                    it.name == "Diamond" &&
                    it.emoji == "💎" &&
                    it.earnedAt == 5000L
                }
            )
        }
    }

    @Test
    fun `getRandomAvailableSticker returns a valid sticker`() = runTest {
        // When
        val sticker = repository.getRandomAvailableSticker()

        // Then
        assertNotNull(sticker)
        assertNotNull(sticker.name)
        assertNotNull(sticker.emoji)
        assertTrue(sticker.emoji.isNotEmpty())
        assertTrue(sticker.earnedAt > 0)
        assertEquals(0, sticker.id) // Should always be 0 for new stickers
    }

    @Test
    fun `getRandomAvailableSticker returns different stickers on multiple calls`() = runTest {
        // When - Call multiple times
        val stickers = mutableSetOf<String>()
        repeat(20) {
            val sticker = repository.getRandomAvailableSticker()
            stickers.add(sticker.emoji)
        }

        // Then - Should have some variety (at least 2 different stickers in 20 calls)
        assertTrue(stickers.size >= 2)
    }

    @Test
    fun `getRandomAvailableSticker emoji matches name from available list`() = runTest {
        // When
        val sticker = repository.getRandomAvailableSticker()

        // Then - Verify the sticker is from the predefined list
        val validStickers = mapOf(
            "⭐" to "Star",
            "🎉" to "Party",
            "🏆" to "Trophy",
            "💎" to "Diamond",
            "🔥" to "Fire",
            "⚡" to "Lightning",
            "🌟" to "Sparkles",
            "👑" to "Crown",
            "🎯" to "Target",
            "🚀" to "Rocket",
            "💪" to "Strong",
            "🧠" to "Brain",
            "🎨" to "Art",
            "🌈" to "Rainbow",
            "🦄" to "Unicorn",
            "🐉" to "Dragon",
            "🎭" to "Theater",
            "🎪" to "Circus",
            "🎸" to "Guitar",
            "🎮" to "Gaming"
        )

        assertTrue(validStickers.containsKey(sticker.emoji))
        assertEquals(validStickers[sticker.emoji], sticker.name)
    }

    @Test
    fun `getRandomAvailableSticker sets timestamp to current time`() = runTest {
        // Given
        val beforeTime = System.currentTimeMillis()

        // When
        val sticker = repository.getRandomAvailableSticker()

        // Then
        val afterTime = System.currentTimeMillis()
        assertTrue(sticker.earnedAt >= beforeTime)
        assertTrue(sticker.earnedAt <= afterTime)
    }
}

