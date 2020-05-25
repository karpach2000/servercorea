package com.parcel.tools.games.games.thirtyyears

import com.parcel.tools.games.GameSessionManagerException
import com.parcel.tools.games.GamesSessionManager
import com.parcel.tools.games.games.spy.SpyEvent
import com.parcel.tools.games.games.spy.SpySession
import com.parcel.tools.games.games.spy.SpySessionManager
import com.parcel.tools.games.games.spy.SpyUser

class ThirtyYearsSessionManagerException(message: String): GameSessionManagerException(message)


/**
 * Управляет сессиями инры 30 лет.
 */
object ThirtyYearsSessionManager :
        GamesSessionManager<ThirtyYearsUser , ThirtyYearsEvent, ThirtyYearsSession>(){

    private val logger = org.apache.log4j.Logger.getLogger(ThirtyYearsSessionManager::class.java!!)


}