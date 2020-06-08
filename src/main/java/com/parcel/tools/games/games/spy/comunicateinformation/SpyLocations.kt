package com.parcel.tools.games.games.spy.comunicateinformation

import com.google.gson.GsonBuilder

/**
 * Список локаций предающийся игрокам в шпиона.
 */
class SpyLocations {

    /**
     * Локации доступные всем (созданные пользователями с праавами ADMIN).
     */
    var publicLocations = ArrayList<String>()

    /**
     * Локации доступные только данному пользователю.
     */
    var userLocations = ArrayList<String>()

    /**
     * Информация о том нужно ли использовать локации пользователя.
     */
    var useUserLocations = false


    fun toJson():String
    {
        var builder =  GsonBuilder()
        var gson = builder.create()
        return gson.toJson(this)
    }


}