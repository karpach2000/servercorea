package ru.parcel.sitemqtt

import com.parcel.tools.games.GlobalRandomiser
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
open class GlobalRandomizerTest {

    /**
     * Количество раз которое мы запускаем рандомайзер.
     */
    private val testsCount = 10;

    fun test()
    {

        println("START: GlobalRandomizerTest")
        assert(test01())
        assert(test02())
        assert(test09())
        println("END: GlobalRandomizerTest")
    }

    /**
     * Тестируем генерацию рандомных значений 0 и 1.
     * Метод запускается testsCount раз, хотя бы раз должно выпасть каждое значение.
     */
    private fun test01() :Boolean
    {
        println("Test01")
        val ansers = ArrayList<Int>()
        for(i in 0..testsCount)
        {
            val a= GlobalRandomiser.getRundom(1)
            ansers.add(a)
            println("$i) $a")
        }
        return if(ansers.contains(0) && !ansers.contains(1)) {
            println("Test good.")
            true
        }
        else
        {
            println("Test failed.")
            false
        }
    }
    /**
     * Тестируем генерацию рандомных значений 0, 1 и 2.
     * Метод запускается testsCount раз, хотя бы раз должно выпасть каждое значение.
     */
    private fun test02() :Boolean
    {
        println("Test02")
        val ansers = ArrayList<Int>()
        for(i in 0..testsCount)
        {
            val a= GlobalRandomiser.getRundom(2)
            ansers.add(a)
            println("$i) $a")
        }
        return if(ansers.contains(0) && ansers.contains(1) && !ansers.contains(2)) {
            println("Test good.")
            true
        }
        else
        {
            println("Test failed.")
            false
        }
    }

    /**
     * Тестируем генерацию рандомных значений от 0 до 9.
     * Метод всегда возвращает тру, проверять следует вывод в лог.
     */
    private fun test09(): Boolean
    {
        println("Test09")
        val ansers = ArrayList<Int>()
        for(i in 0..testsCount)
        {
            val a= GlobalRandomiser.getRundom(9)
            ansers.add(a)
            println("$i) $a")
        }
        return true
    }

}