package com.parcel.tools.constructor.database.users

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id


enum class Role(val user_role: String, val description: String = "") {

        ADMIN("ADMIN"),
        USER("USER"),
        NC("NC", "Такой роли не существует");
}