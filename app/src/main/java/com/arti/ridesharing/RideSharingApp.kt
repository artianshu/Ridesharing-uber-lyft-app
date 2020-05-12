package com.arti.ridesharing

import android.app.Application
import com.arti.simulator.Simulator
import com.google.android.libraries.places.api.Places
import com.google.maps.GeoApiContext

class RideSharingApp : Application() {

    override fun onCreate() {
        super.onCreate()
        Places.initialize(applicationContext, getString(R.string.google_maps_key));
        Simulator.geoApiContext = GeoApiContext.Builder()
            .apiKey(getString(R.string.google_maps_key))
            .build()
    }

}