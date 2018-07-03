package com.savaco.testapplication;

import com.savaco.ConfigurationAgent.ConfigurationAgent;
import java.util.concurrent.TimeUnit;

public class TestApplication {
    public static void main(String[] args) {
        ConfigurationAgent config = new ConfigurationAgent("configuration.xml");
        ProductLineClient client = config.getClient();
        try {
            client.start();
            client.startInitialization();
            TimeUnit.SECONDS.sleep(5);
            client.startSimulation();
            TimeUnit.MINUTES.sleep(15);
            client.shutdown();
        } catch(Exception e){
            System.out.println("TESTLOG ---- Failed to start application.");
        }
    }
}
