package ru.parcel.sitemqtt;

import com.parcel.tools.SitemqttApplication;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import ru.parcel.sitemqtt.games.gamesession.GameSessionVoteTest;
import ru.parcel.sitemqtt.games.gamesession.thirtyyears.ThirtyYearsTest;

@SpringBootTest
public class SitemqttApplicationTests {

    private GlobalRandomizerTest grt = new GlobalRandomizerTest();
    private GameSessionVoteTest gameSessionVoteTest = new GameSessionVoteTest();
    private ThirtyYearsTest thirtyYearsTest = null;

    public SitemqttApplicationTests()
    {
        SpringApplication.run(SitemqttApplication.class);
        thirtyYearsTest = new ThirtyYearsTest();
    }

    @Test
    public void contextLoads() {

        grt.test();
        gameSessionVoteTest.test();
        thirtyYearsTest.test();


        try {
            Thread.sleep(11000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }




}
