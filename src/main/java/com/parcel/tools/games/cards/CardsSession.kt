package com.parcel.tools.games.cards

import com.parcel.tools.games.GamesSession


class CardsSessionException(message: String):Exception(message)

class CardsSession(sessionId: Long, sessionPas: Long) : GamesSession<CardsUser, CardsEvent>(sessionId, sessionPas) {

    private val logger = org.apache.log4j.Logger.getLogger(CardsSession::class.java!!)


    init {

       // updateCards()
    }


    // start stop game


    override fun startGame()
    {
        if (!started) {
            started = true
            logger.info("startGame()...")
            updateCards()


            logger.info("Cards was changed")
            logger.info("...Game started")

            startGameEvent()
        }
    }



    //users

    override fun addUser(name: String) =
            addUser(com.parcel.tools.games.cards.CardsUser(name))

    /**
     * Получить информацию отображаемую пользователю во время игры.
     */
    fun getUserInformation(userName: String): UserInformation
    {
        logger.info("getUserInformation($userName)")
        users.forEach {
            if(it.name == userName)
            {
                return UserInformation(it, users.size, getAllUsers())
            }
        }
        return UserInformation("User name not correct")
    }




    fun getCards(userName: String): String
    {
        logger.info("getCards($userName):$gameResult")
        cardsIsNotSecretEvent(gameResult)
        return gameResult

    }

    private fun cardsIsNotSecretEvent(userCard: String)
    {
        gameEvent.forEach { it.cardsIsNotSecretEvent(userCard) }
    }


    /*******SETTINGS*********/


    @Synchronized
    private fun updateCards() : Boolean
    {
        logger.info("updateCards()")
        val buffer = users[0].userCard
        for(i in 0..(users.size - 1)){
            users[i].userCard = users[i+1].userCard
        }
        users[users.size].userCard = buffer

        return true
    }


}