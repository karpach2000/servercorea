package com.parcel.tools.games.games.thirtyyears

enum class GameState {

    /**
     * Введение реальной отмазки.
     */
    ENTER_REAL_EXCUTE,

    /**
     * Введение фальшивой отмазки.
     */
    ENTER_FALSH_EXCUTE,

    /**
     * Голосование.
     */
    VOTE,

    /**
     * Демонстрирует результаты голосования пользователям.
     */
    SHOW_RESULTS

}