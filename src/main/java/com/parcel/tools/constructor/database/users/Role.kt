package com.parcel.tools.constructor.database.users


enum class Role(val user_role: String, val description: String = "") {

    ADMIN("ADMIN"),
    USER("USER"),
    NC("NC", "Такой роли не существует");
}