package com.focusup.core.domain.repository

import com.focusup.core.domain.model.Sticker
import kotlinx.coroutines.flow.Flow

interface StickerRepository {
    fun getAllStickers(): Flow<List<Sticker>>
    suspend fun addSticker(sticker: Sticker)
    suspend fun getRandomAvailableSticker(): Sticker
}

