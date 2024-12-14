package com.adrians.groupchatturing

import android.util.Log
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import okhttp3.*
import okio.ByteString
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit

class WebSocketManager {

    private val client = OkHttpClient.Builder()
        .readTimeout(0, TimeUnit.MILLISECONDS)
        .build()

    private lateinit var webSocket: WebSocket
    private var isConnected: Boolean = false

    private val eventListeners = ConcurrentHashMap<Int, (JsonObject) -> Unit>()

    fun connect(serverUrl: String) {
        val request = Request.Builder()
            .url(serverUrl)
            .build()
        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                Log.d(TAG,"Connected to WebSocket")
                isConnected = true
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                Log.d(TAG,"Received message: $text")
                handleIncomingMessage(text)
            }

            override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
                Log.d(TAG,"Received binary message: ${bytes.hex()}")
            }

            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                Log.d(TAG,"Closing WebSocket: $code / $reason")
                webSocket.close(code, reason)
                isConnected = false
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                Log.d(TAG,"WebSocket closed: $code / $reason")
                isConnected = false
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                Log.d(TAG,"WebSocket error: ${t.message}")
                isConnected = false
//              eventListeners[-1]?.invoke(JsonParser.parseString("WebSocket error: ${t.message}").asJsonObject)
                unregisterAllEventListeners()
            }
        })
    }

    private fun handleIncomingMessage(message: String) {
        try {
            val json = JsonParser.parseString(message).asJsonObject
            val msgType = json.get("msgType")?.asInt
            if (msgType != null && eventListeners.containsKey(msgType)) {
                eventListeners[msgType]?.invoke(json)
            } else {
                println("No listener for id: $msgType")
            }
        } catch (e: Exception) {
            println("Error parsing message: ${e.message}")
        }
    }

    fun sendMessage(jsonMessage: String) {
        if (::webSocket.isInitialized) {
            webSocket.send(jsonMessage)
        } else {
            println("WebSocket is not connected.")
        }
    }

    fun registerEventListener(msgType: Int, action: (JsonObject) -> Unit) {
        eventListeners[msgType] = action
    }

    fun unregisterEventListener(msgType: Int) {
        eventListeners.remove(msgType)
    }

    fun unregisterAllEventListeners() {
        eventListeners.clear()
    }

    fun isConnected(): Boolean
    {
        return isConnected
    }

    fun closeConnection() {
        if (::webSocket.isInitialized) {
//            eventListeners[-1]?.invoke(json)
            webSocket.close(1000, "Client closing connection")
            isConnected = false
        }
    }
}
