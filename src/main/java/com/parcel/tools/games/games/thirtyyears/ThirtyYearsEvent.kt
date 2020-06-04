package com.parcel.tools.games.games.thirtyyears

import com.parcel.tools.games.GameEvent

interface ThirtyYearsEvent : GameEvent {

    /**
     *   Событие перевода в статус ввведения реальной отмазки.
     */
    fun ENTER_REAL_EXCUTE_event(event: String)
    /**
     * Событие введение фальшивой отмазки.
     */
    fun ENTER_FALSH_EXCUTE_event(event: String)
    /**
     * Событие голосования.
     * @param enable значит данный пользователь может голосоватью
     */
    fun VOTE_event(variants: String)
    /**
     * Событие Показываения пользователю результаты всей игры.
     */
    fun SHOW_FINAL_RESULTS_event(table: String)
    /**
     * События демонстрации результатов голосования пользователям.
     */
    fun SHOW_RESULTS_event(table: String)


    /***КОММУНИКАЦИЯ****/
    fun setInMessage(message: String)


}