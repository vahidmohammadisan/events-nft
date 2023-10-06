package ir.vahidmohammadisan.lst.presentation.ui.events

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import ir.vahidmohammadisan.lst.R
import ir.vahidmohammadisan.lst.data.contract.Evn
import ir.vahidmohammadisan.lst.data.remote.dto.ApiResponse
import ir.vahidmohammadisan.lst.data.remote.dto.Event
import ir.vahidmohammadisan.lst.databinding.FragmentEventsBinding
import ir.vahidmohammadisan.lst.presentation.ui.wallet.WalletViewModel
import ir.vahidmohammadisan.lst.utils.Constants
import ir.vahidmohammadisan.lst.utils.makeToastLong
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.web3j.crypto.Credentials
import org.web3j.protocol.Web3j
import org.web3j.protocol.http.HttpService
import org.web3j.tx.gas.DefaultGasProvider

@AndroidEntryPoint
class EventsFragment : Fragment(), EventsAdapter.EventClickListener {

    private var _binding: FragmentEventsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: EventViewModel by viewModels()
    private val walletViewModel: WalletViewModel by viewModels()
    private lateinit var adapter: EventsAdapter

    private lateinit var address: String

    private lateinit var web3j: Web3j
    private lateinit var credentials: Credentials
    private lateinit var contract: Evn

    companion object {
        private var savedEvent: Event? = null
        private var walletBalance: String? = null

        fun setBalance(balance: String) {
            walletBalance = balance
        }

        fun getBalance(): String? {
            return walletBalance
        }

        fun setEvent(event: Event) {
            savedEvent = event
        }

        fun getEvent(): Event? {
            return savedEvent
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentEventsBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getEvents()
        walletViewModel.getAddress()

        setUpRecyclerView()

        viewModel.eventLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is ApiResponse.Success -> {
                    binding.prLoad.visibility = View.GONE
                    adapter.setItems(it.data)
                }

                is ApiResponse.Error -> {
                    binding.prLoad.visibility = View.GONE
                    requireContext().makeToastLong(it.exception.message.toString())
                }

                is ApiResponse.Loading -> {
                    binding.prLoad.visibility = View.VISIBLE
                }
            }
        }

        walletViewModel.address.observe(viewLifecycleOwner) {

            address = it
            binding.tvAddress.text = makeSafeAddress(it)

            walletViewModel.getPrivateKey()
            walletViewModel.privateKey.observe(viewLifecycleOwner) {
                web3j = Web3j.build(HttpService(Constants.ALCHEMY_ADDRESS))
                credentials =
                    Credentials.create(it)

                contract = Evn.load(
                    Constants.CONTRACT_ADDRESS, web3j, credentials,
                    DefaultGasProvider.GAS_PRICE,
                    DefaultGasProvider.GAS_LIMIT
                )

                lifecycleScope.launch(Dispatchers.IO) {
                    try {
                        val balance = contract.getAddressBalance(address)
                            .send()

                        withContext(Dispatchers.Main) {
                            if (balance.toString() == "0x0") {
                                binding.tvWalletBalance.text = "0.0 ETH"
                                setBalance("0.0 ETH")
                            } else {
                                binding.tvWalletBalance.text =
                                    "${balance.toFloat() / Constants.DIVIDE_BALANCE.toFloat()} ETH"
                                setBalance("${balance.toFloat() / Constants.DIVIDE_BALANCE.toFloat()} ETH")
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

            }

        }

        binding.ivCopy.setOnClickListener {
            copyToClipboard(requireContext(), address)
        }

    }

    private fun setUpRecyclerView() {
        adapter = EventsAdapter(emptyList(), this, requireContext())
        binding.eventsRecyclerView.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onEventClick(event: Event) {
        setEvent(event)
        findNavController().navigate(R.id.details)
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
}