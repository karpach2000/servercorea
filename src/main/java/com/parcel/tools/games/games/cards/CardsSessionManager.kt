package com.parcel.tools.games.games.cards

import com.parcel.tools.games.GameSessionManagerException
import com.parcel.tools.games.GamesSessionManager

class CardsSessionManagerException(message: String) : GameSessionManagerException(message)

/**
 * Управляет сессиями игры в карточки
 */
object CardsSessionManager :
        GamesSessionManager<CardsUser, CardsEvent, CardsSession>() {



    private val logger = org.apache.log4j.Logger.getLogger(CardsSessionManager::class.java!!)


    /*******USERS**********/

    fun getUserInformation(sessionId: Long, sessionPas: Long, userName: String, userCard: String): UserInformation {
        logger.info("getUserInformation($sessionId, $sessionPas $userName,$userCard)")
        return getSession(sessionId, sessionPas).getUserInformation(userName)
    }


    /*******ИГРОВОЙ ПРОЦЕСС******/
/*
    fun getCards(user: CardsUser, sessionId: Long, sessionPas: Long): CardsUser {
        logger.info("getUserCards($sessionId, $sessionPas)")
        if (isSessionExists(sessionId))
            return getSession(sessionId, sessionPas).getCards(user)
        else
            throw CardsSessionException("Session $sessionId does not exist. Maybe someone finished the game.")
    }
*/
    /*********УПРАВЛЕНИЕ СЕССИЯМИ**********/

    override fun addSession(sessionId: Long, sessionPas: Long) = addSession(CardsSession(sessionId, sessionPas))
    /*   override fun addSession(gamesSession: CardsSession): Boolean {
           logger.info("addCardsSession(${gamesSession.sessionId}, ${gamesSession.sessionPas})")
           if (!isSessionExists(gamesSession.sessionId)) {
               gameSessions.add(gamesSession)
               return true
           } else {
               logger.warn("Session ${gamesSession.sessionId} is already exists.")
               return false
           }
       }*/
    fun addUser(sessionId: Long, sessionPas: Long, userName: String, userCard: String):Boolean{
        logger.info("addUser($sessionId, $sessionPas, $userName, $userCard)")
        val ans = super.addUser(sessionId, sessionPas, userName)
        getSession(sessionId, sessionPas).addCardToUser(userName, userCard)
        return ans
    }

}