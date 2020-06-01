package com.parcel.tools.games.games.spy

import com.parcel.tools.Globals
import com.parcel.tools.games.gamesession.GamesSession
import com.parcel.tools.games.GlobalRandomiser
import java.lang.Exception


class SpySessionException(message: String):Exception(message)

class SpySession(sessionId: Long, sessionPas: Long) : GamesSession<SpyUser, SpyEvent>(sessionId, sessionPas) {

    private val logger = org.apache.log4j.Logger.getLogger(SpySession::class.java!!)

    private val locations = ArrayList<String>()

    private var currentLocation = ""




    init {

        updateLocations()
    }


    // start stop game


    override fun startGame()
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
            gameResult = users[spyIndex].name
            logger.info("Spy is: $gameResult")
            logger.info("...Game started")

            startGameEvent()
        }
    }



    //users

    override fun addUser(name: String) =
            addUser(com.parcel.tools.games.games.spy.SpyUser(name))

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




    fun getSpy(userName: String): String
    {
        logger.info("getSpy($userName):$gameResult")
        spyIsNotSecretEvent(gameResult)
        return gameResult

    }

    private fun spyIsNotSecretEvent(spyName: String)
    {
        gameEvent.forEach { it.spyIsNotSecretEvent(spyName) }
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