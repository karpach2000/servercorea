package com.parcel.tools.constructor;

import java.util.ArrayList;

public abstract class Menu {




    /**
     * Класс названия меню.
     */
    public class Item
    {
        /**
         * Имя параметра.
         */
        public String name="";
        /**
         * Ссылка параметра
         */
        public String link ="";




        public Item(String name, String link)
        {
            this.name=name;
            this.link = link;
        }
        public Item(String name)
        {
            this.name=name;
        }
    }

    /**
     * Коллекция элементов меню
     */
    public Item[] items=new Item[0];



    public  Menu()
    {

    }

    public void addItem(String name, String lick)
    {

        addItem(new Item(name,lick));
    }

    public void addItem(String name)
    {

        addItem(new Item(name));
    }
    private void addItem(Item item)
    {

        ArrayList<Item> items =new ArrayList<>();
        for(Item i : this.items)
        {
            items.add(i);
        }
        items.add(item);
        this.items=items.toArray(this.items);
    }
}
