package com.parcel.tools.games.mafia

import com.parcel.tools.games.GameEvent

interface MafiaEvent : GameEvent {

    /**
     * Обновляет таблицу в web интерфейсе, в которой прописываются мертвые и живые игроки.
     * (для горожан)
     */
    fun updateVoteTableForСitizens(votetTable: String)

    /**
     * Обновляет таблицу в web интерфейсе, в которой прописываются
     * мертвые и живые игроки, а так же члены мафии.
     * (для мафии)
     */
    fun updateVoteTableForMafia(votetTable: String)

    /**
     * Открыть голосование мафии
     */
    fun openMafiaVote()

    /**
     * Открыть голосование горожан
     */
    fun openСitizensVote()



}