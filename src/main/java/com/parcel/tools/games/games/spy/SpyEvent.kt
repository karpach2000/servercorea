package com.parcel.tools.games.games.spy

import com.parcel.tools.games.GameEvent

interface SpyEvent : GameEvent{

    fun spyIsNotSecretEvent(spyName: String)

    fun updateLocationList(locations: String)



}