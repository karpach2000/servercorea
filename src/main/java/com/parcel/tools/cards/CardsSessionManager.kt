package com.parcel.tools.cards

import com.parcel.tools.Globals

class CardsSessionManagerException(message: String) : Exception(message)

/**
 * Управляет сессиями инры в шпиона
 */
object CardsSessionManager {

    private val gameLifeTime = 60L * 60L * 1000L * 2
    private val destructorPeriod = 60L * 1000L

    private val LOCATION_SIMVOLS_COUNT = 30

    private val logger = org.apache.log4j.Logger.getLogger(CardsSessionManager::class.java)

    private val cardsSessions = ArrayList<CardsSession>()

    init {
        Thread(Runnable {
            destructorAction()
        }).start()
    }


    /**********СОБЫТИЯ************/

    @Synchronized
    fun subscribeCardsSessionEvent(sessionId: Long, sessionPas: Long, cardsEvent: CardsEvent) {
        getSession(sessionId, sessionPas).subscribeCardsEvents(cardsEvent)
    }

    fun deSubscribeCardsSessionEvent(sessionId: Long, sessionPas: Long, CardsEvent: CardsEvent) {
        if (isSessionExists(sessionId))
            getSession(sessionId, sessionPas).deSubscribeCardsEvents(CardsEvent)
    }


    /*******ИГРА******/

    //start stop game

    fun startGame(sessionId: Long, sessionPas: Long): Boolean {
        logger.info("startGame($sessionId, $sessionPas)")
        if (isSessionExists(sessionId)) {
            getSession(sessionId, sessionPas).startGame()
            return true
        } else {
            logger.warn("Game $sessionId not exists.")
            return false
        }
    }

    fun stopGame(sessionId: Long, sessionPas: Long): Boolean {
        logger.info("stopGame($sessionId, $sessionPas)")
        if (isSessionExists(sessionId)) {
            val session = getSession(sessionId, sessionPas)
            session.stopGame()
            cardsSessions.remove(session)
            return true
        }
        return false
    }

    //user
    fun getUserInformation(sessionId: Long, sessionPas: Long, userName: String, userCard: String): UserInformation {
        logger.info("getUserInformation($sessionId, $sessionPas $userName $userCard)")
        return getSession(sessionId, sessionPas).getUserInformation(userName)
    }

    fun addUser(sessionId: Long, sessionPas: Long, userName: String, userCard: String): Boolean {
        logger.info("addUser($sessionId, $sessionPas $userName $userCard)")
        return getSession(sessionId, sessionPas).addUser(userName)
    }

    fun countUsersInGame(sessionId: Long, sessionPas: Long): Int {
        logger.info("usersInGameCount($sessionId, $sessionPas)")
        return getSession(sessionId, sessionPas).usersInGameCount()
    }


    fun getUsers(sessionId: Long, sessionPas: Long, userName: String): String {
        logger.info("getUsers($sessionId, $sessionPas)")
        return getSession(sessionId, sessionPas).getAllUsers(userName)
    }

    fun getCards(sessionId: Long, sessionPas: Long, userName: String, userCard: String): String {
        logger.info("getCards($sessionId, $sessionPas)")
        if (isSessionExists(sessionId))
            return getSession(sessionId, sessionPas).getCards(userName, userCard)
        else
            throw CardsSessionException("Session $sessionId does not exist. Maybe someone finished the game.")
    }


    /*********УПРАВЛЕНИЕ СЕССИЯМИ**********/

    private fun getSession(sessionId: Long, sessionPas: Long): CardsSession {
        cardsSessions.forEach {
            if (it.sessionId == sessionId)
                if (it.sessionPas == sessionPas) {
                    return it
                } else
                    throw CardsSessionManagerException("Id or password not correct!")

        }
        throw CardsSessionManagerException("Id not correct!")
    }

    fun addSession(sessionId: Long, sessionPas: Long): Boolean {
        logger.info("addSession($sessionId, $sessionPas)")
        if (!isSessionExists(sessionId)) {
            cardsSessions.add(CardsSession(sessionId, sessionPas))
            return true
        } else {
            logger.warn("Session $sessionId is already exists.")
            return false
        }
    }


    private fun isSessionExists(sessionId: Long): Boolean {
        cardsSessions.forEach {
            if (it.sessionId == sessionId)
                return true
        }
        return false
    }

    private fun destructorAction() {
        while (true) {
            removeOldGames()
            Thread.sleep(destructorPeriod)
        }
    }

    private fun removeOldGames() {
        logger.debug("Removing old games.")
        val current = System.currentTimeMillis()
        cardsSessions.forEach {
            if (current - it.startTime > this.gameLifeTime) {
                logger.debug("Removing game: ${it.sessionId}")
                cardsSessions.remove(it)
            }
        }
    }

}