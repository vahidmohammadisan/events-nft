package ir.vahidmohammadisan.lst.presentation.di

import android.content.SharedPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ir.vahidmohammadisan.lst.data.local.dao.EventDao
import ir.vahidmohammadisan.lst.data.remote.ApiService
import ir.vahidmohammadisan.lst.data.repository.EventsRepoImpl
import ir.vahidmohammadisan.lst.data.repository.WalletRepoImpl
import ir.vahidmohammadisan.lst.domain.repository.EventsRepo
import ir.vahidmohammadisan.lst.domain.repository.WalletRepo

@Module
@InstallIn(SingletonComponent::class)
object RepoModule {
    @Provides
    fun provideWalletRepo(sharedPreferences: SharedPreferences): WalletRepo {
        return WalletRepoImpl(sharedPreferences)
    }

    @Provides
    fun provideEventsRepo(
        sharedPreferences: SharedPreferences,
        apiService: ApiService,
        eventDao: EventDao
    ): EventsRepo {
        return EventsRepoImpl(sharedPreferences, apiService, eventDao)
    }
}