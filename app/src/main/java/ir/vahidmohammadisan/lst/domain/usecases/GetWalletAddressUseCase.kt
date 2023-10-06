package ir.vahidmohammadisan.lst.domain.usecases

import ir.vahidmohammadisan.lst.domain.repository.WalletRepo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetWalletAddressUseCase @Inject constructor(
    private val walletRepo: WalletRepo
) {
    suspend operator fun invoke(): Flow<String> {
        return walletRepo.getAddress()
    }
}