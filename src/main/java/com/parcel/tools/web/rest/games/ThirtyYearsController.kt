package com.parcel.tools.web.rest.games

import com.parcel.tools.constructor.Page
import com.parcel.tools.constructor.bodies.games.CounterGames
import com.parcel.tools.statistics.StatisticsForGames
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.RequestMapping
import java.io.IOException
import javax.servlet.http.HttpSession

@Controller
class ThirtyYearsController {
    private val logger = org.apache.log4j.Logger.getLogger(ThirtyYearsController::class.java!!)
    private val static = StatisticsForGames("ThirtyYearsController")

    @RequestMapping("/games/thirtyyears")
    @Throws(IOException::class)
    internal fun games(model: Model, session: HttpSession): String {
        static.openGame()
        val counter = CounterGames()
        val page = Page(counter)
        model.addAttribute("page", page)
        return "web/html/games/thirtyYears"
    }
}