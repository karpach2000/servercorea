package com.parcel.tools.games.games.thirtyyears

import com.parcel.tools.games.gamesession.timer.TimerEventInterface

class ThirtyYearsTimerEvent(private val thirtyYearsSession:ThirtyYearsSession) : TimerEventInterface {
    private val logger = org.apache.log4j.Logger.getLogger(ThirtyYearsTimerEvent::class.java!!)

    override fun timerStarted(value: Long) {
        logger.debug("timerStarted($value: Long)")
        val startTime = System.currentTimeMillis()
        thirtyYearsSession.gameEvent.forEach {
            val delta = System.currentTimeMillis() - startTime
            it.START_TIMER_event(value - delta)
        }
    }

    override fun timerStoped(value: Long) {
        logger.debug("timerStoped($value: Long)")
    }

    override fun timerPaused() {
        logger.debug("timerStoped()")
    }

    override fun timeIsOver() {
        logger.debug("timeIsOver()")
        thirtyYearsSession.updateByStateMashine()
    }
}