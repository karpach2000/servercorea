package com.parcel.tools.spy

interface SpyEvent {


    fun addUserEvent(userList: String)

    fun startGameEvent()

    fun stopGameEvent(spyName: String)

    fun spyIsNotSecretEvent(spyName: String)


}