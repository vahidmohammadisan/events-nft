package ir.vahidmohammadisan.lst.domain.repository

import kotlinx.coroutines.flow.Flow


interface WalletRepo {

    suspend fun saveWalletKeys(publicKey: String, privateKey: String, address: String)

    suspend fun getPublicKey(): Flow<String>

    suspend fun getPrivateKey(): Flow<String>

    suspend fun getAddress(): Flow<String>

}