package com.parcel.tools.cards

interface CardsEvent {


    fun addUserEvent(userList: String)

    fun startGameEvent()

    fun stopGameEvent(userCard: String)

    fun cardsIsNotSecretEvent(userCard: String)


}