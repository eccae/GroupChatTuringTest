package com.adrians.groupchatturing

import android.util.Log
import kotlin.random.Random

class Repository {
    private var userName : String = ""
    private var anonName : String = ""
    private var userId: String = ""

    private val TAG = "MyAppTag"
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
    // send usr data to server
    fun fetchUsername(): String
    {
        return userName
    }

    fun setJoinCode(joinCode:String)
    {
        Log.d(TAG, "TODO when I got the JoinCode: $joinCode")
    }

}