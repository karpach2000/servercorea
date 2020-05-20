package com.parcel.tools.games.mafia

import com.parcel.tools.games.GameEvent

interface MafiaEvent : GameEvent {

    /**
     * Имя пользователяя которому должен прилететь Event.
     */
    var userName : String

    /**
     * Обновляет таблицу в web интерфейсе, в которой прописываются мертвые и живые игроки.
     * (для горожан)
     */
    fun updateVoteTable(votetTable: String)


    /**
     * Открыть голосование мафии
     */
    fun openMafiaVote(deadUser: String)

    /**
     * Открыть голосование горожан
     */
    fun openСitizensVote(deadUser: String)

    /**
     * Событие о том что шериф выбрал каого пользователя он хочет проверить.
     */
    fun sheriffCheckedUser(user: String)

    fun leaderChandged(leadername: String)



}