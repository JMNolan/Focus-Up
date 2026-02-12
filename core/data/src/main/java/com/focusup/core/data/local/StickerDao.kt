package com.focusup.core.data.local
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
@Dao
interface StickerDao {
    @Query("SELECT * FROM stickers ORDER BY earnedAt DESC")
    fun getAllStickers(): Flow<List<StickerEntity>>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSticker(sticker: StickerEntity)
    @Query("SELECT COUNT(*) FROM stickers")
    suspend fun getStickerCount(): Int
}
