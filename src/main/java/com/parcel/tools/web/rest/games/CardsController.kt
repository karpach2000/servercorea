package com.parcel.tools.web.rest.games

import com.parcel.tools.constructor.Page
import com.parcel.tools.constructor.games.CounterGames
import com.parcel.tools.constructor.gamesSettings.CounterGamesSettings
import com.parcel.tools.cards.CardsSessionException
import com.parcel.tools.cards.CardsSessionManager
import com.parcel.tools.cards.CardsSessionManagerException
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
class CardsController {

    private val logger = org.apache.log4j.Logger.getLogger(CardsController::class.java)


    @RequestMapping("/games/cards_add_session")
    @ResponseBody
    @Throws(IOException::class)
    internal fun addSession(
                         @RequestParam("userName") userName: String = "",
                         @RequestParam("sessionId") sessionId: String = "",
                         @RequestParam("sessionPas") sessionPas: String = "",
                         @RequestParam("userCard") userCard: String = ""): String {
        logger.info("addSession($userName, $sessionId, $sessionPas, $userCard)")
        return CardsSessionManager.addSession(sessionId.toLong(), sessionPas.toLong()).toString()
    }

    @RequestMapping("/games/cards_addUser")
    @ResponseBody
    @Throws(IOException::class)
    internal fun addUser(
                         @RequestParam("userName") userName: String = "",
                         @RequestParam("sessionId") sessionId: String = "",
                         @RequestParam("sessionPas") sessionPas: String = "",
                         @RequestParam("userCard") userCard: String = ""): String {
        logger.info("addUser($userName, $sessionId, $sessionPas, $userCard)")
        return try {
            CardsSessionManager.addUser(sessionId.toLong(), sessionPas.toLong(), userName, userCard).toString()
        }
        catch(ex: CardsSessionManagerException) {
            ex.message!!
        }
        catch (ex: CardsSessionException){
            logger.warn(ex.message)
            ex.message!!
        }

    }

    @RequestMapping("/games/cards_start_game")
    @ResponseBody
    @Throws(IOException::class)
    internal fun startGame(
                           @RequestParam("userName") userName: String = "",
                           @RequestParam("sessionId") sessionId: String = "",
                           @RequestParam("sessionPas") sessionPas: String = "",
                           @RequestParam("userCard") userCard: String = ""): String {
        logger.info("startGame($userName, $sessionId, $sessionPas, $userCard)")
        CardsSessionManager.startGame(sessionId.toLong(), sessionPas.toLong())
        val userInformation =
                CardsSessionManager.getUserInformation(sessionId.toLong(), sessionPas.toLong(),userName,userCard)
        return userInformation.toString()
    }

    @RequestMapping("/games/cards_stop_game")
    @ResponseBody
    @Throws(IOException::class)
    internal fun stopGame(
                          @RequestParam("userName") userName: String = "",
                          @RequestParam("sessionId") sessionId: String = "",
                          @RequestParam("sessionPas") sessionPas: String = "",
                          @RequestParam("userCard") userCard: String = ""): String {
        logger.info("stopGame($userName, $sessionId, $sessionPas, $userCard)")
        CardsSessionManager.stopGame(sessionId.toLong(), sessionPas.toLong())
        return "true"
    }

    @RequestMapping("/games/cards_get_cards")
    @ResponseBody
    @Throws(IOException::class)
    internal fun getCards(
                        @RequestParam("userName") userName: String = "",
                        @RequestParam("sessionId") sessionId: String = "",
                        @RequestParam("sessionPas") sessionPas: String = "",
                        @RequestParam("userCard") userCard: String = ""): String {
        logger.info("getCards($userName, $sessionId, $sessionPas, $userCard)")
        try {
            return CardsSessionManager.getCards(sessionId.toLong(), sessionPas.toLong(), userName, userCard)
        }
        catch (ex: CardsSessionException)
        {
            logger.warn(ex.message)
            return ex.message!!
        }

    }


    @RequestMapping("/games/cards_get_users")///games/cards_get_users
    @ResponseBody
    @Throws(IOException::class)
    internal fun getUsers(
                             @RequestParam("userName") userName: String = "",
                             @RequestParam("sessionId") sessionId: String = "",
                             @RequestParam("sessionPas") sessionPas: String = "",
                             @RequestParam("userCard") userCard: String = ""): String {
        logger.info("getUsers($userName, $sessionId, $sessionPas, $userCard)")
        return CardsSessionManager.getUsers(sessionId.toLong(), sessionPas.toLong(), userName)
    }

    @RequestMapping("/games/cards_count_users")///games/cards_get_users
    @ResponseBody
    @Throws(IOException::class)
    internal fun countUsers(
                          @RequestParam("userName") userName: String = "",
                          @RequestParam("sessionId") sessionId: String = "",
                          @RequestParam("sessionPas") sessionPas: String = "",
                          @RequestParam("userCard") userCard: String = ""): String {
        logger.info("countUsers($userName, $sessionId, $sessionPas, $userCard)")
        return CardsSessionManager.countUsersInGame(sessionId.toLong(), sessionPas.toLong()).toString()
    }


}