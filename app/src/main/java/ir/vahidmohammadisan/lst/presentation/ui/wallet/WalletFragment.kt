package ir.vahidmohammadisan.lst.presentation.ui.wallet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import ir.vahidmohammadisan.lst.R
import ir.vahidmohammadisan.lst.databinding.FragmentWalletBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.web3j.crypto.Bip32ECKeyPair
import org.web3j.crypto.Credentials
import org.web3j.crypto.MnemonicUtils
import java.security.SecureRandom

@AndroidEntryPoint
class WalletFragment : Fragment() {

    private var _binding: FragmentWalletBinding? = null
    private val binding get() = _binding!!

    private val viewModel: WalletViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentWalletBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getPublicKey()

        binding.btnCreateWallet.setOnClickListener {

            lifecycleScope.launch(Dispatchers.IO) {

                try {
                    val random = SecureRandom()
                    val bytes = ByteArray(16)
                    random.nextBytes(bytes)
                    val mnemonic = MnemonicUtils.generateMnemonic(bytes)
                    val seed = MnemonicUtils.generateSeed(mnemonic, null)
                    val masterKeypair = Bip32ECKeyPair.generateKeyPair(seed)
                    val bip44 = keyGeneration(masterKeypair)
                    val credentials = Credentials.create(bip44)

                    val publicKey = credentials.ecKeyPair.publicKey.toString(16)
                    val privateKey = credentials.ecKeyPair.privateKey.toString(16)

                    val address = credentials.address

                    viewModel.saveWallet(publicKey, privateKey, address)

                    withContext(Dispatchers.Main) {
                        binding.imageWallet.speed = 0.5F
                        binding.imageWallet.playAnimation()
                        delay(3000)
                        findNavController().navigate(R.id.action_wallet_to_events)
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }

        }

        binding.btnImportWallet.setOnClickListener {
            val privateKey = binding.edtPrivateKey.text.toString().trim()

            if (isValidPrivateKey(privateKey)) {
                // Proceed with wallet creation using the imported private key
                createWalletFromPrivateKey(privateKey)
            } else {
                // Handle invalid private key input
                showToast("Invalid private key")
            }
        }

        binding.btnImportWalletMnemonic.setOnClickListener {
            val mnemonicPhrase = binding.edtMnemonic.text.toString().trim()

            if (isValidMnemonic(mnemonicPhrase)) {
                // Proceed with wallet creation using the imported mnemonic phrase
                createWalletFromMnemonic(mnemonicPhrase)
            } else {
                // Handle invalid mnemonic phrase input
                showToast("Invalid mnemonic phrase")
            }
        }

        viewModel.publicKey.observe(viewLifecycleOwner) {
            if (it != null && it != "") {
                findNavController().navigate(R.id.action_wallet_to_events)
            }
        }

    }

    // Validate a private key (simplified validation)
    private fun isValidPrivateKey(privateKey: String): Boolean {
        // Add your validation logic here
        return privateKey.length == 64 // Check if it's 64 characters long, for example
    }

    // Create a wallet using the imported private key
    private fun createWalletFromPrivateKey(privateKey: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val credentials = Credentials.create(privateKey)
                val publicKey = credentials.ecKeyPair.publicKey.toString(16)
                val address = credentials.address

                viewModel.saveWallet(publicKey, privateKey, address)

                withContext(Dispatchers.Main) {
                    binding.imageWallet.speed = 0.5F
                    binding.imageWallet.playAnimation()
                    delay(3000)
                    findNavController().navigate(R.id.action_wallet_to_events)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                showToast("Error creating wallet from private key")
            }
        }
    }

    // Validate a mnemonic phrase (simplified validation)
    private fun isValidMnemonic(mnemonic: String): Boolean {
        // Add your validation logic here
        return mnemonic.isNotEmpty() // Check if it's not empty, for example
    }

    // Create a wallet using the imported mnemonic phrase
    private fun createWalletFromMnemonic(mnemonic: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val seed = MnemonicUtils.generateSeed(mnemonic, null)
                val masterKeypair = Bip32ECKeyPair.generateKeyPair(seed)
                val bip44 = keyGeneration(masterKeypair)
                val credentials = Credentials.create(bip44)

                val publicKey = credentials.ecKeyPair.publicKey.toString(16)
                val privateKey = credentials.ecKeyPair.privateKey.toString(16)
                val address = credentials.address

                viewModel.saveWallet(publicKey, privateKey, address)

                withContext(Dispatchers.Main) {
                    binding.imageWallet.speed = 0.5F
                    binding.imageWallet.playAnimation()
                    delay(3000)
                    findNavController().navigate(R.id.action_wallet_to_events)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                showToast("Error creating wallet from mnemonic phrase")
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun keyGeneration(master: Bip32ECKeyPair): Bip32ECKeyPair {
        val path = intArrayOf(
            44 or Bip32ECKeyPair.HARDENED_BIT,
            60 or Bip32ECKeyPair.HARDENED_BIT,
            0 or Bip32ECKeyPair.HARDENED_BIT,
            0,
            0
        )
        return Bip32ECKeyPair.deriveKeyPair(master, path)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}