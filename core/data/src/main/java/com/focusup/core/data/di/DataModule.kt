package com.focusup.core.data.di

import android.content.Context
import androidx.room.Room
import com.focusup.core.data.local.FocusUpDatabase
import com.focusup.core.data.local.StickerDao
import com.focusup.core.data.repository.StickerRepositoryImpl
import com.focusup.core.domain.repository.StickerRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    @Singleton
    fun provideFocusUpDatabase(
        @ApplicationContext context: Context
    ): FocusUpDatabase {
        return Room.databaseBuilder(
            context,
            FocusUpDatabase::class.java,
            "focusup_database"
        ).build()
    }

    @Provides
    @Singleton
    fun provideStickerDao(database: FocusUpDatabase): StickerDao {
        return database.stickerDao()
    }

    @Provides
    @Singleton
    fun provideStickerRepository(
        stickerDao: StickerDao
    ): StickerRepository {
        return StickerRepositoryImpl(stickerDao)
    }
}
