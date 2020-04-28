package com.parcel.tools.games.cards

import com.parcel.tools.games.GameSessionManagerException
import com.parcel.tools.games.GamesSessionManager

class CardsSessionManagerException(message: String): GameSessionManagerException(message)

/**
 * Управляет сессиями инры в шпиона
 */
object CardsSessionManager :
        GamesSessionManager<CardsUser, CardsEvent, CardsSession>(){



    private val LOCATION_SIMVOLS_COUNT = 30

    private val logger = org.apache.log4j.Logger.getLogger(CardsSessionManager::class.java!!)


    /*******USERS**********/

    fun getUserInformation(sessionId: Long, sessionPas: Long, userName: String): UserInformation
    {
        logger.info("getUserInformation($sessionId, $sessionPas $userName)")
        return getSession(sessionId, sessionPas).getUserInformation(userName)
    }






    /*******ИГРОВОЙ ПРОЦЕСС******/

    fun getCards(sessionId: Long, sessionPas: Long, userName: String): String
    {
        logger.info("getUserCards($sessionId, $sessionPas)")
        if(isSessionExists(sessionId))
            return getSession(sessionId, sessionPas).getCards(userName)
        else
            throw CardsSessionException("Session $sessionId does not exist. Maybe someone finished the game.")
    }



    /*********УПРАВЛЕНИЕ СЕССИЯМИ**********/

    override fun addSession(sessionId: Long, sessionPas: Long)
            = addSession(CardsSession(sessionId, sessionPas))






}