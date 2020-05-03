package com.parcel.tools.games.mafia

import com.parcel.tools.games.GameUser

class MafiaUser(name:String) : GameUser(name) {

    var role = MafiaUserRoles.CITIZEN
    var isAlife = true

    /*******VOTE*********/

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