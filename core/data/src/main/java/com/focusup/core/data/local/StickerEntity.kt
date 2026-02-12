package com.focusup.core.data.local
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.focusup.core.domain.model.Sticker
@Entity(tableName = "stickers")
data class StickerEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val emoji: String,
    val earnedAt: Long
)
fun StickerEntity.toDomain(): Sticker {
    return Sticker(
        id = id,
        name = name,
        emoji = emoji,
        earnedAt = earnedAt
    )
}
fun Sticker.toEntity(): StickerEntity {
    return StickerEntity(
        id = id,
        name = name,
        emoji = emoji,
        earnedAt = earnedAt
    )
}
