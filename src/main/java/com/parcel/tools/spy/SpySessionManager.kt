package com.parcel.tools.spy

import com.parcel.tools.Globals
import com.parcel.tools.spy.database.SpyLocation
import com.parcel.tools.spy.database.SpyLocationManager
import com.parcel.tools.spy.database.SpyLocationManagerException

class SpySessionManagerException(message: String): Exception(message)

/**
 * Управляет сессиями инры в шпиона
 */
object SpySessionManager {

    private val gameLifeTime = 60L*60L*1000L*2
    private val destructorPeriod = 60L*1000L

    private val LOCATION_SIMVOLS_COUNT = 30

    private val logger = org.apache.log4j.Logger.getLogger(SpySessionManager::class.java!!)

    private val spySessions = ArrayList<SpySession>()

    init {
        Thread(Runnable {
            destructorAction()
        }).start()
    }

    /*******ЛОКАЦИИ*********/

    /**
     * Возвращает списо к локаций.
     */
    fun getLocationList() :List<SpyLocation> {
        return Globals.spyLocationManager.getAllLocations()
    }

    /**
     * Добавить локацию
     */
    fun addLocation(location: String, user: String): Boolean
    {
        logger.info("addLocation($location, $user)")
        if (location.length>LOCATION_SIMVOLS_COUNT)
        {
            logger.warn("Can`t add location. To much simbols (${location.length}).")
            return false
        }

        var locatinLowCase = location.toLowerCase()
        for(c in locatinLowCase)
        {
            if((c in 'a'..'z') || (c in 'а'..'я') || c=='.' || c==' ' || c=='-')
            {
                //do nothing
            }
            else {
                logger.warn("Can`t add location. Wrong simbols.")
                return false
            }
        }
        return Globals.spyLocationManager.addLocation(location, user)
    }

    /**
     * Удалить локацию.
     */
    fun deleteLocation(location: String, login: String): Boolean
    {
        logger.info("deleteLocation($location, $login)")
        try {
            if(Globals.userManager.getUserRoles(login).contains("ADMIN")) {
                return Globals.spyLocationManager.deleteLocation(location, login)
            }
            else if(Globals.userManager.getUserRoles(login).contains("USER")) {
                val locationUser = Globals.spyLocationManager.getLocationsUser(location)
                if(locationUser==login)
                    return Globals.spyLocationManager.deleteLocation(location, login)
            }
            logger.warn("Delete location error! User has no ruuls.")
            return false
        }
        catch (ex: SpyLocationManagerException)
        {
            logger.warn(ex.message)
            return false
        }

    }

    /**********СОБЫТИЯ************/

    @Synchronized
    fun subscribeSpySessionEvent(sessionId: Long, sessionPas: Long, spyEvent: SpyEvent)
    {
        getSession(sessionId, sessionPas).subscribeSpyEvents(spyEvent)
    }

    fun deSubscribeSpySessionEvent(sessionId: Long, sessionPas: Long, spyEvent: SpyEvent)
    {
        if(isSessionExists(sessionId))
            getSession(sessionId, sessionPas).deSubscribeSpyEvents(spyEvent)
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
            spySessions.remove(session)
            return true
        }
        return false
    }

    //user
    fun getUserInformation(sessionId: Long, sessionPas: Long, userName: String): UserInformation
    {
        logger.info("getUserInformation($sessionId, $sessionPas $userName)")
        return getSession(sessionId, sessionPas).getUserInformation(userName)
    }

    fun addUser(sessionId: Long, sessionPas: Long, userName: String):Boolean
    {
        logger.info("addUser($sessionId, $sessionPas $userName)")
        return getSession(sessionId, sessionPas).addUser(userName)
    }

    fun countUsersInGame(sessionId: Long, sessionPas: Long):Int
    {
        logger.info("usersInGameCount($sessionId, $sessionPas)")
        return getSession(sessionId, sessionPas).usersInGameCount()
    }



    fun getUsers(sessionId: Long, sessionPas: Long):String {
        logger.info("getUsers($sessionId, $sessionPas)")
        return getSession(sessionId, sessionPas).getAllUsers()
    }

    fun getSpy(sessionId: Long, sessionPas: Long, userName: String): String
    {
        logger.info("getSpy($sessionId, $sessionPas)")
        if(isSessionExists(sessionId))
            return getSession(sessionId, sessionPas).getSpy(userName)
        else
            throw SpySessionException("Session $sessionId does not exist. Maybe someone finished the game.")
    }






    /*********УПРАВЛЕНИЕ СЕССИЯМИ**********/

    private fun getSession(sessionId: Long, sessionPas: Long):SpySession
    {
        spySessions.forEach {
            if(it.sessionId == sessionId)
                if(it.sessionPas == sessionPas)
                {
                    return it
                }
                else
                    throw SpySessionManagerException("Id or password not correct!")

        }
        throw SpySessionManagerException("Id not correct!")
    }

    fun addSession(sessionId: Long, sessionPas: Long): Boolean
    {
        logger.info("addSession($sessionId, $sessionPas)")
        if(!isSessionExists(sessionId))
        {
            spySessions.add(SpySession(sessionId,sessionPas))
            return true
        }
        else
        {
            logger.warn("Session $sessionId is already exists.")
            return false
        }
    }


    private fun isSessionExists(sessionId: Long): Boolean
    {
        spySessions.forEach {
            if(it.sessionId == sessionId)
                return true
        }
        return false
    }

    private fun destructorAction()
    {
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
        spySessions.forEach {
            if(current- it.startTime> this.gameLifeTime) {
                logger.debug("Removing game: ${it.sessionId}")
                spySessions.remove(it)
            }
        }
    }

}