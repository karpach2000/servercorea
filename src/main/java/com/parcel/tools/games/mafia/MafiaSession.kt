package com.parcel.tools.games.mafia

import com.parcel.tools.games.GamesSession
import com.parcel.tools.games.GlobalRandomiser
import com.parcel.tools.games.mafia.voteinformation.MafiaCitizenVoteInformation
import java.lang.Exception


class MafiaSessionException(message: String): Exception(message)
class MafiaSession(sessionId: Long, sessionPas: Long) :  GamesSession<MafiaUser, MafiaEvent>(sessionId, sessionPas) {

    private enum class MafiaSessionState
    {
        ADD_USERS,
        CITIZEN_VOTE,
        MAFIA_VOTE,
    }

    private val logger = org.apache.log4j.Logger.getLogger(MafiaSession::class.java!!)
    private var firstUserAdded = false

    private var mafiaSessionState = MafiaSessionState.ADD_USERS





    fun getLeader():String
    {
        logger.info("getLeader()")
        users.forEach {
            if(it.role == MafiaUserRoles.LEADING) {
                return it.name
            }
        }
        return ""
    }

    fun becomeLeader(name: String):Boolean
    {
        logger.info("becomeLeader($name)")
        if(!started)
        {
            users.forEach {
                if(it.role == MafiaUserRoles.LEADING) {
                    it.role = MafiaUserRoles.CITIZEN
                }
            }
            getUser(name).role = MafiaUserRoles.LEADING
            chndgeLeaderEvent(name)
            return true
        }
        throw MafiaSessionException("Can`t change leader. Game started.")
    }
    /*******USERS******/
    fun getRole(userName: String): String
    {
        logger.info("getRole($userName)")
        return getUser(userName).role.toString()
    }

    override fun addUser(name: String) :Boolean {
        val mu = MafiaUser(name)
        if(!firstUserAdded)
        {
            logger.info("User '$name' is leader.")
            firstUserAdded = true
            mu.role = MafiaUserRoles.LEADING
        }
        else if(started)
            updateVoteTableEvent()//что бы при переподключении обновлялись данные
        return addUser(mu)
    }


    /**
     * Получить варианты за кого можно проголосовать когда голосует город.
     * (список кандидатов)
     */
    fun getUsersForVoteСitizen(userName: String):String
    {
        logger.info("getUsersForVoteСitizen($userName)")
        var ans = ""
        if(getUser(userName).role!=MafiaUserRoles.LEADING) {
            val SEPORATOR = "_"
            users.forEach {
                if (it.isAlife && it.role!=MafiaUserRoles.LEADING) {
                    ans = ans + it.name + SEPORATOR
                }
            }
        }//if
        return ans
    }
    /**
     * Получить варианты за кого можно проголосовать когда голосует мафия.
     * (список кандидатов)
     */
    fun getUsersForVoteMafia(userName: String):String
    {
        logger.info("getUsersForVoteMafia($userName)")
        var ans = ""
        if(getUser(userName).role==MafiaUserRoles.MAFIA) {
            val SEPORATOR = "_"
            users.forEach {
                if (it.isAlife && it.role!=MafiaUserRoles.LEADING) {
                    ans = ans + it.name + SEPORATOR
                }
            }
        }//if
        return ans
    }


    /*******GAME******/

    override fun startGame()
    {
        if (!started) {
            started = true
            logger.info("startGame()...")
            mafiaSessionState = MafiaSessionState.CITIZEN_VOTE
            val mafiaNames = generateMafia()
            mafiaNames.forEach { this.getUser(it).role = MafiaUserRoles.MAFIA }
            startGameEvent()
            updateVoteTableEvent()
        }
    }

    fun getGameState(userName: String):String
    {
        logger.info("getGameState($userName)")
        return this.mafiaSessionState.toString()
    }



    /*******VOTE*******/


    fun getSitizenVoteTable(userName: String):String
    {
        logger.info("getSitizenVoteTable($userName)")
        return MafiaCitizenVoteInformation(getUser(userName), users).toHtml()
    }


   fun vote(userName: String, voteName: String) :Boolean
    {
        logger.info("vote($userName, $voteName)")
        getUser(userName).voteName = voteName
        users.forEach { it.votedCount = 0 }
        users.forEach {
            val voteName = it.voteName
            if(voteName!="")
            {
                getUser(voteName).votedCount ++
            }
        }
        //обновляем таблицы голосования
        updateVoteTableEvent()
        return true
    }

    /**
     * Завершить голосование и применить результаты.
     */
    fun mafiaVoteResult(userName: String):String
    {
        logger.info("mafiaVoteResult($userName)")
        if(getUser(userName).role!=MafiaUserRoles.LEADING)
        {
            throw MafiaSessionException("Only leader can end vote.")
        }
        mafiaSessionState = MafiaSessionState.CITIZEN_VOTE
        val result = getVoteResult()
        logger.info("voteResult: $result")
        if(result!="")
            getUser(result).isAlife = false
        openСitizensVoteCountEvent(result)
        //обновляем таблицы голосования
        updateVoteTableEvent()
        return result
    }

    /**
     * Завершить голосование и применить результаты.
     */
    fun cityzenVoteResult(userName: String):String
    {
        logger.info("cityzenVoteResult($userName)")
        if(getUser(userName).role!=MafiaUserRoles.LEADING)
        {
            throw MafiaSessionException("Only leader can end vote.")
        }
        mafiaSessionState = MafiaSessionState.MAFIA_VOTE
        val result = getVoteResult()
        logger.info("voteResult: $result")

        if(result!="")
            getUser(result).isAlife = false

        openMafiaVoteCountEvent(result)
        //обновляем таблицы голосования
        updateVoteTableEvent()
        return result
    }


    /**
     * Stope vote and get vote result
     */
    private fun getVoteResult():String
    {
        var votesMax = 0
        var maxVoteUser = ""
        users.forEach {
            if(it.votedCount>votesMax)
            {
                votesMax = it.votedCount
                maxVoteUser = it.name
            }
            it.clearVote()
        }

        return maxVoteUser

    }




    private fun generateMafia():ArrayList<String>
    {
        val mafiaNames = ArrayList<String>()
        val users = ArrayList<MafiaUser>()
        this.users.forEach { if(it.role != MafiaUserRoles.LEADING)users.add(it) }
        var mafiaCount = countMafiaGamersIsNecessary()
        while(mafiaCount>0)
        {
            val i = GlobalRandomiser.getRundom(users.size)
            mafiaNames.add(users[i].name)
            users.removeAt(i)
            mafiaCount --
        }
        return mafiaNames
    }

    private fun countMafiaGamersIsNecessary(): Int
    {
        return (users.count()-1)/3
    }


    /*******EVENTS*******/
    fun updateVoteTableEvent()
    {
        gameEvent.forEach {
            val table = MafiaCitizenVoteInformation(getUser(it.userName), users).toHtml()
            it.updateVoteTable(table)
        }
    }

    fun openMafiaVoteCountEvent(deadUser: String)
    {
        gameEvent.forEach { it.openMafiaVote(deadUser) }
    }

    fun openСitizensVoteCountEvent(deadUser: String)
    {
        gameEvent.forEach { it.openСitizensVote(deadUser)}
    }

    fun chndgeLeaderEvent(leaderName: String)
    {
        gameEvent.forEach { it.leaderChandged(leaderName) }
    }


}