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
     * Показывает пользователю результаты всей игры.
     */
    SHOW_FINAL_RESULTS,
    /**
     * Демонстрирует результаты голосования пользователям.
     */
    SHOW_RESULTS

}