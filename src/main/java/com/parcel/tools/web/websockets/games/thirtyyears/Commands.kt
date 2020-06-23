package com.parcel.tools.web.websockets.games.thirtyyears

enum class Commands {

    /**
     * Сообщение об ошибке
     */
    ERROR,
    /******GAME STATE******/

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


    /**********************/
    //PARRENT

    /**
     * Пинг
     */
    PING,
    /**
     * Пинг
     */
    PONG,

    /**
     * Соединится с сервером. (RX)
     */
    CONNECT,

    /**
     * Добавить пользователя. (RX)
     */
    ADD_USER,

    /**
     * Событие начала игры (TX).
     */
    START_GAME,

    /**
     * Событие остановки игры (TX).
     */
    STOP_GAME,


    //GAME

    /**
     * Ввести реальную отмазку.
     */
    SET_REAL_EXCUTE,

    /**
     *Ввести фальшивую отмазку
     */
    SET_FALSH_EXCUTE,

    /**
     * Проголосовать за тот или иной вариант
     */
    SET_VOTE,
    /**
     * Закончить раунд голосования
     */
    ROUND


}