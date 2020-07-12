package com.parcel.tools.constructor.database.users

import com.google.gson.GsonBuilder
import com.google.gson.annotations.Expose


open class User() {

    constructor(login: String):this()
    {
        this.name = login
    }

    //@Id
    //@GeneratedValue
    //@GeneratedValue(strategy = GenerationType.IDENTITY)
    var id : Int?=null

    //@Column(nullable = false)
    var name: String? =null

    @Expose(serialize = false, deserialize =false)
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
            val user = cashRequest as User
            return user.name == name
        }
        catch (e: Exception) {
            return false
        }
    }


    /**
     * Получает поля объекта mutableData из JSON строки.
     * @return true - если старые и новые значения оказались равны, false - если не равны
     */
    fun fromJson(json : String) :Boolean
    {
        val builder = GsonBuilder()
        val user = builder.create().fromJson(json, User::class.java)
        this.name = user.name
        return true
    }


    open fun toJson():String{
        var builder =  GsonBuilder()
        var gson = builder.create()
        return gson.toJson(this)
    }

    override fun toString() =
            "login: $name\npassword: $password\nrole: $role\n"

}