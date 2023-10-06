package ir.vahidmohammadisan.lst.presentation.ui.nft

import IPFSImageUploader
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import ir.vahidmohammadisan.lst.R
import ir.vahidmohammadisan.lst.data.contract.NFT
import ir.vahidmohammadisan.lst.databinding.FragmentNftBinding
import ir.vahidmohammadisan.lst.presentation.ui.events.EventsFragment
import ir.vahidmohammadisan.lst.presentation.ui.wallet.WalletViewModel
import ir.vahidmohammadisan.lst.utils.Constants.ALCHEMY_ADDRESS
import ir.vahidmohammadisan.lst.utils.Constants.IPFS_API_KEY
import ir.vahidmohammadisan.lst.utils.Constants.IPFS_JWT_TOKEN
import ir.vahidmohammadisan.lst.utils.Constants.IPFS_URL
import ir.vahidmohammadisan.lst.utils.Constants.TOKEN_ID
import ir.vahidmohammadisan.lst.utils.IPFSJsonUploader
import ir.vahidmohammadisan.lst.utils.IPFSUploadResponse
import ir.vahidmohammadisan.lst.utils.makeToastLong
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import org.web3j.crypto.Credentials
import org.web3j.protocol.Web3j
import org.web3j.protocol.core.methods.response.TransactionReceipt
import org.web3j.protocol.http.HttpService
import org.web3j.tx.gas.DefaultGasProvider

@AndroidEntryPoint
class NFTFragment : Fragment() {

    private var _binding: FragmentNftBinding? = null
    private val binding get() = _binding!!

    private val viewModel: WalletViewModel by viewModels()

    private lateinit var web3j: Web3j
    private lateinit var privateKey: String
    private lateinit var address: String
    private lateinit var contract_address: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentNftBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getPrivateKey()
        viewModel.privateKey.observe(viewLifecycleOwner) {
            privateKey = it
        }

        viewModel.getAddress()
        viewModel.address.observe(viewLifecycleOwner) {
            binding.tvAddress.text = makeSafeAddress(it)
            address = it
        }

        binding.tvWalletBalance.text = EventsFragment.getBalance()

        binding.btnMint.setOnClickListener {

            if (binding.edNftName.text.toString().isEmpty() || binding.edNftSymbol.text.toString()
                    .isEmpty() || binding.edNftDesc.text.toString().isEmpty()
            ) {
                return@setOnClickListener
            }

            showProgress()

            val imageUploader = IPFSImageUploader(requireContext(), IPFS_API_KEY, IPFS_JWT_TOKEN)

            val bitmap = generateBitmap(EventsFragment.getEvent()?.id.toString())

            lifecycleScope.launch(Dispatchers.IO) {

                val response = imageUploader.uploadImageToIPFS(bitmap)!!
                if (response != null) {
                    val ipfsUploadResponse =
                        Gson().fromJson(response, IPFSUploadResponse::class.java)
                    val ipfsCid = ipfsUploadResponse.IpfsHash

                    val jsonObject = JSONObject().apply {
                        put("name", binding.edNftName.text.toString())
                        put("description", binding.edNftDesc.text.toString())
                        put("image", "ipfs://$ipfsCid")
                    }

                    val pinataUploader = IPFSJsonUploader(IPFS_API_KEY, IPFS_JWT_TOKEN, jsonObject)
                    val response = pinataUploader.pinJSONToIPFS()

                    if (response != null) {
                        val ipfsUploadResponse =
                            Gson().fromJson(response, IPFSUploadResponse::class.java)
                        val ipfsHash = ipfsUploadResponse.IpfsHash

                        lifecycleScope.launch(Dispatchers.IO) {
                            deployContract("$IPFS_URL$ipfsHash")
                        }

                    } else {
                        requireContext().makeToastLong("Uplading json was failed!!!")
                        hideProgress()
                    }
                } else {
                    requireContext().makeToastLong("Uplading image was failed!!!")
                    hideProgress()
                }
            }
        }
        binding.ivCopy.setOnClickListener {
            copyToClipboard(requireContext(), address)
        }

        binding.tvContractAddress.setOnClickListener {
            copyToClipboard(requireContext(), contract_address)
        }
    }

    private fun generateBitmap(text: String): Bitmap {

        val width = 500
        val height = 500
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawColor(Color.BLACK)

        // Create a Paint object for drawing text
        val textPaint = Paint().apply {
            color = Color.WHITE
            textSize = 40f // Text size in pixels
        }

        // Calculate the position to center the text
        val x = (width - textPaint.measureText(text)) / 2
        val y = (height - textPaint.descent() - textPaint.ascent()) / 2

        // Draw the text on the bitmap
        canvas.drawText(text, x, y, textPaint)
        return bitmap
    }

    private suspend fun deployContract(uri: String) {

        web3j =
            Web3j.build(HttpService(ALCHEMY_ADDRESS))

        val deployedContractAddress = NFT.deploy(
            web3j,
            getCredentialsFromPrivateKey(),
            DefaultGasProvider(),
            binding.edNftName.text.toString(), binding.edNftSymbol.text.toString()
        ).send()


        withContext(Dispatchers.Main) {
            requireContext().makeToastLong("contract has deployed!!!")
            binding.tvContractAddress.visibility = View.VISIBLE
            binding.tvContractTitle.visibility = View.VISIBLE
            binding.tvTokenIdtitle.visibility = View.VISIBLE
            binding.tvTokenId.visibility = View.VISIBLE
            contract_address = deployedContractAddress.contractAddress
            binding.tvContractAddress.text =
                makeSafeAddress(deployedContractAddress.contractAddress)
            binding.tvTokenId.text =
                TOKEN_ID.toString()
        }

        val transactionReceipt: TransactionReceipt = deployedContractAddress.mint(
            address,
            TOKEN_ID.toBigInteger(),
            uri
        ).send()

        withContext(Dispatchers.Main) {
            hideProgress()
            requireContext().makeToastLong(transactionReceipt.toString())
        }

    }

    private fun getCredentialsFromPrivateKey(): Credentials {
        return Credentials.create(privateKey)
    }

    private fun makeSafeAddress(input: String): String {
        val firstPart = input.substring(0, 5)
        val middlePart = "*".repeat(5)
        val lastPart = input.substring(input.length - 5)
        return "$firstPart$middlePart$lastPart"
    }

    private fun copyToClipboard(context: Context, textToCopy: String) {
        val clipboardManager =
            context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = ClipData.newPlainText("text", textToCopy)
        clipboardManager.setPrimaryClip(clipData)
        context.makeToastLong("Text copied to clipboard")
    }

    private fun showProgress() {
        binding.btnMint.text = ""
        binding.prMint.visibility = View.VISIBLE
    }

    private fun hideProgress() {
        binding.btnMint.text = resources.getString(R.string.mint)
        binding.prMint.visibility = View.GONE
    }

}