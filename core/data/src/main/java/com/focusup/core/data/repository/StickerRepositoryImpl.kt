package com.focusup.core.data.repository

import com.focusup.core.data.local.StickerDao
import com.focusup.core.data.local.toDomain
import com.focusup.core.data.local.toEntity
import com.focusup.core.domain.model.Sticker
import com.focusup.core.domain.repository.StickerRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class StickerRepositoryImpl @Inject constructor(
    private val stickerDao: StickerDao
) : StickerRepository {

    private val availableStickers = listOf(
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

    override fun getAllStickers(): Flow<List<Sticker>> {
        return stickerDao.getAllStickers().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun addSticker(sticker: Sticker) {
        stickerDao.insertSticker(sticker.toEntity())
    }

    override suspend fun getRandomAvailableSticker(): Sticker {
        val (emoji, name) = availableStickers.random()
        return Sticker(
            id = 0,
            name = name,
            emoji = emoji,
            earnedAt = System.currentTimeMillis()
        )
    }
}
