package com.parcel.tools.web.rest.games

import com.parcel.tools.constructor.Page
import com.parcel.tools.constructor.bodies.games.CounterGames
import com.parcel.tools.games.GameSessionException
import com.parcel.tools.games.GameSessionManagerException
import com.parcel.tools.games.cards.CardsSessionManager
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

    @RequestMapping("/games/cards")
    @Throws(IOException::class)
    internal fun games(model: Model, session: HttpSession): String {
        val counter = CounterGames()
        val page = Page(counter)
        model.addAttribute("page", page)
        return "web/html/games/cards"
    }

    @RequestMapping("/games/cards_add_session")
    @ResponseBody
    @Throws(IOException::class)
    internal fun addSession(
            @RequestParam("sessionId") sessionId: String = "",
            @RequestParam("sessionPas") sessionPas: String = ""): String {
        logger.info("addCardsSession($sessionId, $sessionPas)")
        return CardsSessionManager.addSession(sessionId.toLong(), sessionPas.toLong()).toString()
    }

    @RequestMapping("/games/cards_addUser")
    @ResponseBody
    @Throws(IOException::class)
    internal fun addCardUser(
            @RequestParam("userName") userName: String = "",
            @RequestParam("userCard") userCard: String = "",
            @RequestParam("sessionId") sessionId: String = "",
            @RequestParam("sessionPas") sessionPas: String = ""): String {
        logger.info("addCardUser($userName, $userCard, $sessionId, $sessionPas)")
        return try {
            CardsSessionManager.addUser(sessionId.toLong(), sessionPas.toLong(), userName, userCard).toString()
        } catch (ex: GameSessionManagerException) {
            ex.message!!
        } catch (ex: GameSessionException) {
            logger.warn(ex.message)
            ex.message!!
        }

    }

    @RequestMapping("/games/cards_start_game")
    @ResponseBody
    @Throws(IOException::class)
    internal fun startGame(
            @RequestParam("userName") userName: String = "",
            @RequestParam("userCard") userCard: String = "",
            @RequestParam("sessionId") sessionId: String = "",
            @RequestParam("sessionPas") sessionPas: String = ""): String {
        logger.info("startGame($userName, $userCard, $sessionId, $sessionPas)")
        CardsSessionManager.startGame(sessionId.toLong(), sessionPas.toLong())
        val userInformation =
                CardsSessionManager.getUserInformation(sessionId.toLong(), sessionPas.toLong(), userName, userCard)
        return userInformation.toString()
    }

    @RequestMapping("/games/cards_stop_game")
    @ResponseBody
    @Throws(IOException::class)
    internal fun stopGame(
            @RequestParam("userName") userName: String = "",
            @RequestParam("userCard") userCard: String = "",
            @RequestParam("sessionId") sessionId: String = "",
            @RequestParam("sessionPas") sessionPas: String = ""): String {
        logger.info("stopGame($userName, $userCard, $sessionId, $sessionPas)")
        CardsSessionManager.stopGame(sessionId.toLong(), sessionPas.toLong())
        return userCard
    }

    /*  @RequestMapping("/games/cards_get_cards")
      @ResponseBody
      @Throws(IOException::class)
      internal fun getCards(
              @RequestParam("user") user: CardsUser = CardsUser(""),
              @RequestParam("sessionId") sessionId: String = "",
              @RequestParam("sessionPas") sessionPas: String = ""): CardsUser {
          logger.info("getCards($user, $sessionId, $sessionPas)")
          try {
              return CardsSessionManager.getCards(user, sessionId.toLong(), sessionPas.toLong())
          } catch (ex: CardsSessionException) {
              logger.warn(ex.message)
              return CardsUser(ex.message!!)
          }
      }*/


    @RequestMapping("/games/cards_get_users")///games/spy_get_users
    @ResponseBody
    @Throws(IOException::class)
    internal fun getUsers(
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
    internal fun countUsers(
            @RequestParam("userName") userName: String = "",
            // @RequestParam("userCard") userCard: String = "",
            @RequestParam("sessionId") sessionId: String = "",
            @RequestParam("sessionPas") sessionPas: String = ""): String {
        logger.info("countUsers($userName, $sessionId, $sessionPas)")
        return CardsSessionManager.countUsersInGame(sessionId.toLong(), sessionPas.toLong()).toString()
    }


}