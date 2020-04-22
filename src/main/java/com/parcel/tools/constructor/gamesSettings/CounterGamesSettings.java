package com.parcel.tools.constructor.gamesSettings;

import com.parcel.tools.constructor.games.CounterMenuGames;
import com.parcel.tools.spy.SpySessionManager;
import com.parcel.tools.spy.database.SpyLocation;

import java.util.ArrayList;
import java.util.List;

public class CounterGamesSettings {
    public CounterMenuGamesSettings counterMenu= new CounterMenuGamesSettings();
    public String descriptionText="Настройка игр.";
    public String errorMessage ="";
    public List<SpyLocation> locations = new ArrayList<SpyLocation>();
    public CounterGamesSettings()
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
