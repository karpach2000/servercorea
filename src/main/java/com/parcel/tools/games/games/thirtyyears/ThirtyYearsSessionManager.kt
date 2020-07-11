package com.parcel.tools.games.games.thirtyyears

import com.parcel.tools.games.GameSessionManagerException
import com.parcel.tools.games.GamesSessionManager

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
    fun round(sessionId: Long, sessionPas: Long, userName: String): Boolean
    {
        logger.info("round($sessionId, $sessionPas, $userName)")
        return getSession(sessionId, sessionPas).round()
    }
    fun getGameStatus(sessionId: Long, sessionPas: Long, userName: String): String
    {
        logger.info("getGameStatus($sessionId, $sessionPas, $userName)")
        return getSession(sessionId, sessionPas).getGameStatus(userName)
    }



    override fun addSessionIfNotExist(sessionId: Long, sessionPas: Long)
            = addSessionIfNotExist(ThirtyYearsSession(sessionId, sessionPas))


}