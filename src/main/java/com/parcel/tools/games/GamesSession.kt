package com.parcel.tools.games

import com.parcel.tools.games.spy.SpySessionException
import com.parcel.tools.games.spy.SpySessionManagerException
import com.parcel.tools.games.spy.SpyUser

abstract class  GamesSession<U : GameUser, E:GameEvent>(val sessionId: Long, val sessionPas: Long) {

    private val logger = org.apache.log4j.Logger.getLogger(GamesSession::class.java!!)

    protected val gameEvent = ArrayList<E>()

    protected var users = ArrayList<U>()

    var startTime = 0L
        private set

    protected var started = false

    protected var gameResult = ""

    init {
        startTime = System.currentTimeMillis()
    }




    abstract fun  startGame()

    fun stopGame()
    {
        logger.info("stopGame()")
        users.clear()
        started = false
        stopGameEvent(gameResult)
    }

    /******USERS*******/

    abstract fun addUser(name: String) :Boolean

    protected fun addUser(user: U): Boolean
    {
        logger.info("addUser($user)...")
        val userExist = isUserExist(user.name)
        if(started && !userExist)
        {
            logger.warn("Game  started.")
            throw SpySessionException("The game is already running.")
        }
        else if(started && userExist)
        {
            logger.warn("User $user already exists. Access is allowed.")
            addUserEvent(getAllUsers())
            return true
        }
        else if(user.name.length<1) {
            logger.warn("To short user name.")
            throw SpySessionException("To short user name.")
        }
        else if(!started && userExist)
        {
            logger.warn("A user with the same name already exists.")
            throw SpySessionException("A user with the same name already exists.")
        }
        else
        {

            users.add(user)
            logger.info("...addUser()")
            addUserEvent(getAllUsers())
            return true
        }
    }
    fun getAllUsers():String
    {
        var userList =""
        users.forEach {
            userList = userList+ "    " +it.name + "\n"
        }
        return userList
    }

    private fun isUserExist(name: String): Boolean
    {
        users.forEach {
            if(it.name == name)
                return true
        }
        return false
    }

    protected fun getUser(name: String): U
    {
        users.forEach {
            if(it.name == name)
                return it
        }
        throw GameSessionManagerException("Can`t finde user: $name")
    }

    fun usersInGameCount() =
            users.count()

    /********EVENTS***********/
    fun subscribeSpyEvents(gameEvent: E)
    {
        this.gameEvent.add(gameEvent)
    }

    fun deSubscribeSpyEvents(gameEvent: E)
    {
        this.gameEvent.remove(gameEvent)
    }

    private fun addUserEvent(userList: String)
    {
        gameEvent.forEach {
            it.addUserEvent(userList)
        }
    }

    protected fun startGameEvent()
    {
        gameEvent.forEach { it.startGameEvent() }
    }

    protected fun stopGameEvent(gameResult: String)
    {
        gameEvent.forEach { it.stopGameEvent(gameResult) }
    }

}