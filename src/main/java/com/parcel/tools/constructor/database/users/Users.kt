package com.parcel.tools.constructor.database.users

import javax.persistence.*



class Users {

    //@Id
    //@GeneratedValue
    //@GeneratedValue(strategy = GenerationType.IDENTITY)
    var id : Int?=null

    //@Column(nullable = false)
    var login: String? =null

    //@Column(nullable = false)
    var password: String? =null

    var active: Boolean? =null

    //@Transient
    var role: Role?=null

   //@Transient
    fun setRole(role: String)
    {
        Role.values().forEach {
            if(it.user_role == role) {
                this.role = it
                return
            }
        }
        this.role = Role.NC
    }





    override fun equals(cashRequest: Any?): Boolean {
        try {
            val user = cashRequest as Users
            return user.login == login
        }
        catch (e: Exception) {
            return false
        }
    }

    override fun toString() =
            "login: $login\npassword: $password\nrole: $role\n"

}