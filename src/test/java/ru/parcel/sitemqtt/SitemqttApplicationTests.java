package ru.parcel.sitemqtt;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
public class SitemqttApplicationTests {

    @Test
    public void contextLoads() {
        GlobalRandomizerTest grt = new GlobalRandomizerTest();
        grt.test();

    }




}
