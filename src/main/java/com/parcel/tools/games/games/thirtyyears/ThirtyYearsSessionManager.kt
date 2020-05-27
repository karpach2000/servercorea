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

    fun setRealExcude(sessionId: Long, sessionPas: Long, userName: String, excute: String): Boolean
    {
        logger.info("setRealExcude($sessionId, $sessionPas, $userName, $excute)")
        return getSession(sessionId, sessionPas).setRealExcute(userName, excute)
    }

    fun setFalshExcute(sessionId: Long, sessionPas: Long, userName: String, excute: String): Boolean
    {
        logger.info("setFalshExcute($sessionId, $sessionPas, $userName, $excute)")
        return getSession(sessionId, sessionPas).setFalshExcute(userName, excute)
    }

    fun vote(sessionId: Long, sessionPas: Long, userName: String, anser: String): Boolean
    {
        logger.info("setFalshExcute($sessionId, $sessionPas)")
        return getSession(sessionId, sessionPas).vote(userName, anser)
    }




    override fun addSession(sessionId: Long, sessionPas: Long)
            = addSession(ThirtyYearsSession(sessionId, sessionPas))

}