package com.adrians.groupchatturing

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    private val repository = Repository()
    private val _usernameData = MutableStateFlow("")
    val usernameData = _usernameData.asStateFlow()
    private val _anonUsernameData = MutableStateFlow("")
    val anonUsernameData = _anonUsernameData.asStateFlow()
    private val _roomData = MutableStateFlow<Map<String, String>>(emptyMap())
    val roomData = _roomData.asStateFlow()
    private val _userId = MutableStateFlow("11")
    val userId = _userId.asStateFlow()

    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages.asStateFlow()
//////////////////////////////////////////////////////////
    private val _uiState = MutableStateFlow(0)
    val uiState = _uiState.asStateFlow()

    private val _lobbyId = MutableStateFlow(0)
    val lobbyId= _lobbyId.asStateFlow()

    private val _userName = MutableStateFlow("")
    val userName = _userName.asStateFlow()

    private val _isHost = MutableStateFlow(false)
    val isHost = _isHost.asStateFlow()

    private val _lobbyUserList = MutableStateFlow<List<String>>(emptyList())
    val lobbyUserList: StateFlow<List<String>> get() = _lobbyUserList

    private val _isGameRunning = MutableStateFlow(false)
    val isGameRunning = _isGameRunning.asStateFlow()


    init {
        viewModelScope.launch {
            repository.events.collect { event ->
                _lobbyId.update { repository.getLobbyId() }
                handleRepositoryEvent(event)
            }
        }
    }

    private fun addItemToLobbyUserList(item: String) {
        viewModelScope.launch {
            val updatedList = _lobbyUserList.value.toMutableList()
            updatedList.add(item)
            _lobbyUserList.emit(updatedList)
        }
    }

    private fun handleRepositoryEvent(event: RepoEvent) {
        when (event) {
            is RepoEvent.ErrorOccurred -> Log.d(TAG, "ERROR occurred")
            is RepoEvent.UserRegistered -> {
                Log.d(TAG, "User Registered")
                _userName.update{repository.getUserName()}
            }
            is RepoEvent.LobbyCreated -> {Log.d(TAG, "Captured lobby event")
                _uiState.update{1}
                _isHost.update{repository.getIsHost()}
                addItemToLobbyUserList(userName.value)
            }
            is RepoEvent.GameStarted -> {
                Log.d(TAG, "Game started")
                _isGameRunning.update{true}
                _uiState.update{2}
            }
        }
    }

    fun getJoinCode(joinCode:String)
    {
        repository.setJoinCode(joinCode)
    }

    fun addMessage(msg: Message) {
        _messages.value += msg
    }

    fun saveUsername(usrNam:String)
    {
        repository.setUsername(usrNam)
    }
    fun getUsername()
    {
        val str = repository.fetchUsername()
        _usernameData.update { str }
    }

    fun saveAnonUsername()
    {
        val str = repository.fetchAnonUsername()
        _anonUsernameData.update { str }
    }

    fun saveRoomData(dataDict: Map<String, Int>) {
        repository.createRoomReq(dataDict)
    }

    fun startGame(){
        repository.startGameReq()
    }

//    fun getRoomData()
//    {
//        val map = repository.fetchRoomData()
//        _roomData.update { map }
//    }

    fun postMessage(messageString: String) {
        //TODO
    }

    fun setServerIpV2R(ip: String) {
        repository.setServerIp(ip)
    }

    fun setServerPortV2R(port: String) {
        repository.setServerPort(port)
    }
}