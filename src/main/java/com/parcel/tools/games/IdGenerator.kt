package com.parcel.tools.games

import com.parcel.tools.games.games.cards.CardsSessionManager
import com.parcel.tools.games.games.mafia.MafiaSessionManager
import com.parcel.tools.games.games.spy.SpySessionManager
import com.parcel.tools.games.games.thirtyyears.ThirtyYearsSessionManager

/**
 * Генератор Id сессий игры.
 *  - генерирует уникальные id игры
 *  - id уникальны не только для конкретной игры но и для всехз игр
 */
object IdGenerator {


    /**
     * Счетчик id шниколв.
     */
    private var idCounter = 0x99L

    /**
     * Сгенерировать Id игры
     */
    @Synchronized
    fun generate(): Long
    {
        fun generate() = System.currentTimeMillis()%100000
        val idInUse = getIdsInUse()
        var id = generate()
         while(idInUse.contains(id))
         {
             id = generate()
         }

        return id//это внештатная ситуация возможна при ddos атаке
    }


    /**
     * Получить список всех используемых в данный момент id.
     */
    private fun getIdsInUse():ArrayList<Long>
    {
        val mafiaId = MafiaSessionManager.getGameIdList()
        val spyId = SpySessionManager.getGameIdList()
        val cardsId = CardsSessionManager.getGameIdList()
        val thirtyYearsId = ThirtyYearsSessionManager.getGameIdList()
        val idInUse = ArrayList<Long>()
        mafiaId.forEach { if(!idInUse.contains(it)) idInUse.add(it) }
        spyId.forEach { if(!idInUse.contains(it)) idInUse.add(it) }
        cardsId.forEach { if(!idInUse.contains(it)) idInUse.add(it) }
        thirtyYearsId.forEach { if(!idInUse.contains(it)) idInUse.add(it) }
        return idInUse
    }
}