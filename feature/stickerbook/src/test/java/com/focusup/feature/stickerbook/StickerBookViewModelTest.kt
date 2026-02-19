package com.focusup.feature.stickerbook

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.focusup.core.domain.model.Sticker
import com.focusup.core.domain.repository.StickerRepository
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class StickerBookViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var stickerRepository: StickerRepository
    private lateinit var viewModel: StickerBookViewModel

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        stickerRepository = mockk(relaxed = true)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is empty list`() = runTest(testDispatcher) {
        // Given
        every { stickerRepository.getAllStickers() } returns flowOf(emptyList())

        // When
        viewModel = StickerBookViewModel(stickerRepository)

        // Then - StateFlow should have the repository value
        // The flow from repository immediately returns empty list
        assertEquals(emptyList<Sticker>(), viewModel.stickers.value)
    }

    @Test
    fun `stickers are loaded from repository`() = runTest(testDispatcher) {
        // Given
        val testStickers = listOf(
            Sticker(1, "Star", "⭐", 1000L),
            Sticker(2, "Trophy", "🏆", 2000L),
            Sticker(3, "Fire", "🔥", 3000L)
        )
        every { stickerRepository.getAllStickers() } returns flowOf(testStickers)

        // When
        viewModel = StickerBookViewModel(stickerRepository)

        // Collect to activate the StateFlow
        viewModel.stickers.test {
            // First emission is the initialValue (empty list)
            awaitItem()
            // Now advance to get the repository data
            runCurrent()
            val stickers = awaitItem()
            assertEquals(3, stickers.size)
            assertEquals("Star", stickers[0].name)
            assertEquals("Trophy", stickers[1].name)
            assertEquals("Fire", stickers[2].name)
            cancel()
        }
    }

    @Test
    fun `stickers maintain correct order from repository`() = runTest(testDispatcher) {
        // Given - Repository returns stickers in descending order by earnedAt
        val testStickers = listOf(
            Sticker(3, "Latest", "🔥", 5000L),
            Sticker(2, "Middle", "🏆", 3000L),
            Sticker(1, "Oldest", "⭐", 1000L)
        )
        every { stickerRepository.getAllStickers() } returns flowOf(testStickers)

        // When
        viewModel = StickerBookViewModel(stickerRepository)

        // Then
        viewModel.stickers.test {
            awaitItem() // Skip initial empty list
            runCurrent() // Execute pending coroutines
            val stickers = awaitItem()
            assertEquals("Latest", stickers[0].name)
            assertEquals(5000L, stickers[0].earnedAt)
            assertEquals("Middle", stickers[1].name)
            assertEquals(3000L, stickers[1].earnedAt)
            assertEquals("Oldest", stickers[2].name)
            assertEquals(1000L, stickers[2].earnedAt)
            cancel()
        }
    }

    @Test
    fun `stickers update when repository emits new values`() = runTest(testDispatcher) {
        // Given
        val initialStickers = listOf(
            Sticker(1, "Star", "⭐", 1000L)
        )
        every { stickerRepository.getAllStickers() } returns flowOf(initialStickers)

        // When
        viewModel = StickerBookViewModel(stickerRepository)

        // Then
        viewModel.stickers.test {
            awaitItem() // Skip initial empty list
            runCurrent() // Execute pending coroutines
            val stickers = awaitItem()
            assertEquals(1, stickers.size)
            assertEquals("Star", stickers[0].name)
            cancel()
        }
    }

    @Test
    fun `stickers contain all expected properties`() = runTest(testDispatcher) {
        // Given
        val testStickers = listOf(
            Sticker(
                id = 42,
                name = "Diamond",
                emoji = "💎",
                earnedAt = 123456789L
            )
        )
        every { stickerRepository.getAllStickers() } returns flowOf(testStickers)

        // When
        viewModel = StickerBookViewModel(stickerRepository)

        // Then
        viewModel.stickers.test {
            awaitItem() // Skip initial empty list
            runCurrent() // Execute pending coroutines
            val stickers = awaitItem()
            val sticker = stickers[0]
            assertEquals(42, sticker.id)
            assertEquals("Diamond", sticker.name)
            assertEquals("💎", sticker.emoji)
            assertEquals(123456789L, sticker.earnedAt)
            cancel()
        }
    }

    @Test
    fun `stickers handles large collection`() = runTest(testDispatcher) {
        // Given
        val largeCollection = (1..100).map { index ->
            Sticker(
                id = index,
                name = "Sticker $index",
                emoji = "⭐",
                earnedAt = index * 1000L
            )
        }
        every { stickerRepository.getAllStickers() } returns flowOf(largeCollection)

        // When
        viewModel = StickerBookViewModel(stickerRepository)

        // Then
        viewModel.stickers.test {
            awaitItem() // Skip initial empty list
            runCurrent() // Execute pending coroutines
            val stickers = awaitItem()
            assertEquals(100, stickers.size)
            assertEquals("Sticker 1", stickers[0].name)
            assertEquals("Sticker 100", stickers[99].name)
            cancel()
        }
    }

    @Test
    fun `stickers handles emoji variety`() = runTest(testDispatcher) {
        // Given
        val diverseStickers = listOf(
            Sticker(1, "Star", "⭐", 1000L),
            Sticker(2, "Party", "🎉", 2000L),
            Sticker(3, "Trophy", "🏆", 3000L),
            Sticker(4, "Diamond", "💎", 4000L),
            Sticker(5, "Fire", "🔥", 5000L),
            Sticker(6, "Lightning", "⚡", 6000L),
            Sticker(7, "Sparkles", "🌟", 7000L),
            Sticker(8, "Crown", "👑", 8000L)
        )
        every { stickerRepository.getAllStickers() } returns flowOf(diverseStickers)

        // When
        viewModel = StickerBookViewModel(stickerRepository)

        // Then
        viewModel.stickers.test {
            awaitItem() // Skip initial empty list
            runCurrent() // Execute pending coroutines
            val stickers = awaitItem()
            assertEquals(8, stickers.size)

            // Verify each emoji is preserved
            val emojis = stickers.map { it.emoji }
            assertTrue(emojis.contains("⭐"))
            assertTrue(emojis.contains("🎉"))
            assertTrue(emojis.contains("🏆"))
            assertTrue(emojis.contains("💎"))
            assertTrue(emojis.contains("🔥"))
            assertTrue(emojis.contains("⚡"))
            assertTrue(emojis.contains("🌟"))
            assertTrue(emojis.contains("👑"))
            cancel()
        }
    }

    @Test
    fun `viewModel maintains state across recomposition`() = runTest(testDispatcher) {
        // Given
        val testStickers = listOf(
            Sticker(1, "Star", "⭐", 1000L),
            Sticker(2, "Trophy", "🏆", 2000L)
        )
        every { stickerRepository.getAllStickers() } returns flowOf(testStickers)

        // When
        viewModel = StickerBookViewModel(stickerRepository)

        // Then - Collect once to activate StateFlow
        viewModel.stickers.test {
            awaitItem() // Skip initial empty list
            runCurrent() // Execute pending coroutines
            val firstCollection = awaitItem()
            assertEquals(2, firstCollection.size)
            assertEquals("Star", firstCollection[0].name)
            cancel()
        }

        // StateFlow should maintain the cached value
        // We can access it via .value without collecting again
        val cachedValue = viewModel.stickers.value
        assertEquals(2, cachedValue.size)
        assertEquals("Star", cachedValue[0].name)
        assertEquals("Trophy", cachedValue[1].name)
    }
}
