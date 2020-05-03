package com.parcel.tools.games

interface GameEvent {

    fun addUserEvent(userList: String)

    fun startGameEvent()

    fun stopGameEvent(userCard: String)

}