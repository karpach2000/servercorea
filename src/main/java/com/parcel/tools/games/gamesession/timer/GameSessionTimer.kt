package com.parcel.tools.games.gamesession.timer

/**
 * Используется в качестве таймера для игр.
 */
class GameSessionTimer {

    /**
     * Периодичность с которой проверяется таймер
     */
    private val CHECK_PERIOD = 200L


    /**
     * События таймера.
     */
    private val timerEventsHandlers = ArrayList<TimerEventInterface>()

    private var startMiles =0L

    var isStarted = false
    private set

    private var thread = Thread()

    /**
     * Запустить таймер.
     * @param time время на которое запускается таймер мс
     * @return true - таймер успешно запущен, false - таймер уже запущен
     */
    fun startTimer(time: Long): Boolean
    {
        isStarted = true
        if(!isStarted) {
            startMiles = System.currentTimeMillis()
            timerEventsHandlers.forEach { it.timerStarted(time) }
            thread = Thread(Runnable { timerAction(time) })
            thread.start()
            return true
        }
        else
        {
            return false
        }
    }

    fun stopTimer()
    {
        isStarted = false
        timerEventsHandlers.forEach { it.timerStoped() }
        thread.stop()

    }



    /**
     * Фигня которая считает милисекунды.
     */
    private fun timerAction(time: Long)
    {
        while (!checkTimer(time))
        {
            Thread.sleep(CHECK_PERIOD)
        }
        timerEventsHandlers.forEach { it.timerStoped(time) }
    }

    /**
     * Проверить таймер.
     */
    private fun checkTimer(time: Long): Boolean
    {
        val current = System.currentTimeMillis()
        val deltaTime = current  - startMiles
        return deltaTime > time
    }



    /*****EVENTS*****/

    fun subscribeTimerEvents(timerEventInterface: TimerEventInterface)
    {
        timerEventsHandlers.add(timerEventInterface)
    }

}