package com.parcel.tools.constructor.bodies.counter;

public class Counter {
    public CounterMenu counterMenu= new CounterMenu();
    public String descriptionText="Здесь предсатвленны различаные утилиты, полезные при разборе протоколов.";

    public Counter()
    {
        counterMenu.addItem("Что это?","main");
        counterMenu.addItem("Перевод между системами счисления.","notation");
        counterMenu.addItem("Расчет контрольных сумм.", "crc");
    }
}
