package com.parcel.tools.games.games.thirtyyears.settings

import com.google.gson.GsonBuilder
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths

/**
 * Настройки игры 30 лет.
 */
object ThirtyYearsSettings {

    private val folder = "Settings/ThirtyYearsSettings/"
    private val pointsFileName = "ThirtyYearsSettings.json"


    private val logger = org.apache.log4j.Logger.getLogger(ThirtyYearsSettings::class.java!!)

    /**
     * Объект содержит количество очков начисляемое пользователю за то или иное действие.
     */
    var points = SettingsData()


    /**
     * Обновить настройки из файла.
     */
    fun update() : Boolean
    {

        try {
            val file = File(folder + pointsFileName)
            //проверяем, что если файл не существует то создаем его
            return if (file.exists()) {
                fromJson(file.readText())
            } else {
                logger.warn("File not found")
                save()
                false
            }
        }
        catch (e: java.lang.Exception)
        {
            logger.error("Error settings file reader. ${e.message}")
            return false
        }

    }


    private fun toJson():String{
        var builder =  GsonBuilder()
        var gson = builder.create()
        return gson.toJson(points)
    }

    /**
     * Получает поля объекта mutableData из JSON строки.
     * @return true - если старые и новые значения оказались равны, false - если не равны
     */
    private fun fromJson(json : String) :Boolean
    {
        val builder = GsonBuilder()
        points = builder.create().fromJson(json, SettingsData::class.java)
        return true
    }


    /**
     * Сохранить настройки в файл.
     */
    private fun save(): Boolean
    {
        try {
            val file = File(folder + pointsFileName)
            val path = Paths.get(folder)
            //проверяем наличие каталога, если катлона нет, создаем его
            if (!Files.exists(path)) {
                Files.createDirectories(path)
            }

            //проверяем, что если файл не существует то создаем его
            if (file.exists())
                file.delete()
            file.createNewFile()
            logger.warn("New settings file created.")
            file.writeText(toJson())

            return true
        }
        catch (e: java.lang.Exception)
        {
            logger.error("Settings file creating error ${e.message}")
            return false
        }
    }

}