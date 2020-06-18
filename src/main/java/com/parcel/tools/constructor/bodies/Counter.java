package com.parcel.tools.constructor.bodies;

import com.parcel.tools.constructor.bodies.games.CounterMenuGames;

public abstract class Counter {
    public CounterMenuGames counterMenu= new CounterMenuGames();
    public Counter()
    {
        counterMenu.addItem("Шпион","/games/spy");
        counterMenu.addItem("Мафия","/games/mafia");
        counterMenu.addItem("Карточки","/games/cards");
        counterMenu.addItem("Тридцатник","/games/thirtyyears");
    }
}
