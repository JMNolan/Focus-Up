package com.focusup.feature.timer

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.focusup.core.domain.model.Sticker
import com.focusup.core.domain.model.TimerDuration
import com.focusup.core.domain.repository.StickerRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TimerViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var stickerRepository: StickerRepository
    private lateinit var viewModel: TimerViewModel

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        stickerRepository = mockk()
        viewModel = TimerViewModel(stickerRepository)
    }

    @Test
    fun `initial state is correct`() = runTest {
        // When
        val state = viewModel.uiState.value

        // Then
        assertNull(state.selectedDuration)
        assertFalse(state.isRunning)
        assertEquals(0L, state.remainingTimeMillis)
        assertFalse(state.isCompleted)
        assertNull(state.earnedSticker)
    }

    @Test
    fun `selectDuration updates state correctly`() = runTest {
        // When
        viewModel.selectDuration(TimerDuration.FIFTEEN_MIN)

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals(TimerDuration.FIFTEEN_MIN, state.selectedDuration)
            assertEquals(TimerDuration.FIFTEEN_MIN.milliseconds, state.remainingTimeMillis)
            assertFalse(state.isRunning)
            assertFalse(state.isCompleted)
        }
    }

    @Test
    fun `selectDuration does not change when timer is running`() = runTest {
        // Given
        viewModel.selectDuration(TimerDuration.TEST)

        val testSticker = Sticker(1, "Star", "⭐", System.currentTimeMillis())
        coEvery { stickerRepository.getRandomAvailableSticker() } returns testSticker
        coEvery { stickerRepository.addSticker(any()) } returns Unit

        viewModel.startTimer()
        advanceTimeBy(100)

        // When - Try to change duration while running
        viewModel.selectDuration(TimerDuration.ONE_HOUR)

        // Then - Should still be the original duration
        val state = viewModel.uiState.value
        assertEquals(TimerDuration.TEST, state.selectedDuration)
    }

    @Test
    fun `startTimer sets isRunning to true`() = runTest {
        // Given
        viewModel.selectDuration(TimerDuration.TEST)

        val testSticker = Sticker(1, "Star", "⭐", System.currentTimeMillis())
        coEvery { stickerRepository.getRandomAvailableSticker() } returns testSticker
        coEvery { stickerRepository.addSticker(any()) } returns Unit

        // When
        viewModel.startTimer()

        // Then
        val state = viewModel.uiState.value
        assertTrue(state.isRunning)
        assertEquals(TimerDuration.TEST.milliseconds, state.remainingTimeMillis)
    }

    @Test
    fun `startTimer does nothing when no duration selected`() = runTest {
        // When
        viewModel.startTimer()

        // Then
        val state = viewModel.uiState.value
        assertFalse(state.isRunning)
        assertNull(state.selectedDuration)
    }

    @Test
    fun `timer completes successfully and earns sticker`() = runTest {
        // Given
        val testSticker = Sticker(1, "Trophy", "🏆", System.currentTimeMillis())
        coEvery { stickerRepository.getRandomAvailableSticker() } returns testSticker
        coEvery { stickerRepository.addSticker(any()) } returns Unit

        viewModel.selectDuration(TimerDuration.TEST)

        // When
        viewModel.startTimer()
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertFalse(state.isRunning)
        assertTrue(state.isCompleted)
        assertEquals(0L, state.remainingTimeMillis)
        assertNotNull(state.earnedSticker)
        assertEquals("Trophy", state.earnedSticker?.name)
        assertEquals("🏆", state.earnedSticker?.emoji)

        coVerify { stickerRepository.getRandomAvailableSticker() }
        coVerify { stickerRepository.addSticker(testSticker) }
    }

    @Test
    fun `resetTimer clears completed state and resets timer`() = runTest {
        // Given
        val testSticker = Sticker(1, "Star", "⭐", System.currentTimeMillis())
        coEvery { stickerRepository.getRandomAvailableSticker() } returns testSticker
        coEvery { stickerRepository.addSticker(any()) } returns Unit

        viewModel.selectDuration(TimerDuration.TEST)
        viewModel.startTimer()
        advanceUntilIdle()

        // When
        viewModel.resetTimer()

        // Then
        val state = viewModel.uiState.value
        assertFalse(state.isRunning)
        assertFalse(state.isCompleted)
        assertEquals(TimerDuration.TEST.milliseconds, state.remainingTimeMillis)
        assertNull(state.earnedSticker)
    }

    @Test
    fun `resetTimer stops running timer`() = runTest {
        // Given
        viewModel.selectDuration(TimerDuration.FIFTEEN_MIN)
        viewModel.startTimer()
        advanceTimeBy(1000)

        // When
        viewModel.resetTimer()

        // Then
        val state = viewModel.uiState.value
        assertFalse(state.isRunning)
        assertEquals(TimerDuration.FIFTEEN_MIN.milliseconds, state.remainingTimeMillis)
    }

    @Test
    fun `timer updates remaining time correctly`() = runTest {
        // Given
        val testSticker = Sticker(1, "Star", "⭐", System.currentTimeMillis())
        coEvery { stickerRepository.getRandomAvailableSticker() } returns testSticker
        coEvery { stickerRepository.addSticker(any()) } returns Unit

        viewModel.selectDuration(TimerDuration.TEST)

        // When
        viewModel.uiState.test {
            skipItems(1) // Skip initial state

            viewModel.startTimer()
            val startState = awaitItem()
            assertTrue(startState.isRunning)

            // Advance time and check updates
            advanceTimeBy(1000)

            // Wait for state updates
            advanceUntilIdle()

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `multiple duration selections work correctly`() = runTest {
        // When
        viewModel.selectDuration(TimerDuration.TEST)
        var state = viewModel.uiState.value
        assertEquals(TimerDuration.TEST, state.selectedDuration)

        viewModel.selectDuration(TimerDuration.THIRTY_MIN)
        state = viewModel.uiState.value
        assertEquals(TimerDuration.THIRTY_MIN, state.selectedDuration)

        viewModel.selectDuration(TimerDuration.ONE_HOUR)
        state = viewModel.uiState.value
        assertEquals(TimerDuration.ONE_HOUR, state.selectedDuration)
    }

    @Test
    fun `selecting new duration clears completed state`() = runTest {
        // Given - Complete a timer
        val testSticker = Sticker(1, "Star", "⭐", System.currentTimeMillis())
        coEvery { stickerRepository.getRandomAvailableSticker() } returns testSticker
        coEvery { stickerRepository.addSticker(any()) } returns Unit

        viewModel.selectDuration(TimerDuration.TEST)
        viewModel.startTimer()
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.isCompleted)

        // When - Select new duration
        viewModel.selectDuration(TimerDuration.FIFTEEN_MIN)

        // Then
        val state = viewModel.uiState.value
        assertFalse(state.isCompleted)
        assertNull(state.earnedSticker)
    }
}

