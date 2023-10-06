package ir.vahidmohammadisan.lst.data.repository

import android.content.SharedPreferences
import ir.vahidmohammadisan.lst.domain.repository.WalletRepo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class WalletRepoImpl(
    private val sharedPreferences: SharedPreferences
) : WalletRepo {
    override suspend fun saveWalletKeys(publicKey: String, privateKey: String,address:String) {
        val editor = sharedPreferences.edit()
        editor.putString("public_key", publicKey)
        editor.putString("private_key", privateKey)
        editor.putString("address", address)
        editor.apply()
    }

    override suspend fun getPublicKey(): Flow<String> = flow {
        val publicKey = sharedPreferences.getString("public_key", null)
        if (publicKey != null) {
            emit(publicKey)
        }
    }

    override suspend fun getPrivateKey(): Flow<String> = flow {
        val privateKey = sharedPreferences.getString("private_key", null)
        if (privateKey != null) {
            emit(privateKey)
        }
    }

    override suspend fun getAddress(): Flow<String> = flow {
        val address = sharedPreferences.getString("address", null)
        if (address != null) {
            emit(address)
        }
    }


}