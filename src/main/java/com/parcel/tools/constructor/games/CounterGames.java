package com.parcel.tools.constructor.games;

import com.parcel.tools.constructor.games.cards.CounterGamesCards;
import com.parcel.tools.constructor.games.spy.CounterGamesSpy;
import com.parcel.tools.constructor.games.mafia.CounterGamesMafia;


/**
 * Класс который в дальнейшем серриализуется в WEB страницу при помощи шаблонизатора.
 */
public class CounterGames {
    public CounterMenuGames counterMenu = new CounterMenuGames();
    public String descriptionText = "Игры";
    public CounterGamesCards counterGamesCards = new CounterGamesCards();
    public CounterGamesSpy counterGamesSpy = new CounterGamesSpy();
    public CounterGamesMafia counterGamesMafia = new CounterGamesMafia();


    public CounterGames() {
        counterMenu.addItem("Шпион", "spy");
        counterMenu.addItem("Мафия", "mafia");
        counterMenu.addItem("Карточки", "cards");
    }
}