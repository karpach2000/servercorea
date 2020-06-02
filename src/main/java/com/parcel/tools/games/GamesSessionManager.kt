package com.parcel.tools.games

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

    abstract fun addSession(sessionId: Long, sessionPas: Long): Boolean

    protected fun addSession(gamesSession: GS): Boolean
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

    protected fun isSessionExists(sessionId: Long): Boolean
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


    /********СБОРЩИКИ МУСОРА*******/

    private fun destructorAction()
    {
        logger.info("START DESTRUCTOR!")
        while (true)
        {
            removeOldGames()
            Thread.sleep(destructorPeriod)
        }
    }

    private fun removeOldGames()
    {
        logger.debug("Removing old games.")
        val current = System.currentTimeMillis()
        for(gs in gameSessions)
        {
            if(current- gs.startTime> this.gameLifeTime) {
                logger.info("Removing game: ${gs.sessionId}")
                gameSessions.remove(gs)
            }
        }
    }
}