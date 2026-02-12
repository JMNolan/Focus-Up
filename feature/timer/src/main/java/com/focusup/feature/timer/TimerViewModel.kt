package com.focusup.feature.timer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.focusup.core.domain.model.Sticker
import com.focusup.core.domain.model.TimerDuration
import com.focusup.core.domain.repository.StickerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TimerUiState(
    val selectedDuration: TimerDuration? = null,
    val isRunning: Boolean = false,
    val remainingTimeMillis: Long = 0L,
    val isCompleted: Boolean = false,
    val earnedSticker: Sticker? = null
)

@HiltViewModel
class TimerViewModel @Inject constructor(
    private val stickerRepository: StickerRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TimerUiState())
    val uiState: StateFlow<TimerUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null

    fun selectDuration(duration: TimerDuration) {
        if (!_uiState.value.isRunning) {
            _uiState.update {
                it.copy(
                    selectedDuration = duration,
                    remainingTimeMillis = duration.milliseconds,
                    isCompleted = false,
                    earnedSticker = null
                )
            }
        }
    }

    fun startTimer() {
        val duration = _uiState.value.selectedDuration ?: return

        _uiState.update {
            it.copy(
                isRunning = true,
                remainingTimeMillis = duration.milliseconds,
                isCompleted = false
            )
        }

        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            val endTime = System.currentTimeMillis() + duration.milliseconds

            while (_uiState.value.remainingTimeMillis > 0) {
                delay(100)
                val remaining = endTime - System.currentTimeMillis()

                if (remaining <= 0) {
                    onTimerComplete()
                    break
                }

                _uiState.update { it.copy(remainingTimeMillis = remaining) }
            }
        }
    }

    private suspend fun onTimerComplete() {
        val sticker = stickerRepository.getRandomAvailableSticker()
        stickerRepository.addSticker(sticker)

        _uiState.update {
            it.copy(
                isRunning = false,
                remainingTimeMillis = 0,
                isCompleted = true,
                earnedSticker = sticker
            )
        }
    }

    fun resetTimer() {
        timerJob?.cancel()
        _uiState.update {
            it.copy(
                isRunning = false,
                remainingTimeMillis = it.selectedDuration?.milliseconds ?: 0,
                isCompleted = false,
                earnedSticker = null
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}

