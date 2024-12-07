package com.adrians.groupchatturing

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import kotlin.random.Random

//JAVA IMPORTS
import java.net.Socket
import okhttp3.*
import okio.ByteString
import java.util.concurrent.TimeUnit

val TAG = "MyLogTag"

data class Message(
    val id: String = "",
    val senderId: String = "",
    val message: String = "",
    val senderName: String = "")
data class User( //TO DO
    val userId: String = "",
    val userName: String = "",
    val anonName: String = "")

sealed class RepoEvent {
    data class LobbyCreated(val data: Map<String,String>) : RepoEvent()
    data class UserRegistered(val data: String) : RepoEvent()
    data class GameStarted(val data: String) : RepoEvent()
    data object ErrorOccurred : RepoEvent()
}

//TODO FetchX methods when asking a server, GetX when accessing fields
class Repository {
    private var userName : String = ""
    private var anonName : String = ""
    private var userId: Int = 0
    private var roomData: Map<String,Int> = emptyMap()

    private var serverPort = "12345"
    private var serverIp = "192.168.0.94"
    private var webSocketManager = WebSocketManager()

    private var lobbyId = 0

    private var isHost = false;

    private var topic = ""
    private var roundDurationSec = 0
    private var roundNum = 0

    private val _events = MutableSharedFlow<RepoEvent>()
    val events: SharedFlow<RepoEvent> get() = _events

    init
    {
        Log.d(TAG, "Repository init")
        CoroutineScope(Dispatchers.IO).launch {
//            val serverUrl ="ws://${serverIp}:${serverPort}" // WebSocket server URL
//
//            webSocketManager.connect(serverUrl)
//            webSocketManager.sendMessage("Hello, WebSocket!")
//            webSocketManager.closeConnection()
        }
    }

//    fun connectToServer(ip: String, port: Int) {
//        try {
//            Log.d(TAG, "Socktet init")
//            socket = Socket(ip, port)
//            Log.d(TAG, "Socktet doone")
//            val output: OutputStream? = socket.getOutputStream()
//            val input = BufferedReader(InputStreamReader(socket.getInputStream()))
//            if(!socket.isConnected)
//                Log.d(TAG, "Socket fail connection")
//            if(socket.isConnected)
//                pingServer("Test Ping from android to Server", output, input)
//            if(socket.isConnected)
//                socket.close()
//        } catch (e: Exception) {
//            e.message?.let { Log.d(TAG, it) }
//        }
//    }
//
//    fun pingServer(message: String, outputStream: OutputStream?, inputStreamReader: BufferedReader)
//    {
//        outputStream?.write(message.toByteArray())
//        outputStream!!.flush()
//        val response = inputStreamReader.readLine()
//        Log.d(TAG, "Server Response: $response")
//    }

    fun fetchAnonUsername(): String
    {
        return anonName
    }

    fun generateAnonUsername(): String
    {
        val randNum = (Random.nextInt(1000, 9999)).toString()
        anonName = "User$randNum"
        return anonName
    }

    fun setUsername(usrNam: String)
    {
        userName = usrNam
        Log.d(TAG, usrNam)
    }

    fun setServerIp(ip: String)
    {
        serverIp = ip
        Log.d(TAG, ip)
    }

    fun setServerPort(port: String)
    {
        serverPort = port
        Log.d(TAG, port)
    }


    fun fetchUsername(): String
    {
        return userName
    }

    fun setJoinCode(joinCode:String)
    {
        Log.d(TAG, "TODO when I got the JoinCode: $joinCode")
    }

    fun createRoomReq(dataDict: Map<String, Int>) {
        roomData = dataDict
        val serverUrl ="ws://${serverIp}:${serverPort}"

        webSocketManager.connect(serverUrl)
        registerEvents()

        webSocketManager.sendMessage("""{"msgType": 1, "username": "$userName"}""")
    }

    fun startGameReq()
    {
        if(!isHost) return
        webSocketManager.sendMessage("""{"msgType": 16, "clientId": $userId, "lobbyId": $lobbyId}""")
    }

    private fun registerEvents() {
        //Register client resp
        webSocketManager.registerEventListener(2) { json ->
            Log.d(TAG,"Handling event_2: ${json.toString()}")
            userId = json.get("clientId").asInt
            Log.d(TAG, "userId: $userId")
            CoroutineScope(Dispatchers.IO).launch{ _events.emit(RepoEvent.UserRegistered("")) }
            webSocketManager.sendMessage("""{"msgType": 3, "clientId": $userId, "username": "$userName", "maxUsers": ${roomData["maxUsers"]}, "roundsNumber": ${roomData["roundsNumber"]}}""")
        }

        //Create lobby Resp
        webSocketManager.registerEventListener(4) { json ->
            Log.d(TAG,"Handling event_4: ${json.toString()}")
            lobbyId = json.get("lobbyId").asInt
            isHost = true
            //Generate Event
            CoroutineScope(Dispatchers.IO).launch{ _events.emit(RepoEvent.LobbyCreated(mapOf("lobbyId" to lobbyId.toString()))) }
        }

        //Game started
        webSocketManager.registerEventListener(8) { json ->
            Log.d(TAG,"Handling event_8: ${json.toString()}")
            topic = json.get("topic").asString
            roundDurationSec = json.get("roundDurationSec").asInt
            roundNum = json.get("roundNum").asInt
            CoroutineScope(Dispatchers.IO).launch{ _events.emit(RepoEvent.GameStarted("")) }
        }

        //User left event
        webSocketManager.registerEventListener(17) { json ->
            Log.d(TAG,"Handling event_17: ${json.toString()}")
            //if from this lobby then update player list etc
            //TODO
        }
    }

    fun getLobbyId(): Int {
        return lobbyId
    }

    fun getUserName(): String {
        return userName
    }

    fun getIsHost(): Boolean {
        return isHost
    }

//    fun fetchRoomData(): Map<String, String> {
//        return roomData
//    }

}