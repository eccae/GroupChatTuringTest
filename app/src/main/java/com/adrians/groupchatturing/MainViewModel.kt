package com.adrians.groupchatturing

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    private val repository = Repository()
//    private val _anonUsernameData = MutableStateFlow("")
//    val anonUsernameData = _anonUsernameData.asStateFlow()
    private val _roomData = MutableStateFlow<Map<String, String>>(emptyMap())
    val roomData = _roomData.asStateFlow()

//    private val _messages = MutableStateFlow<List<Message>>(emptyList())
//    val messages: StateFlow<List<Message>> = _messages.asStateFlow()
//////////////////////////////////////////////////////////
    private val _uiState = MutableStateFlow(0)
    val uiState = _uiState.asStateFlow()

    private val _uiScoreboardState = MutableStateFlow(false)
    val uiScoreboardState = _uiScoreboardState.asStateFlow()

    private val _lobbyId = MutableStateFlow(0)
    val lobbyId= _lobbyId.asStateFlow()

    private val _userName = MutableStateFlow("")
    val userName = _userName.asStateFlow()

    private val _userId = MutableStateFlow(0)
    val userId = _userId.asStateFlow()

    private val _isHost = MutableStateFlow(false)
    val isHost = _isHost.asStateFlow()

    private val _roundDurationSec = MutableStateFlow(0)
    val roundDurationSec = _roundDurationSec.asStateFlow()

    private val _votingTimeSec = MutableStateFlow(0)
    val votingTimeSec = _votingTimeSec.asStateFlow()

    private val _lobbyUserList = MutableStateFlow<List<String>>(emptyList())
    val lobbyUserList: StateFlow<List<String>> get() = _lobbyUserList

    private val _chatMessagesList = MutableStateFlow<List<ChatMsg>>(emptyList())
    val chatMessagesList: StateFlow<List<ChatMsg>> get() = _chatMessagesList
    private val _scoreboardList = MutableStateFlow<List<UserScore>>(emptyList())
    val scoreboardList: StateFlow<List<UserScore>> get() = _scoreboardList

    private val _isGameRunning = MutableStateFlow(false)
    val isGameRunning = _isGameRunning.asStateFlow()

    private val _currentBotNickname = MutableStateFlow("")
    val currentBotNickname = _currentBotNickname.asStateFlow()
    private val _anonUserList = MutableStateFlow<List<AnonUser>>(emptyList())
    val anonUserList: StateFlow<List<AnonUser>> get() = _anonUserList
    private val _gameTopic = MutableStateFlow("")
    val gameTopic = _gameTopic.asStateFlow()

    init {
        viewModelScope.launch {
            repository.events.collect { event ->
                _lobbyId.update { repository.getLobbyId }
                handleRepositoryEvent(event)
            }
        }
    }

    private fun addItemToScoresList(item: UserScore) {
        viewModelScope.launch {
            val updatedList = _scoreboardList.value.toMutableList()
            updatedList.add(item)
            _scoreboardList.emit(updatedList)
        }
    }

    private fun addItemToLobbyUserList(item: String) {
        viewModelScope.launch {
            val updatedList = _lobbyUserList.value.toMutableList()
            updatedList.add(item)
            _lobbyUserList.emit(updatedList)
        }
    }

    private fun addItemToChatMessagesList(item: ChatMsg) {
        viewModelScope.launch {
            val updatedList = _chatMessagesList.value.toMutableList()
            updatedList.add(item)
            _chatMessagesList.emit(updatedList)
        }
    }

    private fun addItemToAnonsList(anon: AnonUser) {
        viewModelScope.launch {
            val updatedList = _anonUserList.value.toMutableList()
            updatedList.add(anon)
            _anonUserList.emit(updatedList)
        }
    }

    private fun startCountingCoroutine(num: MutableStateFlow<Int> )
    {
        CoroutineScope(Dispatchers.Default).launch {
            while (num.value > 0) {
                num.update{num.value - 1}
                delay(1000L) // Pause for 1 second
            }
        }
    }

    private fun handleRepositoryEvent(event: RepoEvent) {
        when (event) {
            is RepoEvent.ErrorOccurred -> Log.d(TAG, "ERROR occurred")
            is RepoEvent.UserRegistered -> {
                Log.d(TAG, "User Registered")
                _userName.update{repository.getUserName}
                _userId.update { repository.getUserId }
            }
            is RepoEvent.LobbyCreated -> {
                Log.d(TAG, "Captured lobby event")
                _uiState.update{1}
                _isHost.update{repository.getIsHost}
                addItemToLobbyUserList(userName.value)
            }
            is RepoEvent.GameStarted -> {
                Log.d(TAG, "Game started")
                _isGameRunning.update{true}
                _roundDurationSec.update{repository.getRoundDurationSec}
                _gameTopic.update { repository.getTopic }
                _uiState.update{2}
                startCountingCoroutine(_roundDurationSec)

            }
            is RepoEvent.GameEnded -> {
                Log.d(TAG, "Game ended, return to menu")
                _uiState.update{0}
            }
            is RepoEvent.JoinedLobby -> {
                Log.d(TAG, "Joined lobby as player")
                _uiState.update{1}
                _isHost.update{repository.getIsHost}
                val allUsers = repository.getUserList
                allUsers.forEach { user ->  addItemToLobbyUserList(user)}
            }
            is RepoEvent.NewChatMessage -> {
                Log.d(TAG, "New chat message")
                val allMessages = repository.getChatMessages
                allMessages.forEach { msg ->  addItemToChatMessagesList(msg)}
            }
            is RepoEvent.NewRound -> {
                Log.d(TAG, "New Round")
                _roundDurationSec.update{repository.getRoundDurationSec}
                _uiScoreboardState.update { false }
                _gameTopic.update { repository.getTopic }
                _uiState.update{2}
                startCountingCoroutine(_roundDurationSec)
            }
            is RepoEvent.NewUserJoined -> {
                Log.d(TAG, "New User")
                val allUsers = repository.getUserList
                allUsers.forEach { user ->  addItemToLobbyUserList(user)}
            }
            is RepoEvent.RoundEnded -> {
                Log.d(TAG, "End of round after vote, display scoreboard")
                val scores = repository.getScoreboardList
                scores.forEach { score ->  addItemToScoresList(score)}
                _uiScoreboardState.update { true }
                _currentBotNickname.update{repository.getCurrentBotNickname}
                //display summary
            }
            is RepoEvent.TimeToVote -> {
                Log.d(TAG, "Chat ended, vote screen")
                _votingTimeSec.update{repository.getVotingTimeSec}
                repository.getAnonUserList.forEach {anon -> addItemToAnonsList(anon)}
                _uiState.update{3}
                startCountingCoroutine(_votingTimeSec)
            }
        }
    }

//    fun getJoinCode(joinCode:String)
//    {
//        repository.setJoinCode(joinCode)
//    }

//    fun addMessage(msg: Message) {
//        _messages.value += msg
//    }

    fun saveUsername(usrNam:String)
    {
        repository.setUsername(usrNam)
    }

//    fun saveAnonUsername()
//    {
//        val str = repository.fetchAnonUsername()
//        _anonUsernameData.update { str }
//    }

    fun saveRoomData(dataDict: MutableMap<String, Int>) {
        repository.sendCreateRoomReq(dataDict)
    }

    fun startGame(){
        repository.sendStartGameReq()
    }

    fun joinLobby(lobbyId : Int){
        repository.sendJoinRoomReq(lobbyId)
    }
//    fun getRoomData()
//    {
//        val map = repository.fetchRoomData()
//        _roomData.update { map }
//    }

    fun postMessage(messageString: String) {
        repository.sendPostNewMessageReq(messageString)
    }

    fun setServerIpV2R(ip: String) {
        repository.setServerIp(ip)
    }

    fun setServerPortV2R(port: String) {
        repository.setServerPort(port)
    }

    fun vote(userNickname: String) {
        repository.sendVoteResp(userNickname)
    }
}