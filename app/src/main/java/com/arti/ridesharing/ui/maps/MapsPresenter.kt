package com.arti.ridesharing.ui.maps

import android.util.Log
import com.arti.ridesharing.data.NetworkService
import com.arti.ridesharing.utils.Constants
import com.arti.simulator.WebSocket
import com.arti.simulator.WebSocketListener
import com.google.android.gms.maps.model.LatLng
import org.json.JSONArray
import org.json.JSONObject

class MapsPresenter(private val networkService: NetworkService): WebSocketListener {

    companion object{
        private const val TAG = "MapsRepresenter"
    }
    private var view:MapsView? = null
    private lateinit var webSocket: WebSocket

    fun onAttach(view: MapsActivity){
        this.view = view
        webSocket = networkService.createWebSocket(this)
        webSocket.connect()
    }

    fun onDetach(){
        webSocket.disconnect()
        view = null
    }

    override fun onConnect() {
        Log.d(TAG, "Connect")
    }

    private fun handleOnMessageNearByCabs(jsonObject: JSONObject){
        val nearByCabsLocations = arrayListOf<LatLng>()
        val jsonArray = jsonObject.getJSONArray(Constants.LOCATIONS)
        for(i in 0 until jsonArray.length()){
            val lat = (jsonArray.get(i) as JSONObject).getDouble(Constants.LAT)
            val lng = (jsonArray.get(i) as JSONObject).getDouble(Constants.LNG)
            val latLng = LatLng(lat,lng)
            nearByCabsLocations.add(latLng)
        }

        view?.showNearByCabs(nearByCabsLocations)
    }

    fun requestNearByCabs(latLng: LatLng){
        val jsonObject = JSONObject()
        jsonObject.put(Constants.TYPE, Constants.NEAR_BY_CABS)
        jsonObject.put(Constants.LAT, latLng.latitude)
        jsonObject.put(Constants.LNG, latLng.longitude)
        webSocket.sendMessage(jsonObject.toString())
    }

    override fun onMessage(data: String) {
        Log.d(TAG, "Data : $data")
        val jsonObject = JSONObject(data)
        when(jsonObject.getString(Constants.TYPE)){
            Constants.NEAR_BY_CABS ->{
                handleOnMessageNearByCabs(jsonObject)
            }
        }
    }

    override fun onDisconnect() {
        Log.d(TAG, "Disconnect")
    }

    override fun onError(error: String) {
        Log.d(TAG, "Error: $error")
    }

}