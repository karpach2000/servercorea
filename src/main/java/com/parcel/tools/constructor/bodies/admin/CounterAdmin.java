package com.parcel.tools.constructor.bodies.admin;


import java.util.ArrayList;
import java.util.List;

import com.parcel.tools.Globals;
import com.parcel.tools.constructor.bodies.Counter;
import com.parcel.tools.constructor.database.users.User;

public class CounterAdmin  extends Counter {

    public String descriptionText="Администрирование пользователей.";
    public List<User> users = new ArrayList<User>();
    public CounterAdmin()
    {
        getUsers();
    }

    private List<User> getUsers()
    {
        users = Globals.INSTANCE.getUserManager().getAllUsers();
        return users;
    }
}
