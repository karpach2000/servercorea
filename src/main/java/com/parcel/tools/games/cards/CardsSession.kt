package com.parcel.tools.games.cards

import com.parcel.tools.games.GameSessionException
import com.parcel.tools.games.GamesSession


class CardsSessionException(message: String) : Exception(message)
class CardsSession(sessionId: Long, sessionPas: Long) : GamesSession<CardsUser, CardsEvent>(sessionId, sessionPas) {

    private val logger = org.apache.log4j.Logger.getLogger(CardsSession::class.java!!)


    init {

        // updateCards()
    }


    // start stop game


    override fun startGame() {
        if (!started) {
            started = true
            logger.info("startGame()...")
            updateCards()
            gameResult = getAllCardsUsers("")

            logger.info("Cards was changed")
            logger.info("...Game started")

            startGameEvent()
        }
    }


    //users

    private fun getAllCardsUsers(user: String): String {
        var userList = ""
        users.forEach {
            if(user != it.name)
                userList = userList + "    " + it.name + ": " + it.userCard + "\n"
            //else
              //  gameResult = it.userCard
        }
        return userList
    }



    /**
     * Получить информацию отображаемую пользователю во время игры.
     */
    fun getUserInformation(userName: String): UserInformation {
        logger.info("getUserInformation($userName)")
        users.forEach {
            if (it.name == userName) {
                return UserInformation(it, users.size, getAllCardsUsers(userName))
            }
        }
        return UserInformation("User name not correct")
    }

    fun stopCardsGame() {
        logger.info("stopGame()")
        users.clear()
        started = false
        stopGameEvent(gameResult)
    }

    override fun stopGameEvent(userCard: String) {
        gameEvent.forEach { it.stopGameEvent(userCard) }
    }
    override fun startGameEvent() {
        gameEvent.forEach { it.startGameEvent() }
    }


    /*******SETTINGS*********/


    @Synchronized
    private fun updateCards(): Boolean {
        logger.info("updateCards()")
        val buffer = users[0].userCard
        for (i in 0..(users.size - 2)) {
            users[i].userCard = users[i + 1].userCard
        }
        users[users.size-1].userCard = buffer

        return true
    }

    override fun addUser(user: CardsUser): Boolean {
        logger.info("addCardUser(${user.name},${user.userCard})...")
        val userExist = isUserExist(user.name)
        if (started && !userExist) {
            logger.warn("Game  started.")
            throw GameSessionException("The game is already running.")
        } else if (started && userExist) {
            logger.warn("User ${user.name} already exists. Access is allowed.")
            addUserEvent(getAllUsers())
            return true
        } else if (user.name.length < 1) {
            logger.warn("To short user name.")
            throw GameSessionException("To short user name.")
        } else if (!started && userExist) {
            logger.warn("A user with the same name already exists.")
            throw GameSessionException("A user with the same name already exists.")
        } else {

            users.add(user)
            logger.info("...addCardUser()")
            addUserEvent(getAllUsers())
            return true
        }

    }
    fun addCardToUser(userName: String, userCard: String){
        getUser(userName).userCard = userCard
    }

    override fun addUser(name: String): Boolean {
        return super.addUser(CardsUser(name))
    }


}