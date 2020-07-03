package com.parcel.tools.games.gamesuser

/**
 * Класс описывающий абстрактного пользователя.
 */
abstract class GameUser (val name:String) {

    /**
     * Используется как свойство в классе GameUser при голосовани.
     */
    val gameUserVote = GameUserVote()

    /**
     * Идентефикатор по которому можно отличить 2 разных пользователей с одинаковыми именами пользователя
     */
    var identeficator = ""


}