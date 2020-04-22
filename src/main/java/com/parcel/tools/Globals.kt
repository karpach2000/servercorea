package com.parcel.tools

import com.parcel.tools.constructor.database.users.UserManager
import com.parcel.tools.spy.database.SpyLocationManager

object Globals {

    lateinit var userManager: UserManager
    lateinit var spyLocationManager: SpyLocationManager
}