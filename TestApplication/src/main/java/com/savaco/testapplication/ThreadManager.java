/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.savaco.testapplication;

import com.savaco.ConfigurationAgent.AssetThing;
import com.savaco.ConfigurationAgent.ConfigurationAgent;
import com.savaco.ConfigurationAgent.ThingProperty;
import com.thingworx.types.primitives.IntegerPrimitive;
import com.thingworx.types.primitives.StringPrimitive;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Administrator
 */
public class ThreadManager {

    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);

    private List<Thread> threads;
    private ConfigurationAgent agent;
    private ProductLineClient client;
    private boolean pauseThread;

    public ThreadManager(ConfigurationAgent agent) {
        this.agent = agent;
        this.client = agent.getClient();
        this.threads = new ArrayList<>();
        pauseThread = false;
    }

    public void start() {
        try {
            if (client.waitForConnection(30000)) {
                List<AssetThing> assetThings = agent.getAssetsAsThings();
                for (AssetThing thing : assetThings) {
                    Thread agentThread = new Thread(new AgentThreadRunnable(thing, 5));
                    agentThread.start();
                }
            } else {
                LOG.warn("TESTLOG ---- ThreadManager client could not connect.");
            }
        } catch (Exception e) {
            LOG.warn("TESTLOG ---- Exception starting the ThreadManager.");
        }
    }

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

    public void pause() {
        pauseThread = true;
    }

    public void resume() {
        pauseThread = false;
    }

    private class AgentThreadRunnable implements Runnable {

        Random random = new Random();
        
        private final AssetThing thing;
        private final int sleepTime;

        public AgentThreadRunnable(AssetThing thing, int sleepTime) {
            this.thing = thing;
            this.sleepTime = sleepTime;
        }

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
                                int currProdRate = 0;
                                int sign = random.nextInt(2);
                                if(sign == 0){
                                    sign = -1;
                                }
                                for (ThingProperty tp : this.thing.getDevice_Properties()) {
                                    if (tp.getPropertyName().equals("ProductionRate")) {
                                        currProdRate = Integer.parseInt(tp.getValue());
                                    }
                                }
                                //LOG.info("TESTLOG ---- Change: " + currProdRate*0.1*sign);
                                this.thing.simulateNewData((int) (currProdRate+= currProdRate*0.1*sign));
                                
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
