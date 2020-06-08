package com.parcel.tools.games.games.spy

import com.parcel.tools.Globals
import com.parcel.tools.games.gamesession.GamesSession
import com.parcel.tools.games.GlobalRandomiser
import com.parcel.tools.games.games.spy.comunicateinformation.SpyLocations
import java.lang.Exception


class SpySessionException(message: String):Exception(message)

class SpySession(sessionId: Long, sessionPas: Long) : GamesSession<SpyUser, SpyEvent>(sessionId, sessionPas) {

    private val logger = org.apache.log4j.Logger.getLogger(SpySession::class.java!!)

    private val locations = ArrayList<String>()

    private var currentLocation = ""

    /**
     * Информация о том нужно ли использовать локации пользователя.
     */
    var useUserLocations = false




    init {

        updateLocations()
    }


    // start stop game


    override fun startGame()
    {
        if (!started) {
            started = true
            logger.debug("startGame()...")
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
        logger.debug("getUserInformation($userName)")
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
        logger.debug("getSpy($userName):$gameResult")
        spyIsNotSecretEvent(gameResult)
        return gameResult

    }

    private fun spyIsNotSecretEvent(spyName: String)
    {
        gameEvent.forEach { it.spyIsNotSecretEvent(spyName) }
    }


    /*******SETTINGS*********/


    /**
     * Обновить список локаций.
     * @param useUserLocations использовать пользовательские локации.
     */
    @Synchronized
    fun updateLocations(useUserLocations: Boolean = false) : Boolean
    {
        logger.debug("updateLocations()")
        locations.clear()
        Globals.spyLocationManager.getLocatioinsByRole("ADMIN") .forEach { locations.add(it) }
        if(useUserLocations && this.registeredGameCreator!="anonymousUser")
            Globals.spyLocationManager.getLocatioinsByLogin(registeredGameCreator)
                    .forEach { locations.add(it) }
        newLocationsEvent()
        return true
    }


    /**
     * Получить список основных локаций (те что в оригинале)
     */
    fun getMainLocations() : List<String>
    {
        logger.debug("getMainLocations()")
        return Globals.spyLocationManager.getLocatioinsByRole("ADMIN")
    }

    /**
     * Получить список локаций пользователя администратора игры.
     */
    fun getUserLocations() : List<String>
    {
        logger.debug("getMainLocations()")
        if(this.registeredGameCreator!="anonymousUser")
            return Globals.spyLocationManager.getLocatioinsByLogin(registeredGameCreator)
        else return ArrayList<String>()
    }


    /******EVENTS********/

    /**
     * Событие что кто то обновил список локаций.
     */
    fun newLocationsEvent()
    {
        val spyLocations = SpyLocations()
        spyLocations.publicLocations = getMainLocations() as ArrayList<String>
        spyLocations.userLocations = getUserLocations() as ArrayList<String>
        spyLocations.useUserLocations = this.useUserLocations
        gameEvent.forEach { it.updateLocationList(spyLocations.toJson()) }
    }




}