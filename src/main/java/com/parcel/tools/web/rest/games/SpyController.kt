package com.parcel.tools.web.rest.games

import com.parcel.tools.constructor.Page
import com.parcel.tools.constructor.bodies.games.CounterGames
import com.parcel.tools.constructor.bodies.gamesSettings.CounterGamesSettings
import com.parcel.tools.games.gamesession.GameSessionException
import com.parcel.tools.games.GameSessionManagerException
import com.parcel.tools.games.games.spy.SpySessionException
import com.parcel.tools.games.games.spy.SpySessionManager
import com.parcel.tools.statistics.StatisticsForGames
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.User
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import java.io.IOException
import javax.servlet.http.HttpSession


@Controller
class SpyController {

    private val logger = org.apache.log4j.Logger.getLogger(SpyController::class.java!!)
    private val static = StatisticsForGames("Spy")

    @RequestMapping("/games/spy")
    @Throws(IOException::class)
    internal fun games(model: Model, session: HttpSession): String {
        static.openGame()
        val counter = CounterGames()
        val page = Page(counter)
        model.addAttribute("page", page)
        return "web/html/games/spy"
    }




    @RequestMapping("/games/spy_add_session")
    @ResponseBody
    @Throws(IOException::class)
    internal fun addSession(model: Model,
                         @RequestParam("userName") userName: String = "",
                         @RequestParam("sessionId") sessionId: String = "",
                         @RequestParam("sessionPas") sessionPas: String = ""): String {
        logger.info("addSession($userName, $sessionId, $sessionPas)")
        return SpySessionManager.addSession(sessionId.toLong(), sessionPas.toLong()).toString()
    }

    @RequestMapping("/games/spy_addUser")
    @ResponseBody
    @Throws(IOException::class)
    internal fun addUser(model: Model,
                         @RequestParam("userName") userName: String = "",
                         @RequestParam("sessionId") sessionId: String = "",
                         @RequestParam("sessionPas") sessionPas: String = ""): String {
        logger.info("addUser($userName, $sessionId, $sessionPas)")
        return try {
            SpySessionManager.addUser(sessionId.toLong(), sessionPas.toLong(), userName).toString()
        }
        catch(ex: GameSessionManagerException) {
            ex.message!!
        }
        catch (ex: GameSessionException){
            logger.warn(ex.message)
            ex.message!!
        }

    }

    @RequestMapping("/games/spy_start_game")
    @ResponseBody
    @Throws(IOException::class)
    internal fun startGame(model: Model,
                           @RequestParam("userName") userName: String = "",
                           @RequestParam("sessionId") sessionId: String = "",
                           @RequestParam("sessionPas") sessionPas: String = ""): String {
        static.startGame()
        logger.info("startGame($userName, $sessionId, $sessionPas)")
        SpySessionManager.startGame(sessionId.toLong(), sessionPas.toLong())
        val userInformation =
                SpySessionManager.getUserInformation(sessionId.toLong(), sessionPas.toLong(),userName)
        return userInformation.toJson()
    }

    @RequestMapping("/games/spy_stop_game")
    @ResponseBody
    @Throws(IOException::class)
    internal fun stopGame(model: Model,
                          @RequestParam("userName") userName: String = "",
                          @RequestParam("sessionId") sessionId: String = "",
                          @RequestParam("sessionPas") sessionPas: String = ""): String {
        logger.info("stopGame($userName, $sessionId, $sessionPas)")
        SpySessionManager.stopGame(sessionId.toLong(), sessionPas.toLong())
        return "true"
    }

    @RequestMapping("/games/spy_get_spy")
    @ResponseBody
    @Throws(IOException::class)
    internal fun getSpy(model: Model,
                        @RequestParam("userName") userName: String = "",
                        @RequestParam("sessionId") sessionId: String = "",
                        @RequestParam("sessionPas") sessionPas: String = ""): String {
        logger.info("getSpy($userName, $sessionId, $sessionPas)")
        try {
            return SpySessionManager.getSpy(sessionId.toLong(), sessionPas.toLong(), userName)
        }
        catch (ex: SpySessionException)
        {
            logger.warn(ex.message)
            return ex.message!!
        }

    }


    @RequestMapping("/games/spy_get_users")///games/spy_get_users
    @ResponseBody
    @Throws(IOException::class)
    internal fun getUsers(model: Model,
                             @RequestParam("userName") userName: String = "",
                             @RequestParam("sessionId") sessionId: String = "",
                             @RequestParam("sessionPas") sessionPas: String = ""): String {
        logger.info("getUsers($userName, $sessionId, $sessionPas)")
        return SpySessionManager.getUsers(sessionId.toLong(), sessionPas.toLong())
    }

    @RequestMapping("/games/spy_count_users")///games/spy_get_users
    @ResponseBody
    @Throws(IOException::class)
    internal fun countUsers(model: Model,
                          @RequestParam("userName") userName: String = "",
                          @RequestParam("sessionId") sessionId: String = "",
                          @RequestParam("sessionPas") sessionPas: String = ""): String {
        logger.info("countUsers($userName, $sessionId, $sessionPas)")
        return SpySessionManager.countUsersInGame(sessionId.toLong(), sessionPas.toLong()).toString()
    }


    /**
     * Выставить имя пользователя (зарегестрированного) создавшего игру.
     */
    @RequestMapping("/games/becomeRegisteredGameCreator")///games/spy_get_users
    @ResponseBody
    @Throws(IOException::class)
    internal fun becomeRegisteredGameCreator(model: Model,
                                 @RequestParam("userName") userName: String = "",
                                 @RequestParam("sessionId") sessionId: String = "",
                                 @RequestParam("sessionPas") sessionPas: String = ""
    ): String {
        logger.info("becomeRegisteredGameCreator($userName, $sessionId, $sessionPas)")
        val auth: Authentication = SecurityContextHolder.getContext().authentication
        val name: String = auth.getName() //get logged in username

        return SpySessionManager.setRegisteredGameCreator(
                sessionId.toLong(), sessionPas.toLong(), userName, name).toString()
    }



   /****LOCATIONS******/

    @RequestMapping("/games_settings_spy_addLocation")
    @Throws(IOException::class)
    internal fun addLocation(model: Model, session: HttpSession,
                             @RequestParam("locationName") locationName: String = "",
                             @RequestParam("button") button: String = ""): String {
        val user: User = SecurityContextHolder
                .getContext()
                .authentication
                .principal as User
        val name: String = user.getUsername()
        var result = false
        if(button =="Add") {

            result = SpySessionManager.addLocation(locationName, name)
        }
        else if(button == "Delete")
        {
            result = SpySessionManager.deleteLocation(locationName, name)
        }
        val counter = CounterGamesSettings()
        if(!result) {
            counter.errorMessage = "Ошибка добавления/удаления локации";
        }
        val page = Page(counter)
        model.addAttribute("page", page)
        return "web/html/gamesSettings"
    }


    /**
     * Обновить список локаций в игре
     * @param useUserLocations нужно ли использовать локации пользователя.
     */
    @RequestMapping("/games/updateLocations")///games/spy_get_users
    @ResponseBody
    @Throws(IOException::class)
    internal fun updateLocations(model: Model,
                            @RequestParam("userName") userName: String = "",
                            @RequestParam("sessionId") sessionId: String = "",
                            @RequestParam("sessionPas") sessionPas: String = "",
                            @RequestParam("useUserLocations") useUserLocations: String = ""
                                 ): String {
        logger.info("countUsers($userName, $sessionId, $sessionPas, $useUserLocations)")
        return SpySessionManager.updateLocations(
                sessionId.toLong(), sessionPas.toLong(), userName, useUserLocations.toBoolean()).toString()
    }
    /**
     * Получить список локаций пользователя администратора игры.
     */
    @RequestMapping("/games/getUserLocations")
    @ResponseBody
    @Throws(IOException::class)
    internal fun getUserLocations(model: Model,
                                 @RequestParam("userName") userName: String = "",
                                 @RequestParam("sessionId") sessionId: String = "",
                                 @RequestParam("sessionPas") sessionPas: String = ""
    ): String {
        logger.info("countUsers($userName, $sessionId, $sessionPas)")
        return SpySessionManager.getUserLocations(
                sessionId.toLong(), sessionPas.toLong(), userName).toString()
    }


    /**
     * Получить список основных локаций (те что в оригинале)
     */
    @RequestMapping("/games/getMainLocations")
    @ResponseBody
    @Throws(IOException::class)
    internal fun getMainLocations(model: Model,
                                  @RequestParam("userName") userName: String = "",
                                  @RequestParam("sessionId") sessionId: String = "",
                                  @RequestParam("sessionPas") sessionPas: String = ""
    ): String {
        logger.info("countUsers($userName, $sessionId, $sessionPas)")
        return SpySessionManager.getMainLocations(
                sessionId.toLong(), sessionPas.toLong(), userName).toString()
    }



}