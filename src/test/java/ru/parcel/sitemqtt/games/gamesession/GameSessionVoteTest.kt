package ru.parcel.sitemqtt.games.gamesession

import com.parcel.tools.games.gamesession.GameSessionVote
import com.parcel.tools.games.gamesuser.GameUser
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class GameSessionVoteTest {

    class User(name: String): GameUser(name)

    private val gameUsers = ArrayList<User>()
    private lateinit var gsv : GameSessionVote<User>



    init {
        gameUsers.add(User("User_1"))//1
        gameUsers.add(User("User_0"))//0
        gameUsers.add(User("User_5"))
        gameUsers.add(User("User_3"))//3
        gameUsers.add(User("User_8"))
        gameUsers.add(User("User_2"))//2
        gameUsers.add(User("User_4"))
        gameUsers.add(User("User_6"))
        gameUsers.add(User("User_9"))
        gameUsers.add(User("User_7"))

        gsv = GameSessionVote(gameUsers)
    }

    fun test()
    {
        println("START: GameSessionVoteTest")
        vote1()
        assert(testResult1())
        gsv.clear()
        vote2()
        assert(testResult2())
        gsv.clear()
        vote3()
        assert(testResult3())
        gsv.clear()

        println("END: GameSessionVoteTest")
    }

    private fun vote1()
    {
        val votesOrder = ArrayList<String>()
        votesOrder.add("User_0")
        votesOrder.add("User_1")
        votesOrder.add("User_2")
        votesOrder.add("User_1")
        votesOrder.add("User_6")
        votesOrder.add("User_4")
        votesOrder.add("User_4")
        votesOrder.add("User_5")
        votesOrder.add("User_1")

        var i = 0
        votesOrder.forEach {
            gsv.vote(gameUsers[i].name!!, it)
            i++
        }
    }

    private fun vote2()
    {
        val votesOrder = ArrayList<String>()
        votesOrder.add("User_0")
        votesOrder.add("User_3")
        votesOrder.add("User_2")
        votesOrder.add("User_1")
        votesOrder.add("User_6")
        votesOrder.add("User_4")
        votesOrder.add("User_8")
        votesOrder.add("User_5")
        votesOrder.add("User_3")

        var i = 0
        votesOrder.forEach {
            gsv.vote(gameUsers[i].name!!, it)
            i++
        }
    }

    private fun vote3()
    {
        val votesOrder = ArrayList<String>()
        votesOrder.add("User_0")
        votesOrder.add("User_1")
        votesOrder.add("User_2")
        votesOrder.add("User_1")
        votesOrder.add("User_6")
        votesOrder.add("User_4")
        votesOrder.add("User_4")
        votesOrder.add("User_5")
        votesOrder.add("User_4")

        var i = 0
        votesOrder.forEach {
            gsv.vote(gameUsers[i].name!!, it)
            i++
        }
    }

    private fun testResult1():Boolean
    {
       return gsv.getLeader().name == "User_1"
    }

    private fun testResult2():Boolean
    {
        return gsv.getLeader().name == "User_3"
    }

    private fun testResult3():Boolean
    {
        return gsv.getLeader().name == "User_4"
    }

}