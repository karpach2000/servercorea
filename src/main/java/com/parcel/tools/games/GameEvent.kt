package com.parcel.tools.games

interface GameEvent {

    /**
     * Имя пользователяя которому должен прилететь Event.
     */
    var userName : String


    /**
     * Идентефикатор по которому можно отличить 2 разных пользователей с одинаковыми именами пользователя
     */
    var identeficator : String

    fun addUserEvent(userList: String)

    fun startGameEvent()

    fun stopGameEvent(spyName: String)

}