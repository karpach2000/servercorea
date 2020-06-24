package com.parcel.tools.games.games.thirtyyears.settings

/**
 * Объект содержит количество очков начисляемое пользователю за то или иное действие.
 */
class SettingsData {

    /**
     * Угададал кто говорит правду.
     */
    var selectedTrueTellerPoints = 10

    /**
     * Кто - то проголосовал за твой вариант.
     */
    var somewoneVotedYouPoints = 1

    /**
     * Время требуемое на введение фальшивой отмазки.
     */
    var ENTER_FALSH_EXCUTE_time = 30000L
    /**
     * Время требуемое на голосование.
     */
    var VOTE_time = 20000L

    /**
     * Время требуемое для введения реальной отмазки.
     */
    var ENTER_REAL_EXCUTE_time = 60000L
}