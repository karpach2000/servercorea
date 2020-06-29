package com.parcel.tools.games

import com.parcel.tools.games.games.cards.CardsSessionManager
import com.parcel.tools.games.games.spy.*
import com.parcel.tools.games.gamesession.GamesSession
import com.parcel.tools.games.gamesuser.GameUser

open class  GameSessionManagerException(message: String):Exception(message)

abstract class GamesSessionManager<U : GameUser, E:GameEvent, GS: GamesSession<U, E>>(){
    private val gameLifeTime = 60L*60L*1000L*2
    private val destructorPeriod = 60L*1000L

    private val logger = org.apache.log4j.Logger.getLogger(GamesSessionManager::class.java!!)

    private val gameSessions = ArrayList<GS>()


    init {
        Thread(Runnable {
            destructorAction()
        }).start()
    }

    /*******ИГРА******/

    //start stop game

    fun startGame(sessionId: Long, sessionPas: Long): Boolean {
        logger.info("startGame($sessionId, $sessionPas)")
        if(isSessionExists(sessionId)) {
            getSession(sessionId, sessionPas).startGame()
            return true
        }
        else
        {
            logger.warn("Game $sessionId not exists.")
            return false
        }
    }

    fun stopGame(sessionId: Long, sessionPas: Long):Boolean
    {
        logger.info("stopGame($sessionId, $sessionPas)")
        if (isSessionExists(sessionId)) {
            val session = getSession(sessionId, sessionPas)
            session.stopGame()
            gameSessions.remove(session)
            return true
        }
        logger.warn("Can`t finde game($sessionId, $sessionPas)")
        return false
    }

    /**
     * Выставить имя пользователя (зарегестрированного) создавшего игру.
     */
    fun setRegisteredGameCreator(sessionId: Long, sessionPas: Long, userName: String, gameCreator: String)
    {
        logger.info("setGameCreator($sessionId, $sessionPas $userName,$gameCreator)")
        getSession(sessionId, sessionPas).registeredGameCreator=gameCreator
    }


    /*******USERS**********/

    fun addUser(sessionId: Long, sessionPas: Long, userName: String):Boolean
    {
        logger.info("addUser($sessionId, $sessionPas $userName)")
        return getSession(sessionId, sessionPas).addUser(userName)
    }

    fun getUsers(sessionId: Long, sessionPas: Long):String {
        logger.info("getUsers($sessionId, $sessionPas)")
        return getSession(sessionId, sessionPas).getAllUsers()
    }

    fun countUsersInGame(sessionId: Long, sessionPas: Long):Int
    {
        logger.info("usersInGameCount($sessionId, $sessionPas)")
        return getSession(sessionId, sessionPas).usersInGameCount()
    }






    /*********УПРАВЛЕНИЕ СЕССИЯМИ**********/

    abstract fun addSessionIfNotExist(sessionId: Long, sessionPas: Long): Boolean

    /**
     * Добавить сессию если последняя не существует.
     * @return false если сессия уже существовала.
     */
    protected fun addSessionIfNotExist(gamesSession: GS): Boolean
    {
        logger.info("addSession(${gamesSession.sessionId}, ${gamesSession.sessionPas})")
        if(!isSessionExists(gamesSession.sessionId))
        {
            gameSessions.add(gamesSession)
            return true
        }
        else
        {
            logger.warn("Session ${gamesSession.sessionId} is already exists.")
            return false
        }
    }




    fun isSessionExists(sessionId: Long): Boolean
    {
        gameSessions.forEach {
            if(it.sessionId == sessionId)
                return true
        }
        return false
    }

    protected fun getSession(sessionId: Long, sessionPas: Long):GS
    {
        gameSessions.forEach {
            if(it.sessionId == sessionId)
                if(it.sessionPas == sessionPas)
                {
                    return it
                }
                else
                    throw GameSessionManagerException("Id or password not correct!")

        }
        throw GameSessionManagerException("Id not correct!")
    }

    /**********СОБЫТИЯ************/

    @Synchronized
    fun subscribeGameSessionEvent(sessionId: Long, sessionPas: Long, event: E)
    {
        getSession(sessionId, sessionPas).subscribeGameEvents(event)
    }

    fun getGameSessionEvents(sessionId: Long, sessionPas: Long, userName: String): E
    {
        return getSession(sessionId, sessionPas).getGameEvents(userName)
    }

    fun deSubscribeGameSessionEvent(sessionId: Long, sessionPas: Long, event: E)
    {
        if(SpySessionManager.isSessionExists(sessionId))
            getSession(sessionId, sessionPas).deSubscribeGameEvents(event)
    }


    /******СТАТИСТИКА************/
    fun countSessions() = gameSessions.size

    /**
     * Получает Id всех текущих игр.
     */
    fun getGameIdList(): ArrayList<Long>
    {
        val ids = ArrayList<Long>()
        for(gs in gameSessions)
        {
            ids.add(gs.sessionId)
        }
        return ids
    }


    /********СБОРЩИКИ МУСОРА*******/

    private fun destructorAction()
    {
        logger.info("START DESTRUCTOR!")
        while (true)
        {
            try {
                removeOldGames()
            }
            catch (e: Exception)
            {
                logger.error(e.message)
            }
            Thread.sleep(destructorPeriod)
        }
    }

    private fun removeOldGames()
    {
        logger.debug("Removing old games.")
        val current = System.currentTimeMillis()
        val oldGames = ArrayList<GamesSession<U, E>>()
        for(gs in gameSessions)
        {
            if(current- gs.startTime> this.gameLifeTime) {
                logger.info("Removing game: ${gs.sessionId}")
                oldGames.add(gs)
            }
        }
        oldGames.forEach { gameSessions.remove(it) }
    }
}