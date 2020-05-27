package com.parcel.tools.games.games.thirtyyears

import com.parcel.tools.games.GameEvent

interface ThirtyYearsEvent : GameEvent {

    /**
     * Событие ввведения реальной отмазки.
     */
    fun ENTER_REAL_EXCUTE_event()
    /**
     * Событие введение фальшивой отмазки.
     */
    fun ENTER_FALSH_EXCUTE_event()
    /**
     * Событие голосования.
     */
    fun VOTE_event()
    /**
     * Событие Показываения пользователю результаты всей игры.
     */
    fun SHOW_FINAL_RESULTS_event(table: String)
    /**
     * События демонстрации результатов голосования пользователям.
     */
    fun SHOW_RESULTS_event(table: String)


}