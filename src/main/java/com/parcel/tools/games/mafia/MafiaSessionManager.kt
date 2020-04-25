package com.parcel.tools.games.mafia

import com.parcel.tools.games.GamesSessionManager
import com.parcel.tools.games.spy.SpyEvent
import com.parcel.tools.games.spy.SpySession
import com.parcel.tools.games.spy.SpySessionManager
import com.parcel.tools.games.spy.SpyUser

object MafiaSessionManager :
        GamesSessionManager<MafiaUser, MafiaEvent, MafiaSession>(){

    private val logger = org.apache.log4j.Logger.getLogger(MafiaSessionManager::class.java!!)



    fun getLeader(sessionId: Long, sessionPas: Long, userName: String): String
    {
        logger.info("getLeader($sessionId, $sessionPas , $userName)")
        return getSession(sessionId, sessionPas).getLeader()
    }

    fun becomeLeader(sessionId: Long, sessionPas: Long, userName: String):Boolean
    {
        logger.info("becomeLeader($sessionId, $sessionPas, $userName)")
        return getSession(sessionId, sessionPas).becomeLeader(userName)
    }
    fun getRole(sessionId: Long, sessionPas: Long, userName: String):String
    {
        logger.info("getRole($sessionId, $sessionPas, $userName)")
        return getSession(sessionId, sessionPas).getRole(userName)
    }

    fun getCitizenVoteVariants(sessionId: Long, sessionPas: Long, userName: String):String
    {
        logger.info("getCitizenVoteVariants($sessionId, $sessionPas, $userName)")
        return getSession(sessionId, sessionPas).getCitizenVoteVariants(userName)
    }
    fun getMafiaVoteVariants(sessionId: Long, sessionPas: Long, userName: String):String
    {
        logger.info("getMafiaVoteVariants($sessionId, $sessionPas, $userName)")
        return getSession(sessionId, sessionPas).getMafiaVoteVariants(userName)
    }


    /*******VOTE*******/

    fun vote(sessionId: Long, sessionPas: Long, userName: String, voteName: String): Boolean
    {
        logger.info("mafiaVote($sessionId, $sessionPas, $userName)")
        return getSession(sessionId, sessionPas).vote(userName, voteName)
    }


    fun mafiaVoteResult(sessionId: Long, sessionPas: Long, userName: String): String
    {
        logger.info("mafiaVoteResult($sessionId, $sessionPas, $userName)")
        return getSession(sessionId, sessionPas).mafiaVoteResult(userName)
    }

    fun cityzenVoteResult(sessionId: Long, sessionPas: Long, userName: String): String
    {
        logger.info("cityzenVoteResult($sessionId, $sessionPas, $userName)")
        return getSession(sessionId, sessionPas).cityzenVoteResult(userName)
    }

    fun getSitizenVoteTable(sessionId: Long, sessionPas: Long, userName: String): String
    {
        logger.info("getSitizenVoteTable($sessionId, $sessionPas, $userName)")
        return getSession(sessionId, sessionPas).getSitizenVoteTable(userName)
    }


    /*********УПРАВЛЕНИЕ СЕССИЯМИ**********/

    override fun addSession(sessionId: Long, sessionPas: Long)
            = addSession(MafiaSession(sessionId, sessionPas))
}