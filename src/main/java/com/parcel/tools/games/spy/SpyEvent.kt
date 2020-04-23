package com.parcel.tools.games.spy

import com.parcel.tools.games.GameEvent

interface SpyEvent : GameEvent{



    fun spyIsNotSecretEvent(spyName: String)


}