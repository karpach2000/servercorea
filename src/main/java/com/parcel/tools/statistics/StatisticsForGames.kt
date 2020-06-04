package com.parcel.tools.statistics

class StatisticsForGames(private val gameName: String)
{
    private val logger = org.apache.log4j.Logger.getLogger(StatisticsForGames::class.java!!)

    fun startGame(countUsers: Int = -1)
    {
        if(countUsers>0)
            logger.info("startGame:$gameName, countUsers:$countUsers")
        else
            logger.info("startGame:$gameName")
    }

    fun openGame()
    {
        logger.info("openGame:$gameName")
    }

}