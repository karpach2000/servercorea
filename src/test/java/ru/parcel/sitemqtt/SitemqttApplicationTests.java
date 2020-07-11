package ru.parcel.sitemqtt;

import com.parcel.tools.SitemqttApplication;
import org.junit.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;
import ru.parcel.sitemqtt.games.gamesession.GameSessionVoteTest;
import ru.parcel.sitemqtt.games.gamesession.thirtyyears.ThirtyYearsGameTest;

@SpringBootTest
public class SitemqttApplicationTests {

    private GlobalRandomizerTest grt = new GlobalRandomizerTest();
    private GameSessionVoteTest gameSessionVoteTest = new GameSessionVoteTest();
    private ThirtyYearsGameTest thirtyYearsTest = null;

    public SitemqttApplicationTests()
    {
        SpringApplication.run(SitemqttApplication.class);
        thirtyYearsTest = new ThirtyYearsGameTest();
    }

    @Test
    public void contextLoads() {

        grt.test();
        gameSessionVoteTest.test();
        thirtyYearsTest.test();




    }




}
