package com.focusup.core.data.local
import androidx.room.Database
import androidx.room.RoomDatabase
@Database(
    entities = [StickerEntity::class],
    version = 1,
    exportSchema = false
)
abstract class FocusUpDatabase : RoomDatabase() {
    abstract fun stickerDao(): StickerDao
}
