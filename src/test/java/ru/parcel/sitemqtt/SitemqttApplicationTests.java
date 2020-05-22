package ru.parcel.sitemqtt;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import ru.parcel.sitemqtt.games.gamesession.GameSessionVoteTest;

@SpringBootTest
public class SitemqttApplicationTests {

    private GlobalRandomizerTest grt = new GlobalRandomizerTest();
    private GameSessionVoteTest gameSessionVoteTest = new GameSessionVoteTest();


    @Test
    public void contextLoads() {

        grt.test();
        gameSessionVoteTest.test();

    }




}
