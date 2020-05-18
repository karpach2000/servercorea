package com.parcel.tools.constructor.bodies.admin;


import java.util.ArrayList;
import java.util.List;

import com.parcel.tools.Globals;
import com.parcel.tools.constructor.bodies.Counter;
import com.parcel.tools.constructor.database.users.Users;

public class CounterAdmin  extends Counter {

    public String descriptionText="Администрирование пользователей.";
    public List<Users> users = new ArrayList<Users>();
    public CounterAdmin()
    {
        getUsers();
    }

    private List<Users> getUsers()
    {
        users = Globals.INSTANCE.getUserManager().getAllUsers();
        return users;
    }
}
