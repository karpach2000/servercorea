package com.parcel.tools.constructor.games;

import com.parcel.tools.spy.SpySessionManager;
import com.parcel.tools.spy.database.SpyLocation;

import java.util.ArrayList;
import java.util.List;

public class CounterGames {
    public CounterMenuGames counterMenu= new CounterMenuGames();
    public String descriptionText="Игры.";
    public List<SpyLocation> locations = new ArrayList<SpyLocation>();
    public CounterGames()
    {
        counterMenu.addItem("Шпион.","ropeSlack");
        try {
            locations = SpySessionManager.INSTANCE.getLocationList();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
}
