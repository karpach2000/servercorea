package com.parcel.tools.constructor.bodies.games;

import com.parcel.tools.constructor.bodies.Counter;
import com.parcel.tools.constructor.bodies.games.mafia.CounterGamesMafia;
import com.parcel.tools.constructor.bodies.games.spy.CounterGamesSpy;

/**
 * Класс который в дальнейшем серриализуется в WEB страницу при помощи шаблонизатора.
 */
public class CounterGames extends Counter {

    public String descriptionText="Игры.";
    public CounterGamesSpy counterGamesSpy =new CounterGamesSpy();
    public CounterGamesMafia counterGamesMafia =new CounterGamesMafia();

}
