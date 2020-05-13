package com.parcel.tools.web.rest.games

import com.parcel.tools.constructor.Page
import com.parcel.tools.constructor.games.CounterGames
import com.parcel.tools.games.GameSessionException
import com.parcel.tools.games.GameSessionManagerException
import com.parcel.tools.games.mafia.MafiaSessionException
import com.parcel.tools.games.mafia.MafiaSessionManager
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import java.io.IOException
import javax.servlet.http.HttpSession

@Controller
class MafiaController {

    private val logger = org.apache.log4j.Logger.getLogger(MafiaController::class.java!!)


    @RequestMapping("/games/mafia")
    @Throws(IOException::class)
    internal fun games(model: Model, session: HttpSession): String {
        val counter = CounterGames()
        val page = Page(counter)
        model.addAttribute("page", page)
        return "web/html/games/mafia"
    }


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
        logger.info("getRole($userName, $sessionId, $sessionPas)")
        return MafiaSessionManager.getRole(sessionId.toLong(), sessionPas.toLong(), userName)
    }

    @RequestMapping("/games/mafia_getGameState")///games/spy_get_users
    @ResponseBody
    @Throws(IOException::class)
    internal fun getGameState(model: Model,
                         @RequestParam("userName") userName: String = "",
                         @RequestParam("sessionId") sessionId: String = "",
                         @RequestParam("sessionPas") sessionPas: String = ""): String {
        logger.info("getRole($userName, $sessionId, $sessionPas)")
        return MafiaSessionManager.getGameState(sessionId.toLong(), sessionPas.toLong(), userName)
    }



    @RequestMapping("/games/mafia_stopGame")///games/spy_get_users
    @ResponseBody
    @Throws(IOException::class)
    internal fun stopGame(model: Model,
                         @RequestParam("userName") userName: String = "",
                         @RequestParam("sessionId") sessionId: String = "",
                         @RequestParam("sessionPas") sessionPas: String = ""): String {
        logger.info("getRole($userName, $sessionId, $sessionPas)")
        return MafiaSessionManager.stopGame(sessionId.toLong(), sessionPas.toLong()).toString()
    }

    @RequestMapping("/games/mafia_getCitizenVoteResult")///games/spy_get_users
    @ResponseBody
    @Throws(IOException::class)
    internal fun getCitizenVoteVariants(model: Model,
                         @RequestParam("userName") userName: String = "",
                         @RequestParam("sessionId") sessionId: String = "",
                         @RequestParam("sessionPas") sessionPas: String = ""): String {
        logger.info("getCitizenVoteVariants($userName, $sessionId, $sessionPas)")
        return MafiaSessionManager.cityzenVoteResult(sessionId.toLong(), sessionPas.toLong(), userName)
    }

    @RequestMapping("/games/mafia_getMafiaVoteResult")///games/spy_get_users
    @ResponseBody
    @Throws(IOException::class)
    internal fun getMafiaVoteVariants(model: Model,
                                        @RequestParam("userName") userName: String = "",
                                        @RequestParam("sessionId") sessionId: String = "",
                                        @RequestParam("sessionPas") sessionPas: String = ""): String {
        logger.info("getMafiaVoteVariants($userName, $sessionId, $sessionPas)")
        return MafiaSessionManager.mafiaVoteResult(sessionId.toLong(), sessionPas.toLong(), userName)
    }

    @RequestMapping("/games/mafia_getUsersForVoteСitizen")
    @ResponseBody
    @Throws(IOException::class)
    internal fun getUsersForVoteСitizen(model: Model,
                                      @RequestParam("userName") userName: String = "",
                                      @RequestParam("sessionId") sessionId: String = "",
                                      @RequestParam("sessionPas") sessionPas: String = ""): String {
        logger.info("getUsersForVoteСitizen($userName, $sessionId, $sessionPas)")
        return MafiaSessionManager.getUsersForVoteСitizen(sessionId.toLong(), sessionPas.toLong(), userName)
    }

    @RequestMapping("/games/mafia_getUsersForVoteMafia")
    @ResponseBody
    @Throws(IOException::class)
    internal fun getUsersForVoteMafia(model: Model,
                                        @RequestParam("userName") userName: String = "",
                                        @RequestParam("sessionId") sessionId: String = "",
                                        @RequestParam("sessionPas") sessionPas: String = ""): String {
        logger.info("getUsersForVoteСitizen($userName, $sessionId, $sessionPas)")
        return MafiaSessionManager.getUsersForVoteMafia(sessionId.toLong(), sessionPas.toLong(), userName)
    }



    /**
     * Отдать голос.
     */
    @RequestMapping("/games/mafia_voteVote")///games/spy_get_users
    @ResponseBody
    @Throws(IOException::class)
    internal fun vote(model: Model,
                                      @RequestParam("userName") userName: String = "",
                                      @RequestParam("sessionId") sessionId: String = "",
                                      @RequestParam("sessionPas") sessionPas: String = "",
                                      @RequestParam("vote") voteName: String = ""
    ): String {
        logger.info("vote($userName, $sessionId, $sessionPas)")
        return MafiaSessionManager.vote(sessionId.toLong(), sessionPas.toLong(), userName, voteName).toString()
    }


    /**
     * Шериф проверяет игрока.
     */
    @RequestMapping("/games/mafia_checkUserSheriff")
    @ResponseBody
    @Throws(IOException::class)
    internal fun checkUserSheriff(model: Model,
                      @RequestParam("userName") userName: String = "",
                      @RequestParam("sessionId") sessionId: String = "",
                      @RequestParam("sessionPas") sessionPas: String = "",
                      @RequestParam("checkedUserName") checkedUserName: String = ""
    ): String {
        logger.info("checkUserSheriff($userName, $sessionId, $sessionPas, $checkedUserName)")
        return MafiaSessionManager.checkUserSheriff(sessionId.toLong(),
                sessionPas.toLong(), userName, checkedUserName).toString()
    }


    /**
     * Получить список игроков которых может проверить шериф.
     */
    @RequestMapping("/games/mafia_getCheckUserSheriffVariants")
    @ResponseBody
    @Throws(IOException::class)
    internal fun getCheckUserSheriffVariants(model: Model,
                                  @RequestParam("userName") userName: String = "",
                                  @RequestParam("sessionId") sessionId: String = "",
                                  @RequestParam("sessionPas") sessionPas: String = ""
    ): String {
        logger.info("getCheckUserSheriffVariants($userName, $sessionId, $sessionPas)")
        return MafiaSessionManager.getCheckUserSheriffVariants(sessionId.toLong(),
                sessionPas.toLong(), userName).toString()
    }

}