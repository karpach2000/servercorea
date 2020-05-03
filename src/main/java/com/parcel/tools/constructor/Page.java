package com.parcel.tools.constructor;

public class Page {
    public MainMenu mainMenu = new MainMenu();

    public String hay = "привет";
    public Object pageBody;//тело страницы

    public Page() {
        init();

    }

    public Page(Object pageBody) {
        this.pageBody = pageBody;
        init();

    }

    private void init() {
        //****Основное меню*****//
        mainMenu.addItem("Главная", "/");
        mainMenu.addItem("Игры", "/games");
        mainMenu.addItem("Настройка игр", "/games_settings");
        //mainMenu.addItem("Администрирование","/admin");
        mainMenu.addItem("Logout", "/logout");
        //mainMenu.addItem("MQTT сервер","/mqttServer");


    }
}
