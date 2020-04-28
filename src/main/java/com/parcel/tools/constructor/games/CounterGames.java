package com.parcel.tools.constructor.games;

import com.parcel.tools.constructor.games.cards.CounterGamesCards;

/**
 * Класс который в дальнейшем серриализуется в WEB страницу при помощи шаблонизатора.
 */
public class CounterGames {
    public CounterMenuGames counterMenu= new CounterMenuGames();
    public String descriptionText="Игры";
    public CounterGamesCards counterGamesSpy = new CounterGamesCards();


    public CounterGames() {
        counterMenu.addItem("Карточки","cards");
    }
}
