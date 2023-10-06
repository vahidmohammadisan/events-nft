package ir.vahidmohammadisan.lst.domain.usecases

import ir.vahidmohammadisan.lst.domain.repository.WalletRepo
import javax.inject.Inject

class SaveWalletUseCase @Inject constructor(
    private val walletRepo: WalletRepo
) {
    suspend operator fun invoke(publicKey: String, privateKey: String, address: String) {
        walletRepo.saveWalletKeys(publicKey, privateKey, address)
    }
}