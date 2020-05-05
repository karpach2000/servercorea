package com.parcel.tools.constructor.games;

import com.parcel.tools.constructor.games.mafia.CounterGamesMafia;
import com.parcel.tools.constructor.games.spy.CounterGamesSpy;

/**
 * Класс который в дальнейшем серриализуется в WEB страницу при помощи шаблонизатора.
 */
public class CounterGames {
    public CounterMenuGames counterMenu= new CounterMenuGames();
    public String descriptionText="Игры.";
    public CounterGamesSpy counterGamesSpy =new CounterGamesSpy();
    public CounterGamesMafia counterGamesMafia =new CounterGamesMafia();

    public CounterGames()
    {
        counterMenu.addItem("Шпион.","/games/spy");
        counterMenu.addItem("Мафия.","/games/mafia");
        counterMenu.addItem("Карточки.","/games/cards");

    }
}
