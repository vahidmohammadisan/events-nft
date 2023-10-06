package ir.vahidmohammadisan.lst.presentation.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ir.vahidmohammadisan.lst.data.local.EventDatabase
import ir.vahidmohammadisan.lst.data.local.dao.EventDao
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext appContext: Context): EventDatabase {
        return Room.databaseBuilder(
            appContext,
            EventDatabase::class.java,
            "evn_database"
        ).build()
    }

    @Provides
    fun provideEventDao(eventDatabase: EventDatabase): EventDao {
        return eventDatabase.eventDoa()
    }
}