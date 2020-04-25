package com.parcel.tools.games.mafia

import com.parcel.tools.games.GameEvent

interface MafiaEvent : GameEvent {

    /**
     * Обновляет таблицу в web интерфейсе, в которой прописываются мертвые и живые игроки.
     * (для горожан)
     */
    fun updateVoteTable(votetTable: String)


    /**
     * Открыть голосование мафии
     */
    fun openMafiaVote()

    /**
     * Открыть голосование горожан
     */
    fun openСitizensVote()

    fun leaderChandged(leadername: String)



}