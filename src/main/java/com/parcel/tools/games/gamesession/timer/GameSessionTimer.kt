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

    /**
     * Пенриод на который завели таймер
     */
    private var period =0L

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

        if(!isStarted) {
            //stopTimer()//на всякий тормозим то что уже есть
            isStarted = true
            period = time
            startMiles = System.currentTimeMillis()
            timerEventsHandlers.forEach { it.timerStarted(time) }
            thread = Thread(Runnable { timerAction() })
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
    private fun timerAction()
    {
        while (!checkTimer())
        {
            Thread.sleep(CHECK_PERIOD)
        }
        timerEventsHandlers.forEach { it.timeIsOver() }
        //timerEventsHandlers.forEach { it.timerStoped(period) }
        stopTimer()
    }

    /**
     * Проверить таймер.
     * @return true - время прошло
     */
    fun checkTimer(): Boolean
    {
        val current = System.currentTimeMillis()
        val deltaTime = current  - startMiles
        return deltaTime > period
    }



    /*****EVENTS*****/

    fun subscribeTimerEvents(timerEventInterface: TimerEventInterface)
    {
        timerEventsHandlers.add(timerEventInterface)
    }

    fun clearTimerEvents()
    {
        timerEventsHandlers.clear()
    }

}

private val timer = GameSessionTimer()

fun main()
{

    class TimerEvent() :TimerEventInterface
    {
        override fun timerStarted(value: Long) {
            println("timerStarted($value: Long)")
        }

        override fun timerStoped(value: Long) {
            println("timerStoped($value: Long)")
        }

        override fun timerPaused() {
            println("timerPaused()")
        }

        override fun timeIsOver() {
            println("timeIsOver()")
        }

    }
    timer.subscribeTimerEvents(TimerEvent())
    for(i in 0..3)
    {
        println("\nITIRATION $i")
        timerCheckAction()
    }
}

private fun timerCheckAction()
{



    timer.startTimer(5000)
    println("Check timer: ${timer.checkTimer()}")
    var i = 0
    while (i<10000)
    {
        Thread.sleep(1)
        i++
    }
    println("Check timer: ${timer.checkTimer()}")
}