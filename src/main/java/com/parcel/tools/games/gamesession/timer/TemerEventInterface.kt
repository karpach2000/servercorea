package com.parcel.tools.games.gamesession.timer

interface TimerEventInterface {

    fun timerStarted(value: Long)

    fun timerStoped(value: Long = -1)

    fun timerPaused()

    fun timeIsOver()

}