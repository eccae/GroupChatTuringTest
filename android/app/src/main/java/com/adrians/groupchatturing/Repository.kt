package com.adrians.groupchatturing

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
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

sealed class RepoEvent {
    data object LobbyCreated: RepoEvent()
    data object UserRegistered : RepoEvent()
    data object GameStarted : RepoEvent()
    data object GameEnded : RepoEvent()
    data object RoundEnded : RepoEvent()
    data object TimeToVote : RepoEvent()
    data object NewChatMessage : RepoEvent()
    data object NewRound : RepoEvent()
    data object NewUserJoined : RepoEvent()
    data object JoinedLobby : RepoEvent()
    data class ErrorOccurred(val data: String) : RepoEvent()
}

//INFO
//Naming convention - no  getter functions, when sending to server: sendXReq, setting fields: setX
class Repository {
    private var webSocketManager = WebSocketManager()

    private var serverPort = "12345"
    val getServerPort: String
        get() = serverPort
    private var serverIp = "192.168.0.94"
    val getServerIp: String
        get() = serverIp
    private var serverPrefix = "ws://"
    val getServerPrefix: String
        get() = serverPrefix

    private val roomData: MutableMap<String,Int> by lazy {
        mutableMapOf()
    }
    val getRoomData: MutableMap<String,Int>
        get() = roomData

    //Holds lobby Id provided by user in join lobby operation
    private var lobbyIdFromUserInput = 0

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

    fun setServerPrefix(prefix: String) {
        serverPrefix = prefix
        Log.d(TAG, prefix)
    }

    private fun startConnection()
    {
        val serverUrl ="${serverPrefix}${serverIp}:${serverPort}"
        try {
            webSocketManager.closeConnection()
            registerEvents()
            webSocketManager.connect(serverUrl)
        } catch (e: Exception) {
            Log.d(TAG, "Connection error to ${serverIp}:${serverPort}")
        }
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
        if(webSocketManager.isConnected())
            webSocketManager.sendMessage("""{"msgType": 10, "clientId": $userId, "lobbyId": $lobbyId, "chatMsg": "$chatMessage"}""")
    }

    fun sendVoteResp(votedNickname : String)
    {
        if(webSocketManager.isConnected())
            webSocketManager.sendMessage("""{"msgType": 13, "clientId": $userId, "lobbyId": $lobbyId, "chatbotNickname": "$votedNickname"}""")
    }

    fun sendStartGameReq()
    {
        if(!isHost || !webSocketManager.isConnected()) return
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
            CoroutineScope(Dispatchers.IO).launch{ _events.emit(RepoEvent.UserRegistered) }
            if(isHost)
                webSocketManager.sendMessage("""{"msgType": 3, "clientId": $userId, "username": "$userName", "maxUsers": ${roomData["maxUsers"]}, "roundsNumber": ${roomData["roundsNumber"]}}""")
            else
                webSocketManager.sendMessage("""{"msgType": 5, "clientId": $userId, "lobbyId": $lobbyIdFromUserInput}""")
        }

        //Create lobby Resp
        webSocketManager.registerEventListener(4) { json ->
            Log.d(TAG,"Handling event_4: $json")
            userList.clear()
            userList.add(userName)
            lobbyId = json.get("lobbyId").asInt
            isHost = true
            CoroutineScope(Dispatchers.IO).launch{ _events.emit(RepoEvent.LobbyCreated)}
        }

        //Join lobby Resp
        webSocketManager.registerEventListener(6) { json ->
            Log.d(TAG,"Handling event_6: $json")
            lobbyId = json.get("lobbyId").asInt
            isHost = false
            userList.clear()
            userList.addAll(json.getAsJsonArray("userList").map { it.asString })
            roomData["maxUsers"] = json.get("maxUsers").asInt
            roomData["roundsNumber"] = json.get("roundsNumber").asInt
            //Unused field lobbyCreator: "lobbyCreator": "Jane Doe"
            CoroutineScope(Dispatchers.IO).launch { _events.emit(RepoEvent.JoinedLobby) }
        }

        //New user joined Info Message - Specific lobby
        webSocketManager.registerEventListener(7) { json ->
            Log.d(TAG,"Handling event_7: $json")
            if(lobbyId == json.get("lobbyId").asInt) {
                val newUser = json.get("newUser").asString
                userList.add(newUser)
                CoroutineScope(Dispatchers.IO).launch { _events.emit(RepoEvent.NewUserJoined) }
            }
        }

        //Game started
        webSocketManager.registerEventListener(8) { json ->
            Log.d(TAG,"Handling event_8: $json")
//            topic = json.get("topic").asString
//            roundDurationSec = json.get("roundDurationSec").asInt
//            roundNum = json.get("roundNum").asInt
            CoroutineScope(Dispatchers.IO).launch{ _events.emit(RepoEvent.GameStarted) }
        }

        //New round msg - Specific lobby
        webSocketManager.registerEventListener(9) { json ->
            Log.d(TAG,"Handling event_9: $json")
            if(lobbyId == json.get("lobbyId").asInt) {
                chatMessages.clear()
                topic = json.get("topic").asString
                roundDurationSec = json.get("roundDurationSec").asInt
                roundNum = json.get("roundNum").asInt
                CoroutineScope(Dispatchers.IO).launch { _events.emit(RepoEvent.NewRound) }
            }
        }

        //New chat message - Specific lobby
        webSocketManager.registerEventListener(11) { json ->
            Log.d(TAG,"Handling event_11: $json")
            if(lobbyId == json.get("lobbyId").asInt) {
                chatMessages.add(ChatMsg(json.get("chatMsg").asString,json.get("senderNickname").asString,json.get("senderId").asInt))
                CoroutineScope(Dispatchers.IO).launch { _events.emit(RepoEvent.NewChatMessage) }
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
                CoroutineScope(Dispatchers.IO).launch { _events.emit(RepoEvent.TimeToVote) }
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
                CoroutineScope(Dispatchers.IO).launch { _events.emit(RepoEvent.RoundEnded) }
            }
        }

        //Game ended - Specific lobby. Close the websocket
        webSocketManager.registerEventListener(15) { json ->
            Log.d(TAG,"Handling event_15: $json")
            if(lobbyId == json.get("lobbyId").asInt) {
                webSocketManager.unregisterAllEventListeners()
                webSocketManager.closeConnection()
                CoroutineScope(Dispatchers.IO).launch { _events.emit(RepoEvent.GameEnded) }
            }
        }

        //User left event
        webSocketManager.registerEventListener(17) { json ->
            Log.d(TAG,"Handling event_17: $json")
            if(lobbyId == json.get("lobbyId").asInt) {
                userList.remove(json.get("newUser").asString)
                CoroutineScope(Dispatchers.IO).launch { _events.emit(RepoEvent.NewUserJoined) }
            }
        }

        //Lobby is forcefully shutdown
        webSocketManager.registerEventListener(18) { json ->
            Log.d(TAG,"Handling event_18: $json")
            if(lobbyId == json.get("lobbyId").asInt) {
                CoroutineScope(Dispatchers.IO).launch { _events.emit(RepoEvent.ErrorOccurred("CRITICAL")) }
            }
        }

        //WebSocket lost connection or other error
        webSocketManager.registerEventListener(-2) { json ->
            Log.d(TAG,"Handling error_-2: $json")
            CoroutineScope(Dispatchers.IO).launch { _events.emit(RepoEvent.ErrorOccurred("CRITICAL"))}
        }
    }
}