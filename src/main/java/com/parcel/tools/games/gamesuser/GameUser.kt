package com.parcel.tools.games.gamesuser

/**
 * Класс описывающий абстрактного пользователя.
 */
abstract class GameUser (val name:String) {

    /**
     * Используется как свойство в классе GameUser при голосовани.
     */
    val gameUserVote = GameUserVote()

}