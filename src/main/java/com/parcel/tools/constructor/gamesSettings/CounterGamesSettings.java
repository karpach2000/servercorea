package com.parcel.tools.constructor.gamesSettings;

public class CounterGamesSettings {
    public CounterMenuGamesSettings counterMenu = new CounterMenuGamesSettings();
    public String descriptionText = "Настройка игр.";
    public String errorMessage = "";

    public CounterGamesSettings() {
        counterMenu.addItem("Карточки", "cards");

    }
}
