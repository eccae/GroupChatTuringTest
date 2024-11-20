package com.adrians.groupchatturing

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class MainViewModel : ViewModel() {
    private val repository = Repository()
    private val _usernameData = MutableStateFlow("")
    val usernameData = _usernameData.asStateFlow()
    private val _anonUsernameData = MutableStateFlow("")
    val anonUsernameData = _anonUsernameData.asStateFlow()
    private val _roomData = MutableStateFlow<Map<String, String>>(emptyMap())
    val roomData = _roomData.asStateFlow()

    fun getJoinCode(joinCode:String)
    {
        repository.setJoinCode(joinCode)
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

    fun saveRoomData(dataDict: Map<String, String>) {
        repository.createRoomReq(dataDict)
    }

    fun getRoomData()
    {
        val map = repository.fetchRoomData()
        _roomData.update { map }
    }
}