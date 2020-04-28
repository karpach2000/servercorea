package com.parcel.tools.constructor.bodies.admin;


import com.parcel.tools.Globals;
import com.parcel.tools.constructor.database.users.Users;

import java.util.ArrayList;
import java.util.List;

public class CounterAdmin {


    public CounterMenuAdmin counterMenu= new CounterMenuAdmin();
    public String descriptionText="Администрирование пользователей.";
    public List<Users> users = new ArrayList<Users>();
    public CounterAdmin()
    {
        counterMenu.addItem("Список пользователей", "userList");
        counterMenu.addItem("Добавить пользователя", "createUser");
        counterMenu.addItem("Удалить пользователя", "deleteUser");
        getUsers();
    }

    private List<Users> getUsers()
    {
        users = Globals.INSTANCE.getUserManager().getAllUsers();
        return users;
    }
}
