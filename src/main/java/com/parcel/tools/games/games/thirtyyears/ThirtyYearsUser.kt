package com.parcel.tools.games.games.thirtyyears

import com.parcel.tools.games.gamesuser.GameUser

class ThirtyYearsUser (name:String) : GameUser(name) {

    /**
     * Отмазывается ли пользователь сейчас (или делает вид что отмазывается как соперник)
     */
    var isExcuting = false
    /**
     * Событие от которого отмазывется пользователь.
     */
    var event = ""
    /**
     * Отмазка которую придумал пользователь.
     */
    var excute = ""
    /**
     * Псевдоотмазка которую придумывает пользователь за место своего соперника.
     */
    var falshExcute = ""

    /**
     * Количество очков набраное пользователем.
     */
    var points = 0

    /**
     * Отчищает все данные о фальшивых отмазках и голосовании.
     * (подготавливает к новому раунду)
     */
    fun clear()
    {
        falshExcute = ""
    }
}