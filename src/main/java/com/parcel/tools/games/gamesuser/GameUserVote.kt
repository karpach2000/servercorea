package com.parcel.tools.games.gamesuser

/**
 * Используется как свойство в классе GameUser при голосовани.
 */
class GameUserVote {

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