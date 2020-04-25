package com.parcel.tools.web.rest.games

import com.parcel.tools.games.GameSessionException
import com.parcel.tools.games.GameSessionManagerException
import com.parcel.tools.games.mafia.MafiaSessionException
import com.parcel.tools.games.mafia.MafiaSessionManager
import com.parcel.tools.games.spy.SpySessionException
import com.parcel.tools.games.spy.SpySessionManager
import com.parcel.tools.games.spy.SpySessionManagerException
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import java.io.IOException

@Controller
class MafiaController {

    private val logger = org.apache.log4j.Logger.getLogger(MafiaController::class.java!!)

    @RequestMapping("/games/mafia_add_session")
    @ResponseBody
    @Throws(IOException::class)
    internal fun addSession(model: Model,
                            @RequestParam("userName") userName: String = "",
                            @RequestParam("sessionId") sessionId: String = "",
                            @RequestParam("sessionPas") sessionPas: String = ""): String {
        logger.info("addSession($userName, $sessionId, $sessionPas)")
        return MafiaSessionManager.addSession(sessionId.toLong(), sessionPas.toLong()).toString()
    }

    @RequestMapping("/games/mafia_addUser")
    @ResponseBody
    @Throws(IOException::class)
    internal fun addUser(model: Model,
                         @RequestParam("userName") userName: String = "",
                         @RequestParam("sessionId") sessionId: String = "",
                         @RequestParam("sessionPas") sessionPas: String = ""): String {
        logger.info("addUser($userName, $sessionId, $sessionPas)")
        return try {
            MafiaSessionManager.addUser(sessionId.toLong(), sessionPas.toLong(), userName).toString()
        }
        catch (ex: GameSessionException){
            logger.error(ex.message)
            ex.message!!
        }
        catch (ex: GameSessionManagerException){
            logger.error(ex.message)
            ex.message!!
        }

    }


    @RequestMapping("/games/mafia_startGame")
    @ResponseBody
    @Throws(IOException::class)
    internal fun startGame(model: Model,
                           @RequestParam("userName") userName: String = "",
                           @RequestParam("sessionId") sessionId: String = "",
                           @RequestParam("sessionPas") sessionPas: String = ""): String {
        logger.info("startGame($userName, $sessionId, $sessionPas)")
        MafiaSessionManager.startGame(sessionId.toLong(), sessionPas.toLong())
        val userInformation =
                MafiaSessionManager.getSitizenVoteTable(sessionId.toLong(), sessionPas.toLong(),userName)
        return userInformation.toString()
    }

    @RequestMapping("/games/mafia_count_users")///games/spy_get_users
    @ResponseBody
    @Throws(IOException::class)
    internal fun countUsers(model: Model,
                            @RequestParam("userName") userName: String = "",
                            @RequestParam("sessionId") sessionId: String = "",
                            @RequestParam("sessionPas") sessionPas: String = ""): String {
        logger.info("countUsers($userName, $sessionId, $sessionPas)")
        return MafiaSessionManager.countUsersInGame(sessionId.toLong(), sessionPas.toLong()).toString()
    }


    @RequestMapping("/games/mafia_getLeader")///games/spy_get_users
    @ResponseBody
    @Throws(IOException::class)
    internal fun getLeader(model: Model,
                            @RequestParam("userName") userName: String = "",
                            @RequestParam("sessionId") sessionId: String = "",
                            @RequestParam("sessionPas") sessionPas: String = ""): String {
        logger.info("countUsers($userName, $sessionId, $sessionPas)")
        return MafiaSessionManager.getLeader(sessionId.toLong(), sessionPas.toLong(), userName).toString()
    }

    @RequestMapping("/games/mafia_becomeALeader")///games/spy_get_users
    @ResponseBody
    @Throws(IOException::class)
    internal fun becomeALeader(model: Model,
                           @RequestParam("userName") userName: String = "",
                           @RequestParam("sessionId") sessionId: String = "",
                           @RequestParam("sessionPas") sessionPas: String = ""): String {
        logger.info("countUsers($userName, $sessionId, $sessionPas)")
        try{
            MafiaSessionManager.becomeLeader(sessionId.toLong(), sessionPas.toLong(), userName)
            return "true"
        }
        catch (ex:MafiaSessionException)
        {
            logger.error(ex.message)
            return ex.message.toString()
        }
        catch (ex: GameSessionException)
        {
            logger.error(ex.message)
            return ex.message.toString()
        }

    }

    @RequestMapping("/games/mafia_getUsers")///games/spy_get_users
    @ResponseBody
    @Throws(IOException::class)
    internal fun getUsers(model: Model,
                            @RequestParam("userName") userName: String = "",
                            @RequestParam("sessionId") sessionId: String = "",
                            @RequestParam("sessionPas") sessionPas: String = ""): String {
        logger.info("getUsers($userName, $sessionId, $sessionPas)")
        return MafiaSessionManager.getUsers(sessionId.toLong(), sessionPas.toLong()).toString()
    }

    @RequestMapping("/games/mafia_getRole")///games/spy_get_users
    @ResponseBody
    @Throws(IOException::class)
    internal fun getRole(model: Model,
                          @RequestParam("userName") userName: String = "",
                          @RequestParam("sessionId") sessionId: String = "",
                          @RequestParam("sessionPas") sessionPas: String = ""): String {
        logger.info("getUsers($userName, $sessionId, $sessionPas)")
        return MafiaSessionManager.getRole(sessionId.toLong(), sessionPas.toLong(), userName)
    }

    @RequestMapping("/games/mafia_getCitizenVoteVariants")///games/spy_get_users
    @ResponseBody
    @Throws(IOException::class)
    internal fun getCitizenVoteVariants(model: Model,
                         @RequestParam("userName") userName: String = "",
                         @RequestParam("sessionId") sessionId: String = "",
                         @RequestParam("sessionPas") sessionPas: String = ""): String {
        logger.info("getUsers($userName, $sessionId, $sessionPas)")
        return MafiaSessionManager.getCitizenVoteVariants(sessionId.toLong(), sessionPas.toLong(), userName)
    }

    @RequestMapping("/games/mafia_getMafiaVoteVariants")///games/spy_get_users
    @ResponseBody
    @Throws(IOException::class)
    internal fun getMafiaVoteVariants(model: Model,
                                        @RequestParam("userName") userName: String = "",
                                        @RequestParam("sessionId") sessionId: String = "",
                                        @RequestParam("sessionPas") sessionPas: String = ""): String {
        logger.info("getUsers($userName, $sessionId, $sessionPas)")
        return MafiaSessionManager.getMafiaVoteVariants(sessionId.toLong(), sessionPas.toLong(), userName)
    }

}