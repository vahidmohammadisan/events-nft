package ir.vahidmohammadisan.lst.presentation.di

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ir.vahidmohammadisan.lst.utils.Constants.ACCESS_TOKEN
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SecurityModule {

    @Provides
    @Singleton
    @Named("Authorization")
    fun provideAuthorizationHeader(): String {
        return "Bearer $ACCESS_TOKEN"
    }

    @Provides
    @Singleton
    fun provideMasterKeyAlias(@ApplicationContext context: Context): String {
        return MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
    }

    @Provides
    @Singleton
    fun provideEncryptedSharedPreferences(
        @ApplicationContext context: Context,
        masterKeyAlias: String
    ): SharedPreferences {
        return EncryptedSharedPreferences.create(
            "my_encrypted_prefs",
            masterKeyAlias,
            context,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }
}
