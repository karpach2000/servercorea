package com.parcel.tools.web.rest.games

import com.parcel.tools.constructor.Page
import com.parcel.tools.constructor.games.CounterGames
import com.parcel.tools.constructor.gamesSettings.CounterGamesSettings
import com.parcel.tools.spy.SpySessionException
import com.parcel.tools.spy.SpySessionManager
import com.parcel.tools.spy.SpySessionManagerException
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
class GamesController {

    private val logger = org.apache.log4j.Logger.getLogger(GamesController::class.java!!)

    @RequestMapping("/games")
    @Throws(IOException::class)
    internal fun games(model: Model, session: HttpSession): String {
        val counter = CounterGames()
        val page = Page(counter)
        model.addAttribute("page", page)
        return "web/html/games"
    }

    @RequestMapping("/games_settings")
    @Throws(IOException::class)
    internal fun gamesSettings(model: Model, session: HttpSession): String {
        val counter = CounterGamesSettings()
        val page = Page(counter)
        model.addAttribute("page", page)
        return "web/html/gamesSettings"
    }

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
        catch(ex: SpySessionManagerException) {
            ex.message!!
        }
        catch (ex: SpySessionException){
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
        logger.info("startGame($userName, $sessionId, $sessionPas)")
        SpySessionManager.startGame(sessionId.toLong(), sessionPas.toLong())
        val userInformation =
                SpySessionManager.getUserInformation(sessionId.toLong(), sessionPas.toLong(),userName)
        return userInformation.toString()
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

    /*
    @RequestMapping("/games/spy_is_spy_showen")///games/spy_get_users
    @ResponseBody
    @Throws(IOException::class)
    internal fun isSpyShowen(model: Model,
                             @RequestParam("userName") userName: String = "",
                             @RequestParam("sessionId") sessionId: String = "",
                             @RequestParam("sessionPas") sessionPas: String = ""): String {
        logger.info("isSpyShowen($userName, $sessionId, $sessionPas)")
        return SpySessionManager.isSpyUncovered(sessionId.toLong(), sessionPas.toLong()).toString()
    }
    */
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


}