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

    private val logger = org.apache.log4j.Logger.getLogger(MafiaSession::class.java!!)
    private var firstUserAdded = false

    private var mafiaSessionState = MafiaSessionState.FIRST_CONE





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
        return addUser(mu)
    }

    fun getCitizenVoteVariants(userName: String):String
    {
        logger.info("getCitizenVoteVariants($userName)")
        var ans = ""
        if(getUser(userName).role!=MafiaUserRoles.LEADING) {
            val SEPORATOR = "_"
            users.forEach {
                if (it.isAlife && it.role != MafiaUserRoles.LEADING) {
                    ans = ans + it.name + SEPORATOR
                }
            }
        }//if
        return ans
    }
    fun getMafiaVoteVariants(userName: String):String
    {
        logger.info("getMafiaVoteVariants($userName)")
        var ans = ""
        if(getUser(userName).role==MafiaUserRoles.MAFIA) {
            val SEPORATOR = "_"
            users.forEach {
                if (it.isAlife && it.role != MafiaUserRoles.LEADING) {
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

            val mafiaNames = generateMafia()
            mafiaNames.forEach { this.getUser(it).role = MafiaUserRoles.MAFIA }
            startGameEvent()

        }
    }



    /*******VOTE*******/


    fun getSitizenVoteTable(userName: String):String
    {
        logger.info("getSitizenVoteTable($userName)")
        return MafiaCitizenVoteInformation(getUser(userName), users).toHtml()
    }

    fun mafiaVote(userName: String, voteName: String):Boolean
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

    fun citizenVote(userName: String, voteName: String):Boolean
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

    private fun vote(userName: String, voteName: String) :Boolean
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
        return true
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

    fun chndgeLeaderEvent(leaderName: String)
    {
        gameEvent.forEach { it.leaderChandged(leaderName) }
    }


}