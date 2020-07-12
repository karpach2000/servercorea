package com.parcel.tools.games.gamesuser

import com.google.gson.annotations.Expose
import com.parcel.tools.constructor.database.users.User


/**
 * Класс описывающий абстрактного пользователя.
 */
abstract class GameUser (name:String) : User(name){

    /**
     * Используется как свойство в классе GameUser при голосовани.
     */
    @Expose(serialize = false, deserialize =false)
    val gameUserVote = GameUserVote()

    /**
     * Идентефикатор по которому можно отличить 2 разных пользователей с одинаковыми именами пользователя
     */
    var identeficator = ""


}