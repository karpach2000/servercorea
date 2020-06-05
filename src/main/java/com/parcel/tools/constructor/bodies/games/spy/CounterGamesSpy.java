package com.parcel.tools.constructor.bodies.games.spy;

import com.parcel.tools.games.games.spy.SpySessionManager;
import com.parcel.tools.games.games.spy.database.SpyLocation;

import java.util.ArrayList;
import java.util.List;

public class CounterGamesSpy {
    public List<SpyLocation> locations = new ArrayList<SpyLocation>();
    public String gameDescription = "В каждом раунде игроки оказываются в какой-то локации," +
            " но один (или несколько) неизбежно оказывается шпионом, который не знает, где находится." +
            " Его задача — разговорить других игроков, определить локацию и не разоблачить себя. " +
            "Остальные, в свою очередь, пытаются обтекаемо дать понять «своим», что знают, где находится, " +
            "и сузить круг подозреваемых.";
    public String tooltipWhenAddingUsers = "Дождитесь когда все пользователи будут добавлены. " +
            "После нажмите кнопку 'Начать игру'";
    public String tooltipDuringTheGame =
            "Задача игроков - за отведенное время найти и ЕДИНОГЛАСНО указать на шпиона, " +
                    "задача шпиона - отгадать локацию, в которой находятся игроки. " +
            "Для этого все игроки по очереди задают друг другу вопросы, связанные с этой локацией.";



    public CounterGamesSpy()
    {
        try {
            locations = SpySessionManager.INSTANCE.getMainLocationList();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

}
