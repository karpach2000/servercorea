package com.parcel.tools.web.rest.games

import com.parcel.tools.constructor.Page
import com.parcel.tools.constructor.games.CounterGames
import com.parcel.tools.constructor.gamesSettings.CounterGamesSettings
import com.parcel.tools.games.cards.CardsSessionException
import com.parcel.tools.games.cards.CardsSessionManager
import com.parcel.tools.games.cards.CardsSessionManagerException
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

    @RequestMapping("/games/cards_add_session")
    @ResponseBody
    @Throws(IOException::class)
    internal fun addSession(model: Model,
                            @RequestParam("userName") userName: String = "",
                            @RequestParam("userCard") userCard: String = "",
                            @RequestParam("sessionId") sessionId: String = "",
                            @RequestParam("sessionPas") sessionPas: String = ""): String {
        logger.info("addSession($userName, $userCard, $sessionId, $sessionPas)")
        return CardsSessionManager.addSession(sessionId.toLong(), sessionPas.toLong()).toString()
    }

    @RequestMapping("/games/cards_addUser")
    @ResponseBody
    @Throws(IOException::class)
    internal fun addUser(model: Model,
                         @RequestParam("userName") userName: String = "",
                         @RequestParam("userCard") userCard: String = "",
                         @RequestParam("sessionId") sessionId: String = "",
                         @RequestParam("sessionPas") sessionPas: String = ""): String {
        logger.info("addUser($userName, $userCard, $sessionId, $sessionPas)")
        return try {
            CardsSessionManager.addUser(sessionId.toLong(), sessionPas.toLong(), userName).toString()
        } catch (ex: CardsSessionManagerException) {
            ex.message!!
        } catch (ex: CardsSessionException) {
            logger.warn(ex.message)
            ex.message!!
        }

    }

    @RequestMapping("/games/cards_start_game")
    @ResponseBody
    @Throws(IOException::class)
    internal fun startGame(model: Model,
                           @RequestParam("userName") userName: String = "",
                           @RequestParam("userCard") userCard: String = "",
                           @RequestParam("sessionId") sessionId: String = "",
                           @RequestParam("sessionPas") sessionPas: String = ""): String {
        logger.info("startGame($userName, $userCard, $sessionId, $sessionPas)")
        CardsSessionManager.startGame(sessionId.toLong(), sessionPas.toLong())
        val userInformation =
                CardsSessionManager.getUserInformation(sessionId.toLong(), sessionPas.toLong(), userName)
        return userInformation.toString()
    }

    @RequestMapping("/games/cards_stop_game")
    @ResponseBody
    @Throws(IOException::class)
    internal fun stopGame(model: Model,
                          @RequestParam("userName") userName: String = "",
                          @RequestParam("userCard") userCard: String = "",
                          @RequestParam("sessionId") sessionId: String = "",
                          @RequestParam("sessionPas") sessionPas: String = ""): String {
        logger.info("stopGame($userName, $userCard, $sessionId, $sessionPas)")
        CardsSessionManager.stopGame(sessionId.toLong(), sessionPas.toLong())
        return "true"
    }

    @RequestMapping("/games/cards_get_cards")
    @ResponseBody
    @Throws(IOException::class)
    internal fun getCards(model: Model,
                        @RequestParam("userName") userName: String = "",
                        @RequestParam("userCard") userCard: String = "",
                        @RequestParam("sessionId") sessionId: String = "",
                        @RequestParam("sessionPas") sessionPas: String = ""): String {
        logger.info("getCards($userName, $userCard, $sessionId, $sessionPas)")
        try {
            return CardsSessionManager.getCards(sessionId.toLong(), sessionPas.toLong(), userName)
        } catch (ex: CardsSessionException) {
            logger.warn(ex.message)
            return ex.message!!
        }

    }


    @RequestMapping("/games/cards_get_users")///games/spy_get_users
    @ResponseBody
    @Throws(IOException::class)
    internal fun getUsers(model: Model,
                          @RequestParam("userName") userName: String = "",
                          @RequestParam("userCard") userCard: String = "",
                          @RequestParam("sessionId") sessionId: String = "",
                          @RequestParam("sessionPas") sessionPas: String = ""): String {
        logger.info("getUsers($userName, $userCard, $sessionId, $sessionPas)")
        return CardsSessionManager.getUsers(sessionId.toLong(), sessionPas.toLong())
    }

    @RequestMapping("/games/cards_count_users")///games/spy_get_users
    @ResponseBody
    @Throws(IOException::class)
    internal fun countUsers(model: Model,
                            @RequestParam("userName") userName: String = "",
                            @RequestParam("userCard") userCard: String = "",
                            @RequestParam("sessionId") sessionId: String = "",
                            @RequestParam("sessionPas") sessionPas: String = ""): String {
        logger.info("countUsers($userName, $userCard, $sessionId, $sessionPas)")
        return CardsSessionManager.countUsersInGame(sessionId.toLong(), sessionPas.toLong()).toString()
    }


}