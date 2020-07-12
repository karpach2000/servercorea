package com.parcel.tools.games.games.cards

import com.parcel.tools.games.gamesession.GamesSession


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


    override fun getAllUsers():String
    {
        var userList =""
        users.forEach {
            userList = userList+ "    " +it.name + "\n"
        }
        return userList
    }

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
        logger.debug("getUserInformation($userName)")
        users.forEach {
            if (it.name == userName) {
                return UserInformation(it, users.size, getAllCardsUsers(userName))
            }
        }
        return UserInformation("User name not correct")
    }

    fun stopCardsGame() {
        logger.debug("stopGame()")
        users.clear()
        started = false
        stopGameEvent(gameResult)
    }




    /*******SETTINGS*********/


    @Synchronized
    private fun updateCards(): Boolean {
        logger.debug("updateCards()")
        val buffer = users[0].userCard
        for (i in 0..(users.size - 2)) {
            users[i].userCard = users[i + 1].userCard
        }
        users[users.size-1].userCard = buffer

        return true
    }

    fun addUser(name: String, card: String): Boolean {
        logger.debug("addUser(${name},${card})...")
        val user = CardsUser(name)
        user.userCard = card
        return addUser(user)
    }
    fun addCardToUser(userName: String, userCard: String){
        getUser(userName).userCard = userCard
    }

    override fun addUser(name: String): Boolean {
        return addUser(CardsUser(name))
    }


}