package com.parcel.tools.web.rest.games

import com.parcel.tools.games.IdGenerator
import com.parcel.tools.games.games.mafia.MafiaSessionManager
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import java.io.IOException

/**
 * Контроллер занимается служебными функциями присущим всем играм.
 */
@Controller
class GamesController {

    /**
     * Сгенерировать Id игры
     */
    @RequestMapping("/games/generate_game_id")
    @ResponseBody
    @Throws(IOException::class)
    internal fun addSession(model: Model): String {
        return IdGenerator.generate().toString()
    }
}