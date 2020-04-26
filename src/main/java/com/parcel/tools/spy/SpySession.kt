package com.parcel.tools.spy

import com.parcel.tools.Globals
import java.io.File
import java.lang.Exception
import java.nio.file.Files
import java.nio.file.Paths


class SpySessionException(message: String):Exception(message)

class SpySession(val sessionId: Long, val sessionPas: Long) {

    private val logger = org.apache.log4j.Logger.getLogger(SpySession::class.java!!)
    private val spyEvents = ArrayList<SpyEvent>()


    private val locations = ArrayList<String>()

    private var currentLocation = ""

    private val users = ArrayList<User>()
    private var spyName = ""


    var started = false
        private set

    var startTime = 0L
    private set

    init {
        startTime = System.currentTimeMillis()
        updateLocations()
    }


    // start stop game


    fun startGame()
    {
        if (!started) {
            started = true
            logger.info("startGame()...")
            updateLocations()

            //делаем локацию
            val locationIndex: Int = GlobalRandomiser.getRundom(locations.size)
            currentLocation = locations[locationIndex]
            logger.info("Location: $currentLocation")

            //выбираем шпиона
            val spyIndex = GlobalRandomiser.getRundom(users.size)
            users[spyIndex].spy = true
            spyName = users[spyIndex].name
            logger.info("Spy is: $spyName")
            logger.info("...Game started")

            startGameEvent()
        }
    }

    fun stopGame()
    {
        logger.info("stopGame()")
        users.clear()
        started = false
        stopGameEvent(spyName)
    }

    //users

    /**
     * Получить информацию отображаемую пользователю во время игры.
     */
    fun getUserInformation(userName: String): UserInformation
    {
        logger.info("getUserInformation($userName)")
        users.forEach {
            if(it.name == userName)
            {
                return UserInformation(it, currentLocation, users.size, getAllUsers())
            }
        }
        return UserInformation("User name not correct")
    }

    fun addUser(name: String): Boolean
    {
        logger.info("addUser($name)...")
        val userExist = isUserExist(name)
        if(started && !userExist)
        {
            logger.warn("Game  started.")
            throw SpySessionException("The game is already running.")
        }
        else if(started && userExist)
        {
            logger.warn("User $name already exists. Access is allowed.")
            addUserEvent(getAllUsers())
            return true
        }
        else if(name.length<1) {
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
            users.add(User(name))
            logger.info("...addUser()")
            addUserEvent(getAllUsers())
            return true
        }
    }


    fun getSpy(userName: String): String
    {
        logger.info("getSpy($userName):$spyName")
        spyIsNotSecretEvent(spyName)
        return spyName

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

    private fun getUser(name: String): User
    {
        users.forEach {
            if(it.name == name)
                return it
        }
        throw SpySessionManagerException("Can`t finde user: $name")
    }

    fun usersInGameCount() =
            users.count()


    /********EVENTS***********/
    fun subscribeSpyEvents(spyEvent: SpyEvent)
    {
        spyEvents.add(spyEvent)
    }

    fun deSubscribeSpyEvents(spyEvent: SpyEvent)
    {
        spyEvents.remove(spyEvent)
    }

    private fun addUserEvent(userList: String)
    {
        spyEvents.forEach {
            it.addUserEvent(userList)
        }
    }

    private fun startGameEvent()
    {
        spyEvents.forEach { it.startGameEvent() }
    }

    private fun stopGameEvent(spyName: String)
    {
        spyEvents.forEach { it.stopGameEvent(spyName) }
    }

    private fun spyIsNotSecretEvent(spyName: String)
    {
        spyEvents.forEach { it.spyIsNotSecretEvent(spyName) }
    }


    /*******SETTINGS*********/


    @Synchronized
    private fun updateLocations() : Boolean
    {
        logger.info("updateLocations()")
        locations.clear()
        Globals.spyLocationManager.getAllLocationsAsString().forEach { locations.add(it) }
        return true
    }


}