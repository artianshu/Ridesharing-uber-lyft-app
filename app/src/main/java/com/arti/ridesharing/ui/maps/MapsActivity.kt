package com.arti.ridesharing.ui.maps

import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.widget.Toast
import com.arti.ridesharing.R
import com.arti.ridesharing.data.NetworkService
import com.arti.ridesharing.utils.MapsUtils
import com.arti.ridesharing.utils.PermissionUtils
import com.arti.ridesharing.utils.ViewUtils
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*


class MapsActivity : AppCompatActivity(), OnMapReadyCallback, MapsView {
    companion object{
        private const val TAG = "MapsActivity"
        private const val LOCATION_PERMISSION_REQUEST_CODE = 999
    }

    private lateinit var googleMap: GoogleMap
    private lateinit var presenter: MapsPresenter
    private var fusedLocationProviderClient:FusedLocationProviderClient ?= null
    private lateinit var locationCallback: LocationCallback
    private var currentLatLng : LatLng?= null
    private val nearByCabMarkerList = arrayListOf<Marker>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        ViewUtils.enableTransparentStatusBar(window)
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        presenter = MapsPresenter(NetworkService())
        presenter.onAttach(this)
    }

    private fun moveCamera(latLng: LatLng){
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))
    }

    private fun animateCamera(latLng: LatLng){
        val cameraPosition = CameraPosition.Builder()
            .target(latLng).zoom(15.5f).build()
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
    }

    fun addCarMarkerandGet(latLng: LatLng) : Marker{
        val bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(MapsUtils.getCarBitmap(this))
        return googleMap.addMarker(MarkerOptions().position(latLng).flat(true).icon(bitmapDescriptor))
    }

    private fun enableMyLocationOnMap(){
        googleMap.setPadding(0, ViewUtils.dpToPx(48f), 0,0)
        googleMap.isMyLocationEnabled = true
    }


    private fun setupLocationListener(){
        fusedLocationProviderClient = FusedLocationProviderClient(this)
        val locationRequest = LocationRequest().setInterval(2000)
            .setFastestInterval(2000)
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)

        locationCallback = object : LocationCallback(){
            override fun onLocationResult(p0: LocationResult?) {
                super.onLocationResult(p0)
                if (currentLatLng == null){
                    for (location in p0!!.locations){
                        if (currentLatLng == null){
                            currentLatLng = LatLng(location.latitude, location.longitude)
                            enableMyLocationOnMap()
                            moveCamera(currentLatLng!!)
                            animateCamera(currentLatLng!!)
                            presenter.requestNearByCabs(currentLatLng!!)
                        }
                    }
                }


            }
        }
        fusedLocationProviderClient?.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.myLooper()
        )
    }
    override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap
    }

    override fun onStart() {
        super.onStart()
        when{
            PermissionUtils.isAccessFineLocalGranted(this) ->{
                when{
                    PermissionUtils.isLocationEnabled(this) ->{
                        setupLocationListener()

                    } else ->{
                    PermissionUtils.showGPSNotEnabledDialog(this)
                }
                }

            }
            else -> {
                PermissionUtils.requestAccessFineLocation(this,
                LOCATION_PERMISSION_REQUEST_CODE
                )
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            LOCATION_PERMISSION_REQUEST_CODE ->{
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    when {
                        PermissionUtils.isLocationEnabled(this) -> {
                            setupLocationListener()

                        }
                        else -> {
                            PermissionUtils.showGPSNotEnabledDialog(this)
                        }
                    }
                }else{
                Toast.makeText(this, "Location Permission Not Granted", Toast.LENGTH_SHORT).show()
                }

            }
        }
    }

    override fun onDestroy() {
        presenter.onDetach()
        super.onDestroy()
    }

    override fun showNearByCabs(latlngList: List<LatLng>) {
        nearByCabMarkerList.clear()
        for(latlng in latlngList){
            val nearByCarMarker = addCarMarkerandGet(latlng)
            nearByCabMarkerList.add(nearByCarMarker)
        }
    }
}
