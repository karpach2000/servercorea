package com.parcel.tools.games.gamesession

import com.parcel.tools.games.GameSessionManagerException
import com.parcel.tools.games.gamesuser.GameUser

/**
 * Используется как свойство в классе GameSession при голосовани.
 */
class GameSessionVote<U : GameUser>(private val users: ArrayList<U>) {

    private val logger = org.apache.log4j.Logger.getLogger(GameSessionVote::class.java!!)


    /**
     * Проголосовать.
     * @param userName кто голосует
     * @param voteName против/за кого голосует
     */
    fun vote(userName: String, voteName: String) :Boolean
    {
        logger.info("vote($userName, $voteName)")
        getUser(userName).gameUserVote.voteName = voteName
        users.forEach { it.gameUserVote.votedCount = 0 }
        //пересчитываем голоса
        users.forEach {
            val vn = it.gameUserVote.voteName
            if(vn!="")
            {
                getUser(vn).gameUserVote.votedCount ++
            }
        }
        return true
    }

    /**
     * Отчистить таблицу голосования.
     */
    fun clear() :Boolean
    {
        logger.info("clear()")
        users.forEach {
            it.gameUserVote.clearVote()
        }
        return true
    }

    /**
     * Получить пользователя занявшего plase место
     * @param plase место занятое пользователем.
     */
    fun getPlace(plase: Int): U
    {
        return getSortedUserList()[plase]
    }

    /**
     * Получить победителя голосования.
     */
    fun getLeader() = getPlace(0)

    private fun getSortedUserList(): ArrayList<U>
    {
        val sorted = ArrayList<U>()
        users.forEach { sorted.add(it) }
        sorted.sortBy { -it.gameUserVote.votedCount }
        return sorted
    }


    private fun getUser(name: String): GameUser
    {
        users.forEach {
            if(it.name == name)
                return it
        }
        throw GameSessionManagerException("Can`t finde user: $name")
    }

}