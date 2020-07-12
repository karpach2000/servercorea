package com.parcel.tools.constructor.database.users

import com.parcel.tools.Globals
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Component
import java.lang.Exception
import java.sql.ResultSet
import java.sql.SQLException


class UserManagerException(message: String): Exception(message)

/**
 * Объект непосредсвенно работающий с БД. Инициализируется спрингом.
 * После инициализации попадает в объект DataBaseManager.
 * Инициализируй его единожды!!!!
 */
@Component
open class UserManager {




    @Autowired
    var jdbcTemplate: JdbcTemplate? = null



    private val logger = org.apache.log4j.Logger.getLogger(UserManager::class.java!!)

    init {
        logger.info("Init UserManager (database)")
        Globals.userManager = this
    }

    internal inner class GetUsersRowMapper : RowMapper<User> {
        @Throws(SQLException::class)
        override fun mapRow(rs: ResultSet, rowNum: Int): User {
            val user = User()
            //user.id = rs.getInt("id")
            user.name = rs.getString("user_login")
            user.password=rs.getString("user_password")
            user.setRole(rs.getString("user_role"))
            return user
        }
    }
    internal inner class DeleteUserRowMapper : RowMapper<String> {
        @Throws(SQLException::class)
        override fun mapRow(rs: ResultSet, rowNum: Int): String {
            return rs.getString("delete_user")
        }
    }
    internal inner class AddUserRowMapper : RowMapper<String> {
        @Throws(SQLException::class)
        override fun mapRow(rs: ResultSet, rowNum: Int): String {
            return rs.getString("add_user")
        }
    }
    internal inner class GetUserRoles : RowMapper<String> {
        @Throws(SQLException::class)
        override fun mapRow(rs: ResultSet, rowNum: Int): String {
            return rs.getString("user_role")
        }
    }

    fun getAllUsers(): List<User>
    {
        logger.info("getAllUsers()")
        return jdbcTemplate!!.query("SELECT * FROM get_users_whith_roles()",
                GetUsersRowMapper())
    }

    fun addUser(user: User) = addUser(user.name!!, user.password!!, user.role!!.user_role)

    fun addUser(login:String, password: String, role: String = "USER" )
    {
        logger.info("addUser($login, $password, $role)")
        val dbAns = jdbcTemplate!!.query("SELECT * FROM add_user('$login', '$password', '$role')",
                AddUserRowMapper())[0]
        if(dbAns == "OK")
            return
        else {
            logger.warn("Data base ansered: $dbAns")
            throw UserManagerException(dbAns)
        }
    }

    /**
     * Добавить пользователя.
     * (используется пользователем при его регистрации)
     */
    fun addUserU(login:String, password: String, password2: String, role: String = "USER" )
    {
        logger.info("addUser($login, $password, $role)")
        //проверка пароля
        if(password !=password2)
            throw UserManagerException("Password from firs textbox\n" +
                    "does not match whith  password from textbox.")
        //непосредственно добавление
        val dbAns = jdbcTemplate!!.query("SELECT * FROM add_user('$login', '$password', '$role')",
                AddUserRowMapper())[0]
        if(dbAns == "OK")
            return
        else {
            logger.warn("Data base ansered: $dbAns")
            throw UserManagerException(dbAns)
        }
    }

    fun dellUser(login: String)
    {
        logger.info("dellUser($login)")
        val dbAns = jdbcTemplate!!.query("SELECT * FROM delete_user('$login')",
                DeleteUserRowMapper())[0]
        if(dbAns == "OK")
            return
        else {
            logger.warn("Data base ansered: $dbAns")
            throw UserManagerException(dbAns)
        }
    }

    fun getUserRoles(login: String) :ArrayList<String>
    {
        logger.info("getUserRoles($login)")
        val dbAns = jdbcTemplate!!.query("SELECT * FROM get_user_and_role('$login')",
                GetUserRoles()) as ArrayList<String>
        return dbAns

    }

}