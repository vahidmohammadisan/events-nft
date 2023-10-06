package ir.vahidmohammadisan.lst.presentation.ui.wallet

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.vahidmohammadisan.lst.domain.usecases.GetWalletAddressUseCase
import ir.vahidmohammadisan.lst.domain.usecases.GetWalletPrivateKeyUseCase
import ir.vahidmohammadisan.lst.domain.usecases.GetWalletPublicKeyUseCase
import ir.vahidmohammadisan.lst.domain.usecases.SaveWalletUseCase
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WalletViewModel @Inject constructor(
    private val saveWalletUseCase: SaveWalletUseCase,
    private val getWalletPublicKeyUseCase: GetWalletPublicKeyUseCase,
    private val getWalletPrivateKeyUseCase: GetWalletPrivateKeyUseCase,
    private val getWalletAddressUseCase: GetWalletAddressUseCase,
) : ViewModel() {

    var publicKey: MutableLiveData<String> = MutableLiveData()
    var privateKey: MutableLiveData<String> = MutableLiveData()
    var address: MutableLiveData<String> = MutableLiveData()

    fun saveWallet(
        publicKey: String,
        privateKey: String,
        address: String
    ) {
        viewModelScope.launch {
            saveWalletUseCase.invoke(
                publicKey = publicKey,
                privateKey = privateKey,
                address = address
            )
        }
    }

    fun getPublicKey() {
        viewModelScope.launch {
            getWalletPublicKeyUseCase.invoke().collect() {
                publicKey.value = it
            }
        }

    }

    fun getPrivateKey() {
        viewModelScope.launch {
            getWalletPrivateKeyUseCase.invoke().collect() {
                privateKey.value = it
            }
        }
    }

    fun getAddress() {
        viewModelScope.launch {
            getWalletAddressUseCase.invoke().collect() {
                address.value = it
            }
        }
    }

}