package com.parcel.tools

import com.parcel.tools.constructor.database.users.UserManager
import com.parcel.tools.games.games.spy.database.SpyLocationManager
import com.parcel.tools.games.games.thirtyyears.database.ThirtyYearsEventManager

object Globals {

    lateinit var userManager: UserManager
    lateinit var spyLocationManager: SpyLocationManager
    lateinit var thirtyYearsEventManager: ThirtyYearsEventManager
}