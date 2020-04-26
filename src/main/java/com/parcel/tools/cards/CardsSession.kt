package com.parcel.tools.cards

import com.parcel.tools.Globals
import java.io.File
import java.lang.Exception
import java.nio.file.Files
import java.nio.file.Paths


class CardsSessionException(message: String) : Exception(message)

class CardsSession(val sessionId: Long, val sessionPas: Long) {

    private val logger by lazy { org.apache.log4j.Logger.getLogger(CardsSession::class.java) }
    private val cardsEvents = ArrayList<CardsEvent>()

    private var currentCard = ""

    private val users = ArrayList<User>()
    private var cardsName = ""


    var started = false
        private set

    var startTime = 0L
        private set

    init {
        startTime = System.currentTimeMillis()

    }


    // start stop game


    fun startGame() {
        if (!started) {
            started = true
            logger.info("startGame()...")
            updateCards(users)
            startGameEvent()
        }
    }

    fun stopGame() {
        logger.info("stopGame()")
        stopGameEvent(cardsName)
        users.clear()
        started = false

    }

    //users

    /**
     * Получить информацию отображаемую пользователю во время игры.
     */
    fun getUserInformation(userName: String): UserInformation {
        logger.info("getUserInformation($userName)")
        users.forEach {
            if (it.name == userName) {
                return UserInformation(it, users.size, getAllUsers(it.name))
            }
        }
        return UserInformation("User name not correct")
    }

    fun addUser(name: String): Boolean {
        logger.info("addUser($name)...")
        val userExist = isUserExist(name)
        if (started && !userExist) {
            logger.warn("Game  started.")
            throw CardsSessionException("The game is already running.")
        } else if (started && userExist) {
            logger.warn("User $name already exists. Access is allowed.")
            addUserEvent(getAllUsers(name))
            return true
        } else if (name.length < 1) {
            logger.warn("To short user name.")
            throw CardsSessionException("To short user name.")
        } else if (!started && userExist) {
            logger.warn("A user with the same name already exists.")
            throw CardsSessionException("A user with the same name already exists.")
        } else {
            users.add(User(name))
            logger.info("...addUser()")
            addUserEvent(getAllUsers(name))
            return true
        }
    }


    fun getCards(userName: String, userCard: String): String {
        logger.info("getCards($userName):$userCard")
        cardsIsNotSecretEvent(userCard)
        return userCard

    }

    fun getAllUsers(userName: String): String {
        var userList = ""
        users.forEach {
            if (it.name != userName)
                userList = userList + "    " + it.name + ": " + it.userCard + "\n"
        }
        return userList
    }


    private fun isUserExist(name: String): Boolean {
        users.forEach {
            if (it.name == name)
                return true
        }
        return false
    }

    private fun getUser(name: String): User {
        users.forEach {
            if (it.name == name)
                return it
        }
        throw CardsSessionManagerException("Can`t finde user: $name")
    }

    fun usersInGameCount() =
            users.count()


    /********EVENTS***********/
    fun subscribeCardsEvents(cardsEvent: CardsEvent) {
        cardsEvents.add(cardsEvent)
    }

    fun deSubscribeCardsEvents(cardsEvent: CardsEvent) {
        cardsEvents.remove(cardsEvent)
    }

    private fun addUserEvent(userList: String) {
        cardsEvents.forEach {
            it.addUserEvent(userList)
        }
    }

    private fun startGameEvent() {
        cardsEvents.forEach { it.startGameEvent() }
    }

    private fun stopGameEvent(userCard: String) {
        cardsEvents.forEach { it.stopGameEvent(userCard) }
    }

    private fun cardsIsNotSecretEvent(userCard: String) {
        cardsEvents.forEach { it.cardsIsNotSecretEvent(userCard) }
    }


    /*******SETTINGS*********/


    @Synchronized
    private fun updateCards(Users: ArrayList<User>): Boolean {
        logger.info("flushCards()")
        val buffer = Users[0].userCard
        for (i in 0..(Users.size - 1)) {
            Users[i].userCard = Users[i + 1].userCard
        }
        Users[Users.size].userCard = buffer
        return true
    }


}