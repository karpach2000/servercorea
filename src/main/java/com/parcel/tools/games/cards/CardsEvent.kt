package com.parcel.tools.games.cards

import com.parcel.tools.games.GameEvent

interface CardsEvent : GameEvent{

    fun cardsIsNotSecretEvent(userCard: String)


}