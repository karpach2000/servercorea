package com.parcel.tools.games.gamesession

import com.parcel.tools.games.GameErrors
import com.parcel.tools.games.GameEvent
import com.parcel.tools.games.GameSessionManagerException
import com.parcel.tools.games.gamesession.timer.GameSessionTimer
import com.parcel.tools.games.gamesuser.GameUser

class GameSessionException(message: String): Exception(message )
class GameSessionNotFatalException(error: GameErrors):Exception(error.toString())
abstract class  GamesSession<U : GameUser, E: GameEvent>(val sessionId: Long, val sessionPas: Long) {

    private val logger = org.apache.log4j.Logger.getLogger(GamesSession::class.java!!)

    /**
     * Имя пользователя (зарегестрированного) создавшего игру.
     */
    var registeredGameCreator = "anonymousUser"
    /**
     *
     */
    val gameEvent = ArrayList<E>()

    /**
     * Список игроков играющих в игру.
     */
    protected var users = ArrayList<U>()

    /**
     * Используется как свойство в классе GameSession при голосовани.
     */
    protected val gameSessionVote = GameSessionVote(users)
    /**
     * Используется в качестве таймера для игр.
     */
    protected  val gameSessionTimer = GameSessionTimer()

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
        logger.debug("stopGame()")
        users.clear()
        started = false
        stopGameEvent(gameResult)
    }

    /******USERS*******/





    abstract fun addUser(name: String) :Boolean

    fun setIdenteficatorToUser(name: String, identeficator :String = "") : Boolean
    {
        getUser(name).identeficator = identeficator
        return true
    }



    protected fun addUser(user: U): Boolean
    {
        logger.debug("addUser(${user.name})...")
        val userExist = isUserExist(user.name)
        if(started && !userExist)
        {
            logger.warn("Game  started.")
            throw GameSessionNotFatalException(GameErrors.GAME_IS_ALREADY_RUNUNG)
        }
        else if(started && userExist)
        {
            logger.warn("User $user already exists. Access is allowed.")
            addUserEvent(getAllUsers())
            return true
        }
        else if(user.name.length<1) {
            logger.warn("To short user name.")
            throw GameSessionNotFatalException(GameErrors.TO_SHORT_USER_NAME)
        }
        else if(!started && userExist)
        {
            logger.warn("A user with the same name already exists.")
            throw GameSessionNotFatalException(GameErrors.USER_EXISTS)
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
    fun subscribeGameEvents(gameEvent: E)
    {
        this.gameEvent.add(gameEvent)
    }

    fun getGameEvents(name: String) : E
    {
        var ans = gameEvent[0]
        gameEvent.forEach { if(it.userName==name) ans=it }
        return ans
    }


    fun deSubscribeGameEvents(gameEvent: E)
    {
        this.gameEvent.remove(gameEvent)
    }

    /**
     * Удалить подписчика на игру из списка подписчиков.
     * @param identeficator иденотефикатор полдписчика
     * @return true - нашли и удалили, false - не неашли
     */
    fun deSubscribeGameEvents(identeficator: String): Boolean
    {
        for (ge in gameEvent){
            if(ge.identeficator==identeficator)
            {
                this.gameEvent.remove(ge)
                return true
            }
        }
        return false
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