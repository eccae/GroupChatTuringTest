package com.adrians.groupchatturing

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.random.Random

//JAVA IMPORTS
import java.net.Socket
import java.io.BufferedReader
import java.io.InputStreamReader

private val TAG = "MyAppTag"

data class Message(
    val id: String = "",
    val senderId: String = "",
    val message: String = "",
    val senderName: String = "")

class Repository {
    private var userName : String = ""
    private var anonName : String = ""
    private var userId: String = "11"
    private var roomData: Map<String,String> = emptyMap()

    private var socket = Socket()

    init
    {
        Log.d(TAG, "Repository init")
        CoroutineScope(Dispatchers.IO).launch {
            connectToServer("127.0.0.1", 12345)
        }
    }

    private fun connectToServer(ip: String, port: Int) {
        try {
            socket = Socket(ip, port)

            val output: java.io.OutputStream? = socket.getOutputStream()
            val input = BufferedReader(InputStreamReader(socket.getInputStream()))
            if(socket.isConnected)
                pingServer("Test Ping from android to Server", output, input)
            if(socket.isConnected)
                socket.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun pingServer(message: String, outputStream: java.io.OutputStream?, inputStreamReader: BufferedReader)
    {
        outputStream?.write(message.toByteArray())
        outputStream!!.flush()
        val response = inputStreamReader.readLine()
        Log.d(TAG, "Server Response: $response")
    }

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

    fun fetchUsername(): String
    {
        return userName
    }

    fun setJoinCode(joinCode:String)
    {
        Log.d(TAG, "TODO when I got the JoinCode: $joinCode")
    }

    fun createRoomReq(dataDict: Map<String, String>) {
        roomData = dataDict
    }

    fun fetchRoomData(): Map<String, String> {
        return roomData
    }

}