package com.parcel.tools.games.mafia

import com.parcel.tools.games.GamesSession
import com.parcel.tools.games.GlobalRandomiser
import com.parcel.tools.games.mafia.voteinformation.MafiaCitizenVoteInformation
import com.parcel.tools.games.spy.SpySession
import java.lang.Exception


class MafiaSessionException(message: String): Exception(message)
class MafiaSession(sessionId: Long, sessionPas: Long) :  GamesSession<MafiaUser, MafiaEvent>(sessionId, sessionPas) {

    private enum class MafiaSessionState
    {
        MAFIA_VOTE,
        CITIZEN_VOTE,
        FIRST_CONE
    }

    private val logger = org.apache.log4j.Logger.getLogger(SpySession::class.java!!)
    private var firstUserAdded = false

    private var mafiaSessionState = MafiaSessionState.FIRST_CONE








    override fun addUser(name: String) :Boolean {
        val mu = MafiaUser(name)
        if(!firstUserAdded)
        {
            logger.info("User '$name' is leader.")
            firstUserAdded = true
            mu.role = MafiaUserRoles.LEADING
        }
        return addUser(mu)
    }




    override fun startGame()
    {
        if (!started) {
            started = true
            logger.info("startGame()...")

            val mafiaNames = generateMafia()
            mafiaNames.forEach { this.getUser(it).role = MafiaUserRoles.MAFIA }
            startGameEvent()

        }
    }

    /**
     * Получить информацию отображаемую пользователю во время игры.
     */
    fun getUserInformation(userName: String): String
    {
        logger.info("getUserInformation($userName)")
        return MafiaCitizenVoteInformation(getUser(userName), users).toHtml()

    }

    /*******VOTE*******/

    fun startMafiaVote()
    {
        logger.info("startMafiaVote()")
        this.mafiaSessionState = MafiaSessionState.MAFIA_VOTE
        startMafiaVoteEvent()
    }

    fun mafiaVote(userName: String, voteName: String)
    {
        logger.info("mafiaVote($userName, $voteName)")
        if(getUser(userName).role!=MafiaUserRoles.MAFIA)
        {
            throw MafiaSessionException("Only mafia can vote on mafia vote.")
        }
        else if(getUser(voteName).role==MafiaUserRoles.LEADING )
        {
            throw MafiaSessionException("Mafia can vote leader.")
        }
        return vote(userName, voteName)
    }

    fun citizenVote(userName: String, voteName: String)
    {
        logger.info("citizenVote($userName, $voteName)")
        if(getUser(userName).role==MafiaUserRoles.LEADING)
        {
            throw MafiaSessionException("Leader can't wote.")
        }
        else if(getUser(voteName).role==MafiaUserRoles.LEADING )
        {
            throw MafiaSessionException("Can vote leader.")
        }
        return vote(userName, voteName)
    }

    private fun vote(userName: String, voteName: String)
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
    }

    fun mafiaVoteResult(userName: String):String
    {
        logger.info("mafiaVoteResult($userName)")
        if(getUser(userName).role!=MafiaUserRoles.LEADING)
        {
            throw MafiaSessionException("Only leader can end vote.")
        }
        val result = getVoteResult()
        logger.info("voteResult: $result")
        getUser(result).isAlife = false
        this.mafiaSessionState = MafiaSessionState.CITIZEN_VOTE
        openMafiaVoteEvent()
        return result
    }

    fun cityzenVoteResult(userName: String):String
    {
        logger.info("cityzenVoteResult($userName)")
        if(getUser(userName).role!=MafiaUserRoles.LEADING)
        {
            throw MafiaSessionException("Only leader can end vote.")
        }
        val result = getVoteResult()
        logger.info("voteResult: $result")
        getUser(result).isAlife = false
        this.mafiaSessionState = MafiaSessionState.MAFIA_VOTE
        openMafiaVoteEvent()
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
                it.clearVote()
            }
        }
        if(maxVoteUser!="")
            return maxVoteUser
        else
            throw MafiaSessionException("No one users has votes.")
    }

    fun getRole(userName: String): String
    {
        logger.info("getRole($userName)")
        return getUser(userName).role.toString()
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
        return users.count()/4
    }


    /*******EVENTS*******/
    fun startMafiaVoteEvent()
    {
        gameEvent.forEach { it.openMafiaVote() }
    }

    fun openMafiaVoteEvent()
    {
        gameEvent.forEach { it.openMafiaVote() }
    }

    fun openСitizensVoteEvent()
    {
        gameEvent.forEach { it.openСitizensVote()}
    }


}