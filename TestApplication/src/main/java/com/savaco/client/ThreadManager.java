package com.savaco.client;

import com.savaco.client.ProductLineClient;
import com.savaco.configurationagent.AssetThing;
import com.savaco.configurationagent.ConfigurationAgent;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.slf4j.LoggerFactory;

public class ThreadManager {

    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);

    private final List<Thread> threads;
    private final ConfigurationAgent agent;
    private final ProductLineClient client;
    private boolean pauseThread;

    /**
     * 
     * @param agent 
     */
    public ThreadManager(ConfigurationAgent agent) {
        this.agent = agent;
        this.client = agent.getClient();
        this.threads = new ArrayList<>();
        pauseThread = false;
    }

    /**
     * Starts threads.
     */
    public void start() {
        try {
            if (client.waitForConnection(30000)) {
                List<AssetThing> assetThings = agent.getAssetsAsThings();
                for (AssetThing thing : assetThings) {
                    if (!thing.getName().contains("Line")) {
                        Thread agentThread = new Thread(new AgentThreadRunnable(thing, 5));
                        agentThread.start();
                    }
                }
            } else {
                LOG.warn("TESTLOG ---- ThreadManager client could not connect.");
            }
        } catch (Exception e) {
            LOG.warn("TESTLOG ---- Exception starting the ThreadManager.");
        }
    }

    /**
     * Stops threads.
     */
    public void stop() {
        try {
            for (Thread thread : this.threads) {
                thread.stop();
            }
            LOG.info("TESTLOG ---- ThreadManager is stopped");
        } catch (Exception e) {
            LOG.error("TESTLOG ---- An exception during stopping the ThreadManager", e);
        }
    }

    /**
     * Pauses thread execution.
     */
    public void pause() {
        pauseThread = true;
    }

    /**
     * Resumes thread execution.
     */
    public void resume() {
        pauseThread = false;
    }
    
    private class AgentThreadRunnable implements Runnable {

        Random random = new Random();

        private final AssetThing thing;
        private final int sleepTime;

        /**
         * 
         * @param thing
         * @param sleepTime 
         */
        public AgentThreadRunnable(AssetThing thing, int sleepTime) {
            this.thing = thing;
            this.sleepTime = sleepTime;
        }

        /**
         * Thread execution method. Contains a loop simulating the data.
         */
        @Override
        public void run() {
            while (!client.isShutdown()) {
                if (pauseThread) {
                    try {
                        Thread.sleep(2);
                    } catch (InterruptedException ex) {
                        LOG.warn("TESTLOG ---- InterruptedException while pausing threads.");
                    }
                } else {
                    try {
                        client.bindThing(this.thing);
                        if (client.isConnected()) {
                            try {
                                //Random updates production rate (should be inputted)
                                int currProdRate = 0;
                                int sign = random.nextInt(2);
                                if (sign == 0) {
                                    sign = -1;
                                }
                                currProdRate = Integer.parseInt(thing.getPropertyByName("ProductionRate").getValue());
                                //this.thing.simulateNewData((int) (currProdRate+= currProdRate*0.05*sign));
                                int rand = random.nextInt(20);
                                if(rand == 1 && !this.thing.isDown()){
                                    this.thing.setDown(true);
                                    this.thing.breakThing();
                                } else {
                                    if(this.thing.isDown()){
                                        this.thing.restartThing(500);
                                    } else {
                                        this.thing.simulateNewData(currProdRate);
                                    }
                                }
                                //-------------------------------
                                this.thing.updateSubscribedProperties(10000);
                                //LOG.info("TESTLOG ---- {} was updated, {} thread going to sleep now.", thing.getName());
                                Thread.sleep(this.sleepTime * 1000);
                            } catch (Exception e) {
                                LOG.warn("TESTLOG ---- Exception occurred while updating properties. (ThreadManager.java)");
                                e.printStackTrace();
                            }
                        } else {
                            LOG.warn("TESTLOG ---- Thing is not connected :(");
                        }
                    } catch (Exception ex) {
                        LOG.warn("TESTLOG ---- Thing {} could not be bound to the client.", this.thing.getName());
                    }
                }
            }
            LOG.info("TESTLOG ---- The client is shutdown. Finishing {} thread.", this.thing.getName());
        }
    }
}
