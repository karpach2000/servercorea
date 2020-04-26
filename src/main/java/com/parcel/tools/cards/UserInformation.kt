package com.parcel.tools.cards

import com.google.gson.GsonBuilder

/**
 * Информация отображаемая каждому игроку.
 */
class UserInformation() {

    constructor(user: User, usersCount: Int, allUsers: String)
            : this() {
        name = user.name
        card = user.userCard
        this.usersCount = usersCount
        this.allUsers = allUsers


    }

    constructor(error: String)
            : this() {
        this.error = error
    }

    /**
     * Список игроков.
     */
    var allUsers = ""

    /**
     * Количество игроков
     */
    var usersCount = 0

    /**
     * Имя текущего игрока
     */
    var name = ""


    /**
     * Сообщение об ошибке
     */
    var error = ""
    var card = ""

    override fun toString() =
            "Name: $name\n" +
                    "Users in game: $usersCount\n" +
                    "Users:\n$allUsers"


    fun toJson(): String {
        val builder = GsonBuilder()
        val gson = builder.create()
        return gson.toJson(this)
    }
}