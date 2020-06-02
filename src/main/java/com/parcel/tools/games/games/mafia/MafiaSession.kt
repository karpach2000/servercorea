package com.parcel.tools.games.games.mafia

import com.parcel.tools.games.gamesession.GamesSession
import com.parcel.tools.games.GlobalRandomiser
import com.parcel.tools.games.games.mafia.voteinformation.MafiaVoteInformation
import java.lang.Exception


class MafiaSessionException(message: String): Exception(message)
class MafiaSession(sessionId: Long, sessionPas: Long) :  GamesSession<MafiaUser, MafiaEvent>(sessionId, sessionPas) {

    /**
     * Символ используемый для разделения элементов массива при передаче данных WEB страницу.
     */
    private val SEPORATOR = "_"


    private val logger = org.apache.log4j.Logger.getLogger(MafiaSession::class.java!!)

    /**
     * Пользователя которого выбрал шериф для проверки
     */
    private var selectedUserForSherifCheck = ""

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
        logger.debug("getRole($userName)")
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
     * Получить варианты за кого можно проголосовать когда голосует ГОРОД.
     * (список кандидатов)
     */
    fun getUsersForVoteСitizen(userName: String):String
    {
        logger.debug("getUsersForVoteСitizen($userName)")
        var ans = ""
        if(getUser(userName).role!=MafiaUserRoles.LEADING && getUser(userName).isAlife) {
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
     * Получить варианты за кого можно проголосовать когда голосует МАФИЯ.
     * (список кандидатов)
     */
    fun getUsersForVoteMafia(userName: String):String
    {
        logger.debug("getUsersForVoteMafia($userName)")
        var ans = ""
        if(getUser(userName).role==MafiaUserRoles.MAFIA && getUser(userName).isAlife) {
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

    /**
     * Начать игру.
     * (данный метод также распределяет роли)
     */
    override fun startGame()
    {
        if (!started) {
            started = true
            logger.info("startGame()...")
            mafiaSessionState = MafiaSessionState.CITIZEN_VOTE
            val mafiaNames = generateMafia()
            mafiaNames.forEach { this.getUser(it).role = MafiaUserRoles.MAFIA }
            val sheriffNames = generateSheriff()
            sheriffNames.forEach { this.getUser(it).role = MafiaUserRoles.SHERIFF }
            startGameEvent()
            updateVoteTableEvent()
        }
    }

    fun getGameState(userName: String):String
    {
        logger.debug("getGameState($userName)")
        return this.mafiaSessionState.toString()
    }



    /*******VOTE*******/


    fun getSitizenVoteTable(userName: String):String
    {
        logger.debug("getSitizenVoteTable($userName)")
        return MafiaVoteInformation(getUser(userName), users, this.mafiaSessionState).toJson()
    }


   fun vote(userName: String, voteName: String) :Boolean
    {
        logger.debug("vote($userName, $voteName)")
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
        logger.debug("mafiaVoteResult($userName)")
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

        checkUserSheriff(userName, this.selectedUserForSherifCheck)
        this.selectedUserForSherifCheck = ""
        Thread.sleep(100)//AHTUNG KOSTILE
        //обновляем таблицы голосования
        updateVoteTableEvent()
        return result
    }

    /**
     * Завершить голосование и применить результаты.
     */
    fun cityzenVoteResult(userName: String):String
    {
        logger.debug("cityzenVoteResult($userName)")
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
        Thread.sleep(100)//AHTUNG KOSTILE
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

    /**
     * Выбрать пользователя которого проверяет шериф.
     */
    fun selectCheckUserSheriff(userName: String, checkedUserName: String)
    {
        logger.debug("selectCheckUserSheriff($userName, $checkedUserName)")
        if(checkedUserName != "" && getUser(userName).role == MafiaUserRoles.SHERIFF)
        {
            this.selectedUserForSherifCheck = checkedUserName
            sheriffCheckSelectEvent(checkedUserName)
        }
        else
            logger.warn("userName not correct or  checkedUserName.lenth ==0")
    }


    /******SPESHIAL*******/
    /**
     * Проверить яроль игрока.
     * Работает если проверяющий имеет роль шерифа.
     * Метод передает в объект MafiaUser(userName) (если он шериф) информацию  о том пользователе которого он проверил.
     * Метод возвращает роль проверяемого пользователя.
     */
    private fun checkUserSheriff(userName: String, checkedUserName: String)
    {
        logger.debug("checkUserSheriff($userName, $checkedUserName")
        if(isSheriffExists()) {
            var sheriff = getSheriff()
            val checkUser = getUser(checkedUserName)
            checkUser.sheriffOptions.checked = true//ставим метку чт оэтот пользователь проверен шерифом
            sheriff.sheriffOptions.checkedUserNames.add(checkedUserName)//даем шерифу список всех провереных пользователей
        }
        else
            logger.warn("Sheriff does not exists.")

    }

    /**
     * Получить список игроков которых может проверить шериф.
     */
    fun getCheckUserSheriffVariants(userName: String): String
    {
        logger.debug("getCheckUserSheriffVariants($userName")
        val sheriff = getUser(userName)
        return if(sheriff.role==MafiaUserRoles.SHERIFF)
        {
            val usersAvalable = ArrayList<String>()
            users.forEach {
                if(it.role!=MafiaUserRoles.LEADING && it.name!=userName &&
                        !sheriff.sheriffOptions.checkedUserNames.contains(it.name))
                    usersAvalable.add(it.name)
            }
            toCsvLine(usersAvalable)
        }
        else ""
    }




    /******USER NAVIGATION*****/
    /**
     * Есть ли в данной сессии игры Шериф
     */
    private fun isSheriffExists():Boolean
    {
        for (user in users)
        {
            if(user.role == MafiaUserRoles.SHERIFF)
                return true
        }
        return false
    }

    /**
     * Получить шерифа.
     */
    private fun getSheriff(): MafiaUser
    {
        for (user in users)
        {
            if(user.role == MafiaUserRoles.SHERIFF)
                return user
        }
        throw MafiaSessionException("There is no sherif in this game.")
    }


    /******PRIVATE*******/


    /**
     * Выбирает из списка игроков, игроков которые будут играть за Мафию.
     */
    private fun generateMafia():ArrayList<String>
    {
        /**
         * Функция расчета количества игроков играющих за мафию.
         * (надо все этифункции расчета игроков в дальнейшем вынести куда то !!!!)
         */
        fun countMafiaGamersIsNecessary()=(users.count()-1)/3

        val mafiaNames = ArrayList<String>()
        val users = ArrayList<MafiaUser>()
        this.users.forEach { if(it.role == MafiaUserRoles.CITIZEN)users.add(it) }
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
    /**
     * Выбирает из списка игроков, игроков которые будут играть за Шерифа.
     */
    private fun generateSheriff():ArrayList<String>
    {
        /**
         * Функция расчета количества игроков играющих за шерифа.
         * (надо все этифункции расчета игроков в дальнейшем вынести куда то !!!!)
         */
        fun countSheriffGamersIsNecessary()=if(users.count()<7) 0 else 1

        val sheriffNames = ArrayList<String>()
        val users = ArrayList<MafiaUser>()
        this.users.forEach { if(it.role == MafiaUserRoles.CITIZEN)users.add(it) }
        //количество шерифов
        var sheriffCount = countSheriffGamersIsNecessary()
        while(sheriffCount>0)
        {
            val i = GlobalRandomiser.getRundom(users.size)
            sheriffNames.add(users[i].name)
            users.removeAt(i)
            sheriffCount --
        }
        return sheriffNames
    }

    /******TOOLS*******/

    private fun toCsvLine(data: ArrayList<String>): String
    {
        var ans = ""
        data.forEach { ans = ans + SEPORATOR + it }
        return ans
    }




    /*******EVENTS*******/
    fun updateVoteTableEvent()
    {
        gameEvent.forEach {
            val table = MafiaVoteInformation(getUser(it.userName), users, this.mafiaSessionState).toJson()
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
    /*******TERGET_EVENTS*******/
    /**
     * Для уменьшение ебли с перетиранием данныхпри передачи событий каким либо пользователям
     * можно послать событие ограниченому количеству пользователей.
     */


    fun sheriffCheckSelectEvent(checkUserName: String)
    {
        gameEvent.forEach {
            if(getUser(it.userName).role==MafiaUserRoles.LEADING)
            {
                it.sheriffCheckedUser(checkUserName)
            }
        }
    }

}