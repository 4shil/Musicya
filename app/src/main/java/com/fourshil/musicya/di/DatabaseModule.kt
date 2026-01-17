package com.fourshil.musicya.di

import android.content.Context
import androidx.room.Room
import com.fourshil.musicya.data.db.AppDatabase
import com.fourshil.musicya.data.db.MusicDao
import com.fourshil.musicya.data.repository.IMusicRepository
import com.fourshil.musicya.data.repository.MusicRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "lyra_database"
        )
        .fallbackToDestructiveMigration() // For development - handles schema changes
        .build()
    }
    
    @Provides
    @Singleton
    fun provideMusicDao(database: AppDatabase): MusicDao {
        return database.musicDao()
    }
}

/**
 * Hilt module to bind IMusicRepository interface to MusicRepository implementation.
 * This enables easy swapping for testing with fake implementations.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    
    @Binds
    @Singleton
    abstract fun bindMusicRepository(impl: MusicRepository): IMusicRepository
}
