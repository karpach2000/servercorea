package com.parcel.tools.spy

object GlobalRandomiser {

    private val previousValues = ArrayList<Int>()
    private val memory = 10
    private val itiration = 520
    private val random = java.util.Random()

    fun getRundom(area: Int):Int
    {

        var value = 0
        for(i in 0..itiration) {
            value = random.nextInt(area - 1)
            if(!previousValues.contains(value)) {
                previousValues.add(value)
                if(previousValues.size> memory)
                    previousValues.remove(previousValues[0])
                return value
            }
        }
        return value

    }
}