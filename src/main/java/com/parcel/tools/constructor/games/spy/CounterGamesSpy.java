package com.parcel.tools.constructor.games.spy;

import com.parcel.tools.games.spy.SpySessionManager;
import com.parcel.tools.games.spy.database.SpyLocation;

import java.util.ArrayList;
import java.util.List;

public class CounterGamesSpy {
    public List<SpyLocation> locations = new ArrayList<SpyLocation>();
    public String gameDescription = "Описание правил и игры!";
    public String tooltipWhenAddingUsers = "Подсказка при добавлении пользователей";
    public String tooltipDuringTheGame = "Подсказка во время игры";



    public CounterGamesSpy()
    {
        try {
            locations = SpySessionManager.INSTANCE.getLocationList();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

}
