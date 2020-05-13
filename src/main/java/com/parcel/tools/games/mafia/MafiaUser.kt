package com.parcel.tools.games.mafia

import com.parcel.tools.games.GameUser

class MafiaUser(name:String) : GameUser(name) {

    /**
     * Опции активные когда игрок играет за Шерифа
     */
    inner class SheriffOptions()
    {
        /**
         * Имена пользователей которых проверил шериф.
         */
        val checkedUserNames = ArrayList<String>()
    }


    /**
     * Роль пользователя в игре.
     */
    var role = MafiaUserRoles.CITIZEN

    /**
     * Информация о том жив пользователь или уже нет.
     */
    var isAlife = true

    /**
     * Опции активные когда игрок играет за Шерифа
     */
    var sheriffOptions = SheriffOptions()

    /*******VOTE*********/

    /**
     * Количество игроков проголосовавших против данного пользователя.
     */
    var votedCount = 0//true - против этого пользователя проголосовали 1 раз

    /**
     * Имя пользователя против которого проголдосовал данный игрок.
     */
    var voteName = ""

    /**
     * Сброс результатов голосования
     */
    fun clearVote()
    {
        voteName = ""
        votedCount = 0
    }
}