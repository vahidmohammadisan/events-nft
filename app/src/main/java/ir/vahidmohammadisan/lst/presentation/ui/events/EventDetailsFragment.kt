package ir.vahidmohammadisan.lst.presentation.ui.events

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.Circle
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import dagger.hilt.android.AndroidEntryPoint
import ir.vahidmohammadisan.lst.R
import ir.vahidmohammadisan.lst.data.contract.Evn
import ir.vahidmohammadisan.lst.data.remote.dto.Event
import ir.vahidmohammadisan.lst.databinding.FragmentEventDetailsLayoutBinding
import ir.vahidmohammadisan.lst.presentation.ui.wallet.WalletViewModel
import ir.vahidmohammadisan.lst.utils.Constants
import ir.vahidmohammadisan.lst.utils.Constants.ALCHEMY_ADDRESS
import ir.vahidmohammadisan.lst.utils.Constants.CIRCLE_FILL_COLOR
import ir.vahidmohammadisan.lst.utils.Constants.CIRCLE_RADIUS
import ir.vahidmohammadisan.lst.utils.Constants.CIRCLE_STROKE_COLOR
import ir.vahidmohammadisan.lst.utils.Constants.CONTRACT_ADDRESS
import ir.vahidmohammadisan.lst.utils.Constants.DEFAULT_ZOOM
import ir.vahidmohammadisan.lst.utils.hasLocationPermission
import ir.vahidmohammadisan.lst.utils.isBeforeNow
import ir.vahidmohammadisan.lst.utils.isBetween
import ir.vahidmohammadisan.lst.utils.makeToastLong
import ir.vahidmohammadisan.lst.utils.toDateString
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.web3j.crypto.Credentials
import org.web3j.protocol.Web3j
import org.web3j.protocol.exceptions.TransactionException
import org.web3j.protocol.http.HttpService
import org.web3j.tx.gas.DefaultGasProvider.GAS_LIMIT
import org.web3j.tx.gas.DefaultGasProvider.GAS_PRICE
import java.math.BigInteger
import java.util.Date


@AndroidEntryPoint
class EventDetailsFragment : Fragment(), OnMapReadyCallback {

    private var _binding: FragmentEventDetailsLayoutBinding? = null
    private val binding get() = _binding!!

    private val viewModel: EventViewModel by viewModels()
    private val walletViewModel: WalletViewModel by viewModels()

    private lateinit var defaultLocation: LatLng
    private lateinit var event: Event
    private lateinit var circle: Circle
    private lateinit var googleMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private lateinit var web3j: Web3j
    private lateinit var credentials: Credentials
    private lateinit var contract: Evn
    private lateinit var address: String
    private var isEventSubscribed = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentEventDetailsLayoutBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        event = EventsFragment.getEvent()!!

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        defaultLocation = LatLng(event.location[0], event.location[1])

        binding.apply {
            category.text = event.category
            state.text = event.state
            title.text = event.title
            desc.text = event.description
            binding.start.text = event.start.toLong().toDateString(Constants.TIME_FORMAT)
            binding.end.text = event.end.toLong().toDateString(Constants.TIME_FORMAT)
            country.text = event.country
            isPrivate.text = event.isPrivate.toString()
        }

        if (event.state == "Active") {
            binding.state.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.md_green_700
                )
            )
        } else {
            binding.state.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.md_grey_900
                )
            )
        }

        walletViewModel.getPrivateKey()
        walletViewModel.privateKey.observe(viewLifecycleOwner) {
            web3j = Web3j.build(HttpService(ALCHEMY_ADDRESS))
            credentials =
                Credentials.create(it)

            contract = Evn.load(CONTRACT_ADDRESS, web3j, credentials, GAS_PRICE, GAS_LIMIT)
        }

        walletViewModel.getAddress()
        walletViewModel.address.observe(viewLifecycleOwner) {
            binding.tvAddress.text = makeSafeAddress(it)
            address = it
        }

        binding.tvWalletBalance.text = EventsFragment.getBalance()

        binding.btnSubscribe.setOnClickListener {
            if (isEventSubscribed) {
                showProgress()

                contract.getWinnerForEvent(BigInteger(event.id.toString()))
                    .sendAsync()
                    .thenApply { response ->
                        lifecycleScope.launch(Dispatchers.Main) {
                            hideProgress()
                            if (response.component2() == address) {
                                requireContext().makeToastLong("you are won!!!")
                                findNavController().navigate(R.id.question)
                            } else {
                                requireContext().makeToastLong("sorry , you are not won!!! the ${response.component2()} is win in this event!")
                            }
                        }
                    }
                    .exceptionally { throwable ->
                        endEvent(BigInteger(event.id.toString()))
                        lifecycleScope.launch(Dispatchers.Main) {
                            requireContext().makeToastLong(throwable.message.toString())
                        }
                    }
                    .thenAccept { winnerAddress -> }
            } else {
                checkPermissions()
            }
        }

        viewModel.isEventSubscribed(eventID = event.id.toString())

        viewModel.isEventSubscribed.observe(viewLifecycleOwner) {

            isEventSubscribed = it
            if (isEventSubscribed) {
                binding.txtSubscribe.text =
                    resources.getString(R.string.result_check)
            } else {
                binding.txtSubscribe.text = resources.getString(R.string.subscribe)
            }
        }

        binding.ivCopy.setOnClickListener {
            copyToClipboard(requireContext(), address)
        }
    }

    private fun endEvent(eventId: BigInteger) {
        if (isBeforeNow()) {
            lifecycleScope.launch(Dispatchers.Main) {
                binding.prSubscribe.visibility = View.GONE
                binding.txtSubscribe.text = resources.getString(R.string.result_check)
                requireContext().makeToastLong("The event has not Ended!!!")
            }
            return
        }
        contract.endEvent(eventId)
            .sendAsync()
            .thenApply { response ->
                contract.getWinnerForEvent(eventId)
                    .sendAsync()
                    .thenApply { response ->
                        lifecycleScope.launch(Dispatchers.Main) {
                            hideProgress()
                            if (response.component2() == address) {
                                requireContext().makeToastLong("you are won!!!")
                                findNavController().navigate(R.id.question)
                            } else {
                                requireContext().makeToastLong("sorry , you are not won!!!")
                            }
                        }
                    }
                    .exceptionally { throwable ->
                        lifecycleScope.launch(Dispatchers.Main) {
                            requireContext().makeToastLong(throwable.message.toString())
                            hideProgress()
                        }
                    }
                    .thenAccept { winnerAddress -> }
            }
            .exceptionally { throwable ->
                hideProgress()
                requireContext().makeToastLong(throwable.message.toString())
                null
            }
            .thenAccept { winnerAddress ->
            }
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map

        val defaultLocation = LatLng(event.location[0], event.location[1])

        googleMap.addMarker(
            MarkerOptions()
                .position(defaultLocation)
                .title(event.title)
        )

        googleMap.moveCamera(
            CameraUpdateFactory.newLatLngZoom(
                defaultLocation,
                DEFAULT_ZOOM
            )
        )

        circle = googleMap.addCircle(
            CircleOptions()
                .center(defaultLocation)
                .radius(CIRCLE_RADIUS)
                .strokeColor(CIRCLE_STROKE_COLOR)
                .fillColor(CIRCLE_FILL_COLOR)
        )

    }

    private fun checkPermissions() {
        val locationManager =
            requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        val isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

        if (!isNetworkEnabled) {
            requireContext().makeToastLong(getString(R.string.network_is_not_available))
        }

        if (!isGpsEnabled) {
            showOpenSettingsDialog(
                title = getString(R.string.gps_is_required),
                message = getString(R.string.please_enable_gps_in_your_device_settings),
                type = Constants.GPS_DIALOG_TYPE
            )
            return
        }

        if (requireContext().hasLocationPermission()) {
            continueWithMapOperations()
        } else {
            requestPermissions()
        }
    }

    private fun requestPermissions() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                )
            )
        } else {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
                )
            )
        }
    }

    private fun showOpenSettingsDialog(title: String, message: String, type: String? = null) {
        val dialogBuilder = AlertDialog.Builder(requireContext())
        dialogBuilder.setTitle(title)
            .setMessage(message)
            .setPositiveButton(getString(R.string.open_settings)) { _, _ ->
                if (type == Constants.GPS_DIALOG_TYPE) {
                    openGpsSettings()
                } else {
                    openAppSettings()
                }
            }
            .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
                checkPermissions()
            }
            .setCancelable(false)
            .show()
    }

    private fun openAppSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", requireContext().packageName, null)
        intent.data = uri
        openAppSettingsLauncher.launch(intent)
    }

    private fun openGpsSettings() {
        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        openAppSettingsLauncher.launch(intent)
    }

    private val openAppSettingsLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        onMapReady(googleMap)
    }

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions.all { it.value }) {
            onMapReady(googleMap)
        } else {
            showOpenSettingsDialog(
                title = getString(R.string.location_permission_required),
                message = getString(R.string.please_allow_all_the_time_location_permission)
            )
        }
    }

    @SuppressLint("MissingPermission")
    private fun continueWithMapOperations() {

        googleMap.isMyLocationEnabled = true

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                val userLocation = LatLng(location.latitude, location.longitude)

                val distance = calculateDistance(defaultLocation, userLocation)

                if (distance < CIRCLE_RADIUS) {
                    requireContext().makeToastLong("You are within 50 meters of the event!")
                    if (isCurrentTimeWithinRange()) {

                        if (!isEventSubscribed) {

                            showProgress()

                            lifecycleScope.launch(Dispatchers.IO) {
                                try {
                                    val transactionReceipt = contract.enrollForEvent(
                                        BigInteger(event.id.toString()),
                                    ).send()

                                    if (transactionReceipt.status == "0x1") {
                                        viewModel.insertEvent(eventID = event.id.toString())
                                        withContext(Dispatchers.Main) {
                                            requireContext().makeToastLong("Subscription was successfully")
                                            viewModel.isEventSubscribed(eventID = event.id.toString())
                                            hideProgress()
                                        }
                                    } else {
                                        withContext(Dispatchers.Main) {
                                            requireContext().makeToastLong("There is an Error! You can't subscribe.")
                                        }
                                    }
                                } catch (e: TransactionException) {
                                    e.printStackTrace()
                                    withContext(Dispatchers.Main) {
                                        requireContext().makeToastLong(e.message.toString())
                                        hideProgress()
                                    }
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                    withContext(Dispatchers.Main) {
                                        requireContext().makeToastLong(e.message.toString())
                                        hideProgress()
                                    }
                                }

                            }
                        } else {
                            requireContext().makeToastLong("you subscribed the event before.")
                            hideProgress()
                        }


                    } else {
                        requireContext().makeToastLong("Subscription for events is only possible during the event execution period.")
                    }
                } else {
                    requireContext().makeToastLong("It seems that you may not be at the event venue!")
                }

                googleMap.animateCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        userLocation,
                        DEFAULT_ZOOM
                    )
                )
            }
        }
    }

    private fun calculateDistance(location1: LatLng, location2: LatLng): Float {
        val results = FloatArray(1)
        Location.distanceBetween(
            location1.latitude, location1.longitude,
            location2.latitude, location2.longitude, results
        )
        return results[0]
    }

    private fun isCurrentTimeWithinRange(): Boolean {
        val currentDate = Date()
        val startDate = event.start.toLong() * 1000
        val endDate = event.end.toLong() * 1000
        return currentDate.isBetween(startDate, endDate)
    }

    private fun isBeforeNow(): Boolean {
        val currentDate = Date()
        val endDate = event.end.toLong() * 1000
        return currentDate.isBeforeNow(endDate)
    }

    private fun showProgress() {
        binding.txtSubscribe.text = ""
        binding.prSubscribe.visibility = View.VISIBLE
    }

    private fun hideProgress() {
        if (isEventSubscribed)
            binding.txtSubscribe.text = resources.getString(R.string.result_check)
        else
            binding.txtSubscribe.text = resources.getString(R.string.subscribe)
        binding.prSubscribe.visibility = View.GONE
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}