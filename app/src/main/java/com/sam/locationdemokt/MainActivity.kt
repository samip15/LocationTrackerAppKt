package com.sam.locationdemokt

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import java.util.*

class MainActivity : AppCompatActivity() {
    // Now we need to create veriables that we will need
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    lateinit var locationRequest: LocationRequest

    // the permission id
    private var PERMISSION_ID = 15
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // now let us initialize the fused provider client
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        // now lets add the event to our button
        val btnPos = findViewById<Button>(R.id.getPos)
        btnPos.setOnClickListener {
            getLastLocation()
            // now we will create a new function that return the city name and the country name
            // now let's add these value to our location function
        }

    }

    // first we will create a function that will allow us to get last location
    private fun getLastLocation() {
        if (checkPermission()) {
            // now we check the location service is enabled
            if (isLocationEnabled()) {
                // now lets get the location
                fusedLocationProviderClient.lastLocation.addOnCompleteListener { task ->
                    var location = task.result
                    if (location == null) {
                        // if the location is null we will get the new user location
                        // so we need to create new function
                        // don't forget to add the new location function
                        getNewLocation()
                    } else {
                        // location.latitude will return the latitude coordinates
                        // location.longitude will return the longitude coordinates
                        val locationTxt = findViewById<TextView>(R.id.location_txt)
                        locationTxt.text = "Your current coordinates are :\nLat:" +
                                location.latitude + "; Long:" +
                                location.longitude + "\nYour City: " +
                                getCityName(
                                    location.latitude,
                                    location.longitude
                                ) + ", your country: " +
                                getCountryName(location.latitude, location.longitude)
                    }
                }
            } else {
                Toast.makeText(this, "Please Enable Your Location Service", Toast.LENGTH_SHORT)
                    .show()
            }
        } else {
            RequestPermission()
        }
    }

    private fun getNewLocation() {
        locationRequest = LocationRequest()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 0
        locationRequest.fastestInterval = 0
        locationRequest.numUpdates = 2
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        fusedLocationProviderClient!!.requestLocationUpdates(
            locationRequest, locationCallback, Looper.myLooper()
        )
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(p0: LocationResult) {
            var lastLocation = p0.lastLocation
            // now we will set the new location
            val locationTxt = findViewById<TextView>(R.id.location_txt)
            locationTxt.text =
                "Your current coordinates are :\nLat:" +
                        lastLocation.latitude + "; Long:" +
                        lastLocation.longitude + "\nYour City: " +
                        getCityName(
                            lastLocation.latitude,
                            lastLocation.longitude
                        ) + ", your country: " +
                        getCountryName(lastLocation.latitude, lastLocation.longitude)
        }
    }


    // first we need to create a function that will check the uses permission
    private fun checkPermission(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

    // now we need to create a function that will allow us to get user permission
    private fun RequestPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ), PERMISSION_ID
        )
    }

    // now we need a function that check if the location service of a device is enabled
    private fun isLocationEnabled(): Boolean {
        var locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    //function to get the city name
    private fun getCityName(lat: Double, long: Double): String {
        var cityName = ""
        var geoCoder = Geocoder(this, Locale.getDefault())
        var Address = geoCoder.getFromLocation(lat, long, 1)
        cityName = Address.get(0).locality
        return cityName
    }

    // now we will create the function that return the country name

    private fun getCountryName(lat: Double, long: Double): String {
        var countryName = ""
        var geoCoder = Geocoder(this, Locale.getDefault())
        var Address = geoCoder.getFromLocation(lat, long, 1)
        countryName = Address.get(0).countryName
        return countryName
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        // this is the build in function that check the permission result
        if (requestCode == PERMISSION_ID) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.e("Debug", "You Have The Permission")
            }
        }
    }
}