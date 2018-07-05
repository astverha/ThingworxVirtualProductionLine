package com.stage.client;

import configuration.AssetThing;
import configuration.ConfigurationAgent;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.LoggerFactory;

public class ThreadManager {

    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(ThreadManager.class);

    private final List<Thread> threads;
    private final ConfigurationAgent agent;
    private final ThingworxClient client;
    private boolean pauseThread;

    public ThreadManager(ConfigurationAgent agent) {
        this.agent = agent;
        this.client = agent.getClient();
        threads = new ArrayList<>();
        pauseThread = false;
    }

    //Start threads
    public void start() {
        try {
            if (client.waitForConnection(30000)) {
                for (AssetThing thing : agent.getThings()) {
                    if (!thing.getName().contains("Line")) {
                        Thread thingThread = new Thread(new AgentThreadRunnable(thing, 5));
                    }
                }
                LOG.info("NOTIFICATIE [INFO] - {} - Threads succesfully started. Refresh interval: {} seconds.", ThreadManager.class, 5);
            }
        } catch (Exception e) {
            LOG.error("NOTIFICATIE [ERROR] - {} - An exception occurred while starting the threads.", ThreadManager.class);
        }
    }

    private class AgentThreadRunnable implements Runnable {

        private final AssetThing thing;
        private final int speed;

        private AgentThreadRunnable(AssetThing thing, int speed) {
            this.speed = speed;
            this.thing = thing;
        }

        @Override
        public void run() {
            while (!client.isShutdown()) {
                if (pauseThread) {
                    //TO DO
                } else {
                    try {
                        client.bindThing(this.thing);
                        if (client.isConnected()) {
                            try {
                                this.thing.simulateData();
                            } catch (Exception e) {
                                LOG.error("NOTIFICATIE [ERROR] - {} - Exception occurred while simulating new data.", AgentThreadRunnable.class);
                            }
                        }
                    } catch (Exception e) {
                        LOG.error("NOTIFICATIE [ERROR] - {} - Thing {} could not be bound to the client.", AgentThreadRunnable.class, this.thing.getName());
                    }
                }
            }
        }

    }
}
