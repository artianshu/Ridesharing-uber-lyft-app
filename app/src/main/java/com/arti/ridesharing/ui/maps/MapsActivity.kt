package com.arti.ridesharing.ui.maps

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.arti.ridesharing.R
import com.arti.ridesharing.data.NetworkService
import com.arti.ridesharing.utils.ViewUtils
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, MapsView {
    companion object{
        private const val TAG = "MapsActivity"
    }

    private lateinit var mMap: GoogleMap
    private lateinit var presenter: MapsPresenter
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

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
    }
}
