package com.parcel.tools.web.websockets.games.thirtyyears

enum class Commands {
    /**
     * Соединится с сервером. (RX)
     */
    CONNECT,

    /**
     * Добавить пользователя. (RX)
     */
    ADD_USER
}