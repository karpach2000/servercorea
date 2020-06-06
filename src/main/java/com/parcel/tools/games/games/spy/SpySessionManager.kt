package com.parcel.tools.games.games.spy

import com.parcel.tools.Globals
import com.parcel.tools.games.GameSessionManagerException
import com.parcel.tools.games.GamesSessionManager
import com.parcel.tools.games.games.spy.database.SpyLocation
import com.parcel.tools.games.games.spy.database.SpyLocationManagerException

class SpySessionManagerException(message: String): GameSessionManagerException(message)

/**
 * Управляет сессиями инры в шпиона
 */
object SpySessionManager :
        GamesSessionManager<SpyUser, SpyEvent, SpySession>(){



    private val LOCATION_SIMVOLS_COUNT = 30

    private val logger = org.apache.log4j.Logger.getLogger(SpySessionManager::class.java!!)


    /*******USERS**********/

    fun getUserInformation(sessionId: Long, sessionPas: Long, userName: String): UserInformation
    {
        logger.info("getUserInformation($sessionId, $sessionPas $userName)")
        return getSession(sessionId, sessionPas).getUserInformation(userName)
    }






    /*******ИГРОВОЙ ПРОЦЕСС******/

    fun getSpy(sessionId: Long, sessionPas: Long, userName: String): String
    {
        logger.info("getSpy($sessionId, $sessionPas)")
        if(isSessionExists(sessionId))
            return getSession(sessionId, sessionPas).getSpy(userName)
        else
            throw SpySessionException("Session $sessionId does not exist. Maybe someone finished the game.")
    }

    /*******ЛОКАЦИИ******/
    /**
     * Возвращает список основных локаций.
     */

    fun getMainLocationList() :List<SpyLocation> {
        return Globals.spyLocationManager.getAllLocations()
    }


    /**
     * Обновить список локаций в игре
     * @param useUserLocations нужно ли использовать локации пользователя.
     */
    fun updateLocations(sessionId: Long, sessionPas: Long, userName: String,
                        useUserLocations: Boolean = false) : Boolean
    {
        logger.info("updateLocations($sessionId, $sessionPas, $userName)")
        return getSession(sessionId, sessionPas).updateLocations(useUserLocations)
    }


    /**
     * Получить список основных локаций (те что в оригинале)
     */
    fun  getMainLocations(sessionId: Long, sessionPas: Long, userName: String) : List<String>
    {
        logger.info("getMainLocations($sessionId, $sessionPas, $userName)")
        return getSession(sessionId, sessionPas).getMainLocations()
    }

    /**
     * Получить список локаций пользователя администратора игры.
     */
    fun getUserLocations(sessionId: Long, sessionPas: Long, userName: String) : List<String>
    {
        logger.info("getUserLocations($sessionId, $sessionPas, $userName)")
        return getSession(sessionId, sessionPas).getUserLocations()
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


    /*********УПРАВЛЕНИЕ СЕССИЯМИ**********/

    override fun addSession(sessionId: Long, sessionPas: Long)
            = addSession(SpySession(sessionId, sessionPas))






}