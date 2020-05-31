package com.parcel.tools.games.games.thirtyyears.database

import com.parcel.tools.Globals
import com.parcel.tools.games.games.spy.database.SpyLocationManagerException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Component
import java.sql.ResultSet
import java.sql.SQLException

@Component
open class ThirtyYearsEventManager {

    @Autowired
    var jdbcTemplate: JdbcTemplate? = null

    private val logger = org.apache.log4j.Logger.getLogger(ThirtyYearsEventManager::class.java!!)

    init {
        logger.info("Init ThirtyYearsEventManager (database)")
        Globals.thirtyYearsEventManager = this
    }

    internal inner class GetEventRowMapper : RowMapper<ThirtyYearsEvent> {
        @Throws(SQLException::class)
        override fun mapRow(rs: ResultSet, rowNum: Int): ThirtyYearsEvent {
            val thirtyYearsEvent = ThirtyYearsEvent()
            //user.id = rs.getInt("id")
            thirtyYearsEvent.authorLogin = rs.getString("login")
            thirtyYearsEvent.event = rs.getString("event")
            return thirtyYearsEvent
        }
    }

    internal inner class StringRowMapper : RowMapper<String> {
        @Throws(SQLException::class)
        override fun mapRow(rs: ResultSet, rowNum: Int): String {
            val ans = rs.getString(1)
            //user.id = rs.getInt("id")
            return ans
        }
    }

    fun getEventUser(event: String): String
    {
        logger.info("getEventUser($event)")
        val ans = jdbcTemplate!!.query("SELECT * FROM get_thirty_years_event_login('$event')",
                StringRowMapper())
        if(ans.size>0)
            return ans[0]
        else
            throw SpyLocationManagerException("Can't finde event '$event'")
    }

    fun getAllEventsAsString(): List<String>
    {
        logger.info("getAllEventsAsString()")
        return jdbcTemplate!!.query("SELECT event FROM get_thirty_years_event_and_login()",
                StringRowMapper())
    }

    fun getAllEvents(): List<ThirtyYearsEvent>
    {
        logger.info("getAllEvents()")
        return jdbcTemplate!!.query("SELECT * FROM get_thirty_years_event_and_login()",
                GetEventRowMapper())
    }

    fun addEvent(event: String, userLogin: String):Boolean
    {
        logger.info("addLocation($event, $userLogin)")
        val ans =jdbcTemplate!!.query("SELECT * FROM add_thirty_years_event('$event', '$userLogin')",
                StringRowMapper())
        return ans[0] == "OK"
    }
    fun deleteEvent(event: String, login: String):Boolean
    {
        logger.info("deleteLocation($event, $login)")
        val ans =jdbcTemplate!!.query("SELECT * FROM delete_thirty_years_event('$event', '$login')",
                StringRowMapper())[0]
        return ans == "OK"
    }





}