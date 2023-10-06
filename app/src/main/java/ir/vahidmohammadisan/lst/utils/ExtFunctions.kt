package ir.vahidmohammadisan.lst.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.model.LatLng
import java.text.SimpleDateFormat
import java.util.Date
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

fun Context.hasLocationPermission(): Boolean {
    return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
        ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
    } else {
        ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
    }
}

fun Context.makeToastShort(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun Context.makeToastLong(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}

fun List<LatLng>.calculateTotalDistanceInKilometers(): Double {
    var totalDistance = 0.0

    if (isEmpty()) {
        return totalDistance
    }

    var previousLocation: LatLng? = null

    for (location in this) {
        if (previousLocation != null) {
            val distance = calculateDistanceBetween(
                previousLocation.latitude,
                previousLocation.longitude,
                location.latitude,
                location.longitude
            )

            if (distance > 0.02) {
                totalDistance += distance
            }
        }

        previousLocation = location
    }

    return totalDistance
}


private fun calculateDistanceBetween(
    lat1: Double, lon1: Double, lat2: Double, lon2: Double
): Double {
    val radius = 6371.0 // Earth's radius in kilometers

    val dLat = Math.toRadians(lat2 - lat1)
    val dLon = Math.toRadians(lon2 - lon1)
    val a = sin(dLat / 2) * sin(dLat / 2) +
            cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
            sin(dLon / 2) * sin(dLon / 2)
    val c = 2 * atan2(sqrt(a), sqrt(1 - a))

    return radius * c
}

fun Date.isBetween(startDate: Long, endDate: Long): Boolean {
    val start = Date(startDate)
    val end = Date(endDate)
    return !this.before(start) && !this.after(end)
}

fun Date.isBeforeNow(endDate: Long): Boolean {
    val end = Date(endDate)
    return this.before(end)
}

fun Long.toDateString(format: String): String {
    val date = Date(this * 1000)
    val dateFormat = SimpleDateFormat(format)
    return dateFormat.format(date)
}