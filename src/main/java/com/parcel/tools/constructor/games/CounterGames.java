package com.parcel.tools.constructor.games;

import com.parcel.tools.constructor.games.spy.CounterGamesSpy;
import com.parcel.tools.spy.SpySessionManager;
import com.parcel.tools.spy.database.SpyLocation;

import java.util.ArrayList;
import java.util.List;

/**
 * Класс который в дальнейшем серриализуется в WEB страницу при помощи шаблонизатора.
 */
public class CounterGames {
    public CounterMenuGames counterMenu= new CounterMenuGames();
    public String descriptionText="Игры.";
    public CounterGamesSpy counterGamesSpy =new CounterGamesSpy();

    public CounterGames()
    {
        counterMenu.addItem("Шпион.","ropeSlack");

    }
}
