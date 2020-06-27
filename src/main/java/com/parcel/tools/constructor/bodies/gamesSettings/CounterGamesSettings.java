package com.parcel.tools.constructor.bodies.gamesSettings;

import com.parcel.tools.constructor.bodies.Counter;
import com.parcel.tools.games.games.spy.SpySessionManager;
import com.parcel.tools.games.games.spy.database.SpyLocation;

import java.util.ArrayList;
import java.util.List;

public class CounterGamesSettings extends Counter {
    public String descriptionText="Настройка игр.";
    public String errorMessage ="";
    public List<SpyLocation> locations = new ArrayList<SpyLocation>();
    public CounterGamesSettings()
    {
        try {
            locations = SpySessionManager.INSTANCE.getAllLocationList();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
}
