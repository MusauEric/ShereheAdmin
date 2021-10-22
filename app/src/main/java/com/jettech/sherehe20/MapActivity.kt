package com.jettech.sherehe20

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Address
import android.location.Geocoder
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnCameraIdleListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import es.dmoral.toasty.Toasty
import java.io.IOException
import java.util.*
import kotlin.math.log

@Suppress("DEPRECATED_IDENTITY_EQUALS")
class MapActivity : AppCompatActivity(),  OnMapReadyCallback, PermissionListener {

    companion object {
        const val REQUEST_CHECK_SETTINGS = 43
        const val MAP_PIN_LOCATION_REQUEST_CODE = 43
    }

    private lateinit var googleMap: GoogleMap
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
   // private var mMap: GoogleMap? = null


    var mapFragment: SupportMapFragment? = null
    private var mMap: GoogleMap? = null
    private var onCameraIdleListener: OnCameraIdleListener? = null

    var addressName :String =""
    var lat :String =""
    var long :String =""



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        val setLocation = findViewById<Button>(R.id.setLocation)


        if (Build.VERSION.SDK_INT < 22) setStatusBarTranslucent(false) else setStatusBarTranslucent(
            true
        )

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)
        fusedLocationProviderClient = FusedLocationProviderClient(this)


        configureCameraIdle()

        setLocation.setOnClickListener {

            if (lat.equals("")||long.equals("")||addressName.equals("")){
                Toasty.error(
                    this@MapActivity, "Please set your address", Toasty.LENGTH_LONG
                ).show()
                return@setOnClickListener
            }else{
                getAddress ()
            }

        }


    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(Map: GoogleMap) {
        mMap = Map

        googleMap = Map?: return
        if (isPermissionGiven()){
            googleMap.isMyLocationEnabled = true
            googleMap.uiSettings.isMyLocationButtonEnabled = true
            googleMap.uiSettings.isZoomControlsEnabled = true
            getCurrentLocation()
        } else {
            givePermission()
        }
        mMap!!.setOnCameraIdleListener(onCameraIdleListener)

    }


    private fun configureCameraIdle() {
        onCameraIdleListener = OnCameraIdleListener {
            val latLng = mMap!!.cameraPosition.target
            val geocoder = Geocoder(this@MapActivity)
            try {
                val addressList = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
                if (addressList != null && addressList.size > 0) {
                    val locality = addressList[0].getAddressLine(0)
                    val country = addressList[0].countryName
                      lat =   addressList[0].latitude.toString()
                      long =   addressList[0].longitude.toString()

                    val result = findViewById<TextView>(R.id.drag_result)

                    if (!locality.isEmpty() && !country.isEmpty()) {
                        addressName = "$locality  $country"
                        result!!.text = "$locality  $country"
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    protected fun setStatusBarTranslucent(makeTranslucent: Boolean) {
        if (makeTranslucent) {
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        } else {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        }
    }

    private fun isPermissionGiven(): Boolean{
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    private fun givePermission() {
        Dexter.withActivity(this)
            .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
            .withListener(this@MapActivity)
            .check()
    }

    override fun onPermissionGranted(response: PermissionGrantedResponse?) {
        getCurrentLocation()
    }

    override fun onPermissionRationaleShouldBeShown(
        permission: PermissionRequest?,
        token: PermissionToken?
    ) {
        token!!.continuePermissionRequest()
    }

    override fun onPermissionDenied(response: PermissionDeniedResponse?) {
        Toast.makeText(this, "Permission required for showing location", Toast.LENGTH_LONG).show()
        finish()
    }

    private fun getCurrentLocation() {

        val locationRequest = LocationRequest()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = (10 * 1000).toLong()
        locationRequest.fastestInterval = 2000

        val builder = LocationSettingsRequest.Builder()
        builder.addLocationRequest(locationRequest)
        val locationSettingsRequest = builder.build()

        val result = LocationServices.getSettingsClient(this).checkLocationSettings(locationSettingsRequest)
        result.addOnCompleteListener { task ->
            try {
                val response = task.getResult(ApiException::class.java)
                if (response!!.locationSettingsStates.isLocationPresent){
                    getLastLocation()
                }
            } catch (exception: ApiException) {
                when (exception.statusCode) {
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> try {
                        val resolvable = exception as ResolvableApiException
                        resolvable.startResolutionForResult(this, REQUEST_CHECK_SETTINGS)
                    } catch (e: IntentSender.SendIntentException) {
                    } catch (e: ClassCastException) {
                    }

                    LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> { }
                }
            }
        }
    }


    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        fusedLocationProviderClient.lastLocation
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful && task.result != null) {
                    val mLastLocation = task.result

                    var address = "No known address"

                    val gcd = Geocoder(this, Locale.getDefault())
                    val addresses: List<Address>
                    try {
                        addresses = gcd.getFromLocation(mLastLocation!!.latitude, mLastLocation.longitude, 1)
                        if (addresses.isNotEmpty()) {
                            address = addresses[0].getAddressLine(0)
                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }

//                    val icon = BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(this.resources, R.drawable.pin))
//                    googleMap.addMarker(
//                        MarkerOptions()
//                            .position(LatLng(mLastLocation!!.latitude, mLastLocation.longitude))
//                            .title("Current Location")
//                            .snippet(address)
//                            .icon(icon)
//                    )

                    val cameraPosition = CameraPosition.Builder()
                        .target(LatLng(mLastLocation.latitude, mLastLocation.longitude))
                        .zoom(17f)
                        .build()
                    googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
                } else {
                    Toast.makeText(this, "No current location found", Toast.LENGTH_LONG).show()
                }
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        when (requestCode) {
            REQUEST_CHECK_SETTINGS -> {
                if (resultCode == Activity.RESULT_OK) {
                    getCurrentLocation()
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)

    }


    private fun getAddress () {

       val progressDialog = Dialog(this)
        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        progressDialog.setContentView(R.layout.custom_dialog_progress)
        val progressTv = progressDialog.findViewById(R.id.progress_tv) as TextView
        progressTv.text = resources.getString(R.string.loading)
        progressTv.setTextColor(ContextCompat.getColor(this, R.color.pink))
        progressTv.textSize = 15F

        progressDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        progressDialog.setCancelable(false)
        progressDialog.show()

        val i = Intent(this, MainActivity::class.java)
        i.putExtra("latitude", lat.toString())
        i.putExtra("longitude", long)
        i.putExtra("address", addressName)
        this.startActivity(i)
        finish()


        progressDialog.dismiss()

    }




}