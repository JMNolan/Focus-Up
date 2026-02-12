package com.focusup.feature.stickerbook

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.focusup.core.domain.model.Sticker
import com.focusup.core.domain.repository.StickerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class StickerBookViewModel @Inject constructor(
    stickerRepository: StickerRepository
) : ViewModel() {

    val stickers: StateFlow<List<Sticker>> = stickerRepository.getAllStickers()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
}

