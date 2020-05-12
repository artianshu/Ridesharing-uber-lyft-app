package com.arti.ridesharing.data.network

import com.arti.simulator.WebSocket
import com.arti.simulator.WebSocketListener

class NetworkService {

    fun createWebSocket(webSocketListener: WebSocketListener): WebSocket {
        return WebSocket(webSocketListener)
    }

}