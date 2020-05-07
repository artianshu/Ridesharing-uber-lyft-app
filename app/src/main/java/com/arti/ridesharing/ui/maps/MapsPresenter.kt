package com.arti.ridesharing.ui.maps

import android.util.Log
import com.arti.ridesharing.data.NetworkService
import com.arti.simulator.WebSocket
import com.arti.simulator.WebSocketListener

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

    override fun onMessage(data: String) {
        Log.d(TAG, "Data : $data")
    }

    override fun onDisconnect() {
        Log.d(TAG, "Disconnect")
    }

    override fun onError(error: String) {
        Log.d(TAG, "Error: $error")
    }

}