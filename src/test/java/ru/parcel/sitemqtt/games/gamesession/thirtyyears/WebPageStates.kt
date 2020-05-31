package ru.parcel.sitemqtt.games.gamesession.thirtyyears

enum class WebPageStates
{
    /**
     * Введение реальной отмазки.
     */
    ENTER_REAL_EXCUTE_EVENT,

    /**
     * Введение фальшивой отмазки.
     */
    ENTER_FALSH_EXCUTE_EVENT,

    /**
     * Голосование.
     */
    VOTE_EVENT,

    /**
     * Показывает пользователю результаты всей игры.
     */
    SHOW_FINAL_RESULTS_EVENT,
    /**
     * Демонстрирует результаты голосования пользователям.
     */
    SHOW_RESULTS_EVENT,

    /**
     * Добавление пользователя.
     */
    ADD_USER
}