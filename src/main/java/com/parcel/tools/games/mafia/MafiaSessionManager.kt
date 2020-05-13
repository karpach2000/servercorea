package com.parcel.tools.games.mafia

import com.parcel.tools.games.GameSessionManagerException
import com.parcel.tools.games.GamesSessionManager

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

    fun getGameState(sessionId: Long, sessionPas: Long, userName: String):String
    {
        logger.info("getGameState($sessionId, $sessionPas, $userName)")
        try {
            return getSession(sessionId, sessionPas).getGameState(userName)
        }
        catch (ex: GameSessionManagerException)
        {
            return ex.message!!
        }

    }



    /*******VOTE*******/

    /**
     * Получить варианты за кого можно проголосовать когда голосует город.
     * (список кандидатов)
     */
    fun getUsersForVoteСitizen(sessionId: Long, sessionPas: Long, userName: String):String
    {
        logger.info("getUsersForVoteСitizen($sessionId, $sessionPas, $userName)")
        return getSession(sessionId, sessionPas).getUsersForVoteСitizen(userName)
    }

    /**
     * Получить варианты за кого можно проголосовать когда голосует мафия.
     * (список кандидатов)
     */
    fun getUsersForVoteMafia(sessionId: Long, sessionPas: Long, userName: String):String
    {
        logger.info("getUsersForVoteСitizen($sessionId, $sessionPas, $userName)")
        return getSession(sessionId, sessionPas).getUsersForVoteMafia(userName)
    }

    fun vote(sessionId: Long, sessionPas: Long, userName: String, voteName: String): Boolean
    {
        logger.info("mafiaVote($sessionId, $sessionPas, $userName)")
        return getSession(sessionId, sessionPas).vote(userName, voteName)
    }

    /**
     * Завершить голосование и применить результаты.
     */
    fun mafiaVoteResult(sessionId: Long, sessionPas: Long, userName: String): String
    {
        logger.info("mafiaVoteResult($sessionId, $sessionPas, $userName)")
        return getSession(sessionId, sessionPas).mafiaVoteResult(userName)
    }
    /**
     * Завершить голосование и применить результаты.
     */
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




    /******SPESHIAL*******/
    /**
     * Проверить является ли игрок мафией.
     * Работает если проверяющий имеет роль шерифа.
     * Метод передает в объект MafiaUser(userName) (если он шериф) информацию  о том пользователе
     *      которого он проверил.
     * Метод возвращает роль проверяемого пользователя.
     */
    fun checkUserSheriff(sessionId: Long, sessionPas: Long,userName: String, checkedUserName: String): String
    {
        logger.info("checkUserSheriff($sessionId, $sessionPas $userName, $checkedUserName)")
        return getSession(sessionId, sessionPas).checkUserSheriff(userName, checkedUserName)
    }

    /**
     * Получить список игроков которых может проверить шериф.
     */
    fun getCheckUserSheriffVariants(sessionId: Long, sessionPas: Long, userName: String): String {
        logger.info("getCheckUserSheriffVariants($sessionId, $sessionPas, $userName)")
        return getSession(sessionId, sessionPas).getCheckUserSheriffVariants(userName)
    }

    /*********УПРАВЛЕНИЕ СЕССИЯМИ**********/

    override fun addSession(sessionId: Long, sessionPas: Long)
            = addSession(MafiaSession(sessionId, sessionPas))
}