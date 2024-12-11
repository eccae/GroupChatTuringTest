package com.adrians.groupchatturing

import android.util.Log
import com.google.gson.JsonArray
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

const val TAG = "MyLogTag"

data class ChatMsg(
    val message: String = "",
    val senderNickname: String = "",
    val senderId: Int = 0)
data class UserScore(
    val username: String = "",
    val points: Int = 0)
data class AnonUser(
    val userId: Int = 0,
    val nickName: String = "")


//TODO replace class with object when no data needed
sealed class RepoEvent {
    data class LobbyCreated(val data: Map<String,String>) : RepoEvent()
    data class UserRegistered(val data: String) : RepoEvent()
    data class GameStarted(val data: String) : RepoEvent()
    data class GameEnded(val data: String) : RepoEvent()
    data class RoundEnded(val data: String) : RepoEvent()
    data class TimeToVote(val data: String) : RepoEvent()
    data class NewChatMessage(val data: String) : RepoEvent()
    data class NewRound(val data: String) : RepoEvent()
    data class NewUserJoined(val data: String) : RepoEvent()
    data class JoinedLobby(val data: String) : RepoEvent()
    data class ErrorOccurred(val data: String) : RepoEvent()
}

//INFO
//Naming convention - no  getter functions, when sending to server: sendXReq, setting fields: setX
class Repository {
    private var serverPort = "12345"
    private var serverIp = "192.168.0.94"
    private var webSocketManager = WebSocketManager()

    private val roomData: MutableMap<String,Int> by lazy {
        mutableMapOf()
    }

    private var lobbyIdFromUserInput = 0 //Holds lobby Id provided by user in join lobby operation

    private var userName : String = ""
    val getUserName: String
        get() = userName
    private var lobbyId = 0
    val getLobbyId: Int
        get() = lobbyId
    private var userId: Int = 0
    val getUserId: Int
        get() = userId
    private val userList: MutableList<String> by lazy {
        mutableListOf()
    }
    val getUserList: MutableList<String>
        get() = userList
    private val chatMessages: MutableList<ChatMsg> by lazy {
        mutableListOf()
    }
    val getChatMessages: MutableList<ChatMsg>
        get() = chatMessages
    private val scoreboardList: MutableList<UserScore> by lazy {
        mutableListOf()
    }
    val getScoreboardList: MutableList<UserScore>
        get() = scoreboardList
    private val anonUserList: MutableList<AnonUser> by lazy {
        mutableListOf()
    }
    val getAnonUserList: MutableList<AnonUser>
        get() = anonUserList
    private var isHost = false
    val getIsHost: Boolean
        get() = isHost
    private var topic = ""
    val getTopic: String
        get() = topic
    private var roundDurationSec = 0
    val getRoundDurationSec: Int
        get() = roundDurationSec
    private var roundNum = 0
    val getRoundNum: Int
        get() = roundNum
    private var votingTimeSec = 0
    val getVotingTimeSec: Int
        get() = votingTimeSec
    private var currentBotNickname = ""
    val getCurrentBotNickname: String
        get() = currentBotNickname

    private val _events = MutableSharedFlow<RepoEvent>(extraBufferCapacity = 5)
    val events: SharedFlow<RepoEvent> get() = _events

    init
    {
        Log.d(TAG, "Repository init")
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

    private fun startConnection()
    {
        val serverUrl ="ws://${serverIp}:${serverPort}"
        webSocketManager.closeConnection()
        webSocketManager.connect(serverUrl)
        registerEvents()
    }

    fun sendCreateRoomReq(dataDict: MutableMap<String, Int>) {
        roomData["roundsNumber"] = dataDict["roundsNumber"] ?: 1
        roomData["maxUsers"] = dataDict["maxUsers"] ?: 2
        isHost = true
        startConnection()
        Log.d(TAG, "Sending sendCreateRoomReq")
        webSocketManager.sendMessage("""{"msgType": 1, "username": "$userName"}""")
    }

    fun sendJoinRoomReq(lobbyId : Int) {
        lobbyIdFromUserInput = lobbyId
        isHost = false
        startConnection()
        Log.d(TAG, "Sending sendJoinRoomReq")
        webSocketManager.sendMessage("""{"msgType": 1, "username": "$userName"}""")
    }

    fun sendPostNewMessageReq(chatMessage : String)
    {
        webSocketManager.sendMessage("""{"msgType": 10, "clientId": $userId, "lobbyId": $lobbyId, "chatMsg": "$chatMessage"}""")
    }

    fun sendVoteResp(votedNickname : String)
    {
        webSocketManager.sendMessage("""{"msgType": 13, "clientId": $userId, "lobbyId": $lobbyId, "chatbotNickname": "$votedNickname"}""")
    }

    fun sendStartGameReq()
    {
        if(!isHost) return
        Log.d(TAG, "Sending sendStartGameReq")
        webSocketManager.sendMessage("""{"msgType": 16, "clientId": $userId, "lobbyId": $lobbyId}""")
    }

    private fun registerEvents() {
        //Error
        webSocketManager.registerEventListener(-1) { json ->
            Log.d(TAG, "ERROR: Handling event_-1: $json")
            CoroutineScope(Dispatchers.IO).launch { _events.emit(RepoEvent.ErrorOccurred("CRITICAL"))}
        }

        //Register client resp - Has two routes (depends if is host [set locally])
        webSocketManager.registerEventListener(2) { json ->
            Log.d(TAG,"Handling event_2: $json")
            userId = json.get("clientId").asInt
            Log.d(TAG, "userId: $userId")
            CoroutineScope(Dispatchers.IO).launch{ _events.emit(RepoEvent.UserRegistered("")) }
            if(isHost)
                webSocketManager.sendMessage("""{"msgType": 3, "clientId": $userId, "username": "$userName", "maxUsers": ${roomData["maxUsers"]}, "roundsNumber": ${roomData["roundsNumber"]}}""")
            else
                webSocketManager.sendMessage("""{"msgType": 5, "clientId": $userId, "lobbyId": $lobbyIdFromUserInput}""")
        }

        //Create lobby Resp
        webSocketManager.registerEventListener(4) { json ->
            Log.d(TAG,"Handling event_4: $json")
            userList.clear()
            lobbyId = json.get("lobbyId").asInt
            isHost = true
            CoroutineScope(Dispatchers.IO).launch{ _events.emit(RepoEvent.LobbyCreated(mapOf("lobbyId" to lobbyId.toString()))) }
        }

        //Join lobby Resp
        webSocketManager.registerEventListener(6) { json ->
            Log.d(TAG,"Handling event_6: $json")
            lobbyId = json.get("lobbyId").asInt
            isHost = false
            Log.d(TAG,"Handling event_6 2: $json")
            userList.clear()
            userList.addAll(json.getAsJsonArray("userList").map { it.asString })
            Log.d(TAG,"Handling event_6 4: $json") //lateinit property roomData has not been initialized
            roomData["maxUsers"] = json.get("maxUsers").asInt
            roomData["roundsNumber"] = json.get("roundsNumber").asInt
            Log.d(TAG,"Handling event_6 5: $json")
            //Unused field lobbyCreator: "lobbyCreator": "Jane Doe"
            CoroutineScope(Dispatchers.IO).launch { _events.emit(RepoEvent.JoinedLobby("")) }
        }

        //New user joined Info Message - Specific lobby
        webSocketManager.registerEventListener(7) { json ->
            Log.d(TAG,"Handling event_7: $json")
            if(lobbyId == json.get("lobbyId").asInt) {
                val newUser = json.get("newUser").asString
                userList.add(newUser)
                CoroutineScope(Dispatchers.IO).launch { _events.emit(RepoEvent.NewUserJoined("")) }
            }
        }

        //Game started
        webSocketManager.registerEventListener(8) { json ->
            Log.d(TAG,"Handling event_8: $json")
//            topic = json.get("topic").asString
//            roundDurationSec = json.get("roundDurationSec").asInt
//            roundNum = json.get("roundNum").asInt
            CoroutineScope(Dispatchers.IO).launch{ _events.emit(RepoEvent.GameStarted("")) }
        }

        //New round msg - Specific lobby
        webSocketManager.registerEventListener(9) { json ->
            Log.d(TAG,"Handling event_9: $json")
            if(lobbyId == json.get("lobbyId").asInt) {
                chatMessages.clear()
                topic = json.get("topic").asString
                roundDurationSec = json.get("roundDurationSec").asInt
                roundNum = json.get("roundNum").asInt
                CoroutineScope(Dispatchers.IO).launch { _events.emit(RepoEvent.NewRound("")) }
            }
        }

        //New chat message - Specific lobby
        webSocketManager.registerEventListener(11) { json ->
            Log.d(TAG,"Handling event_11: $json")
            if(lobbyId == json.get("lobbyId").asInt) {
                Log.d(TAG,"Handling event_6 1: $json") //Error parsing message: lateinit property chatMessages has not been initialized
                chatMessages.add(ChatMsg(json.get("chatMsg").asString,json.get("senderNickname").asString,json.get("senderId").asInt))
                Log.d(TAG,"Handling event_6 2: $json")
                CoroutineScope(Dispatchers.IO).launch { _events.emit(RepoEvent.NewChatMessage("")) }
            }
        }

        //Order to start voting - Specific lobby
        webSocketManager.registerEventListener(12) { json ->
            Log.d(TAG,"Handling event_12: $json")
            if(lobbyId == json.get("lobbyId").asInt) {
                votingTimeSec = json.get("votingTimeSec").asInt
                anonUserList.clear()
                anonUserList.addAll(json.getAsJsonObject("usersNicknames").entrySet().map { entry ->
                    AnonUser(userId = entry.key.toInt(), nickName = entry.value.asString) })
                CoroutineScope(Dispatchers.IO).launch { _events.emit(RepoEvent.TimeToVote("")) }
            }
        }

        //Round ended - Specific lobby. Contains the current scoreboard
        webSocketManager.registerEventListener(14) { json ->
            Log.d(TAG,"Handling event_14: $json")
            if(lobbyId == json.get("lobbyId").asInt) {
                scoreboardList.clear()
                scoreboardList.addAll(json.getAsJsonObject("scoreboard").entrySet().map { entry ->
                    UserScore(username = entry.key, points = entry.value.asInt) })
                currentBotNickname = json.get("chatbotNickname").asString
                CoroutineScope(Dispatchers.IO).launch { _events.emit(RepoEvent.RoundEnded("")) }
            }
        }

        //Game ended - Specific lobby. Close the websocket
        webSocketManager.registerEventListener(15) { json ->
            Log.d(TAG,"Handling event_15: $json")
            if(lobbyId == json.get("lobbyId").asInt) {
                webSocketManager.unregisterAllEventListeners()
                webSocketManager.closeConnection()
                CoroutineScope(Dispatchers.IO).launch { _events.emit(RepoEvent.GameEnded("")) }
            }
        }

        //User left event
        webSocketManager.registerEventListener(17) { json ->
            Log.d(TAG,"Handling event_17: $json")
            if(lobbyId == json.get("lobbyId").asInt) {
                userList.remove(json.get("newUser").asString)
                CoroutineScope(Dispatchers.IO).launch { _events.emit(RepoEvent.NewUserJoined("")) }
            }
        }

        //Lobby is forcefully shutdown
        webSocketManager.registerEventListener(18) { json ->
            Log.d(TAG,"Handling event_18: $json")
            if(lobbyId == json.get("lobbyId").asInt) {
                CoroutineScope(Dispatchers.IO).launch { _events.emit(RepoEvent.ErrorOccurred("CRITICAL")) }
            }
        }
    }
}