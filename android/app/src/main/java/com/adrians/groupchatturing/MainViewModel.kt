package com.adrians.groupchatturing

import android.util.Log
import androidx.compose.ui.graphics.Color
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
import kotlin.random.Random

class MainViewModel : ViewModel() {
    private val repository = Repository()

    private val _roomData = MutableStateFlow<Map<String, Int>>(emptyMap())
    val roomData = _roomData.asStateFlow()

    private val _uiState = MutableStateFlow(0)
    val uiState = _uiState.asStateFlow()

    private val _uiScoreboardState = MutableStateFlow(false)
    val uiScoreboardState = _uiScoreboardState.asStateFlow()

    private val _lobbyId = MutableStateFlow(0)
    val lobbyId= _lobbyId.asStateFlow()

    private val _userName = MutableStateFlow("User${Random.nextInt(100, 1000)}")
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

    private val _currentBotNickname = MutableStateFlow("")
    val currentBotNickname = _currentBotNickname.asStateFlow()
    private val _anonUserSet = MutableStateFlow<Set<AnonUser>>(emptySet())
    val anonUserSet: StateFlow<Set<AnonUser>> get() = _anonUserSet
    private val _gameTopic = MutableStateFlow("")
    val gameTopic = _gameTopic.asStateFlow()

    private val _nickNameColorList = MutableStateFlow<Map<String, Color>>(emptyMap())
    val nickNameColorList: StateFlow<Map<String, Color>> get() = _nickNameColorList

    private val _roundNumber = MutableStateFlow(0)
    val roundNumber = _roundNumber.asStateFlow()

    private val _serverIpV2R = MutableStateFlow("")
    val serverIpV2R = _serverIpV2R.asStateFlow()
    private val _serverPortV2R = MutableStateFlow("")
    val serverPortV2R = _serverPortV2R.asStateFlow()
    private val _serverPrefix = MutableStateFlow("")
    val serverPrefix = _serverPrefix.asStateFlow()

    init {
        viewModelScope.launch {
            repository.events.collect { event ->
                _lobbyId.update { repository.getLobbyId }
                _serverIpV2R.update { repository.getServerIp}
                _serverPortV2R.update { repository.getServerPort }
                _serverPrefix.update { repository.getServerPrefix }
                handleRepositoryEvent(event)
            }
        }
    }

    private fun addElementToAnonSet(anon: AnonUser) {
        viewModelScope.launch {
            val updatedSet = _anonUserSet.value.toMutableSet()
            updatedSet.add(anon)
            _anonUserSet.emit(updatedSet)
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
            is RepoEvent.ErrorOccurred -> {
                Log.d(TAG, "ERROR occurred ${event.data ?: ""}")
                    _uiState.update{0}
            }
            is RepoEvent.UserRegistered -> {
                Log.d(TAG, "User Registered")
                _userName.update{repository.getUserName}
                _userId.update { repository.getUserId }
            }
            is RepoEvent.LobbyCreated -> {
                Log.d(TAG, "Captured lobby event")
                _uiState.update{1}
                _isHost.update{repository.getIsHost}
                _lobbyUserList.update { emptyList() }
                viewModelScope.launch {
                    _lobbyUserList.emit(listOf(userName.value))
                }
            }
            is RepoEvent.GameStarted -> {
                Log.d(TAG, "Game started")
                _roundNumber.update { 1 }
            }
            is RepoEvent.GameEnded -> {
                Log.d(TAG, "Game ended, return to menu")
                _uiState.update{0}
            }
            is RepoEvent.JoinedLobby -> {
                Log.d(TAG, "Joined lobby as player")
                _uiState.update{1}
                _isHost.update{repository.getIsHost}
                _roomData.update { repository.getRoomData }
                _lobbyUserList.update { emptyList() }
                viewModelScope.launch {
                    val updatedList = repository.getUserList.toList()
                    _lobbyUserList.emit(updatedList)
                }
            }
            is RepoEvent.NewChatMessage -> {
                Log.d(TAG, "New chat message")
                _chatMessagesList.update { emptyList() }
                //TODO - Would be nice to add messages one by one instead of slapping full list every time
                viewModelScope.launch {
                    val updatedList = repository.getChatMessages.toList()
                    _chatMessagesList.emit(updatedList)
                }
            }
            is RepoEvent.NewRound -> {
                Log.d(TAG, "New Round")
                _roundDurationSec.update{repository.getRoundDurationSec}
                _uiScoreboardState.update { false }
                _gameTopic.update { repository.getTopic }
                _chatMessagesList.update { emptyList() }
                _uiState.update{2}
                _roundNumber.update { repository.getRoundNum }
                _nickNameColorList.value = repository.getNicknameColorsDisc
                startCountingCoroutine(_roundDurationSec)
            }
            is RepoEvent.NewUserJoined -> {
                Log.d(TAG, "New User")
                val allUsers = repository.getUserList
                _lobbyUserList.update { emptyList() }
                viewModelScope.launch {
                    val updatedList = repository.getUserList.toList()
                    _lobbyUserList.emit(updatedList)
                }
            }
            is RepoEvent.RoundEnded -> {
                Log.d(TAG, "End of round after vote, display scoreboard")
                _scoreboardList.update { emptyList() }
                viewModelScope.launch {
                    val updatedList = repository.getScoreboardList.toList()
                    _scoreboardList.emit(updatedList)
                }
                _uiScoreboardState.update { true }
                _nickNameColorList.update { emptyMap() }
                _currentBotNickname.update{repository.getCurrentBotNickname}
            }
            is RepoEvent.TimeToVote -> {
                Log.d(TAG, "Chat ended, vote screen")
                _votingTimeSec.update{repository.getVotingTimeSec}
                _anonUserSet.update { emptySet() }
                //TODO - If unexpected crashes occur, this may be the reason but this should never happen as it would require to two "Start Vote" messages arrive in short time.
                repository.getAnonUserList.forEach {anon -> addElementToAnonSet(anon)}
                _uiState.update{3}
                startCountingCoroutine(_votingTimeSec)
            }
        }
    }

    fun setUsername(usrNam:String)
    {
        repository.setUsername(usrNam)
        _userName.update { repository.getUserName }
    }

    fun setRoomDataAndCreateRoom(dataDict: MutableMap<String, Int>) {
        if(repository.getUserName == "")
        {
            repository.setUsername(_userName.value)
        }
        _roomData.update { dataDict }
        repository.sendCreateRoomReq(dataDict)
    }

    fun startGame(){
        repository.sendStartGameReq()
    }

    fun joinLobby(lobbyId : Int){
        if(repository.getUserName == "")
        {
            repository.setUsername(_userName.value)
        }
        repository.sendJoinRoomReq(lobbyId)
    }

    fun postMessage(messageString: String) {
        if(_roundDurationSec.value > 0 && messageString.isNotEmpty())
            repository.sendPostNewMessageReq(messageString)
    }

    fun setServerIpV2R(ip: String) {
        repository.setServerIp(ip)
        _serverIpV2R.update { repository.getServerIp }
    }

    fun setServerPortV2R(port: String) {
        repository.setServerPort(port)
        _serverPortV2R.update { repository.getServerPort }
    }

    fun setServerPrefix(prefix: String) {
        repository.setServerPrefix(prefix)
        _serverPrefix.update { repository.getServerPrefix }
    }

    fun vote(userNickname: String) {
        repository.sendVoteResp(userNickname)
    }

    fun pullCurrentServerAddress() {
        _serverIpV2R.update { repository.getServerIp}
        _serverPortV2R.update { repository.getServerPort }
        _serverPrefix.update { repository.getServerPrefix }
    }

    fun debugForceView(view: Int) {
        _uiState.update { view }
    }
}