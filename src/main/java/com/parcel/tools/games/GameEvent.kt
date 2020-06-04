package com.parcel.tools.games

interface GameEvent {

    /**
     * Имя пользователяя которому должен прилететь Event.
     */
    var userName : String

    fun addUserEvent(userList: String)

    fun startGameEvent()

    fun stopGameEvent(spyName: String)

}