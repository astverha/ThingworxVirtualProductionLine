/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.savaco.testapplication;

import com.savaco.ConfigurationAgent.AssetThing;
import com.savaco.ConfigurationAgent.ConfigurationAgent;
import com.savaco.ConfigurationAgent.ThingProperty;
import com.thingworx.communications.client.things.VirtualThing;
import com.thingworx.types.primitives.IntegerPrimitive;
import com.thingworx.types.primitives.StringPrimitive;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Administrator
 */
public class ThreadManager {

    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);

    private List<Thread> threads;
    private final ConfigurationAgent agent;
    private final ProductLineClient client;

    public ThreadManager(ConfigurationAgent agent) {
        this.agent = agent;
        this.client = agent.getClient();
        this.threads = new ArrayList<>();
    }

    public void start() {
        LOG.info("TESTLOG ---- Starting the agent");

        try {
            client.start();

            if (client.waitForConnection(30000)) {
                LOG.info("TESTLOG ---- ThreadManager client IS NOW CONNECTED!");

                //start a thread for each asset
                List<AssetThing> assetThings = agent.getAssetsAsThings();
                for (AssetThing thing : assetThings) {
                    Thread agentThread = new Thread(new AgentThreadRunnable(thing, 1));
                    agentThread.start();
                }
                //wait for all threads
                /*for(Thread thread : this.threads){
                    thread.join();
                }*/
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

    private class AgentThreadRunnable implements Runnable {

        private final AssetThing thing;
        private final int sleepTime;

        public AgentThreadRunnable(AssetThing thing, int sleepTime) {
            this.thing = thing;
            this.sleepTime = sleepTime;
        }

        @Override
        public void run() {
            Random random = new Random();
            while (!client.isShutdown()) {
                try {
                    //bind the asset to the client
                    client.bindThing(this.thing);
                    //LOG.info("TESTLOG ---- Thing {} is now bound to the client.", this.thing.getName());

                    //check if the client is connected
                    if (client.isConnected()) {
                        //LOG.warn("TESTLOG ---- {} is connected.", thing.getName());
                        try {
                            //set new values for the properties of the thing associated with this runnable thread
                            for (ThingProperty tp : this.thing.getDevice_Properties()) {
                                if (!tp.getPropertyName().equals("status")) {
                                    if (StringUtils.isNumeric(tp.getValue())) {
                                        int currVal = Integer.parseInt(tp.getValue());
                                        int newVal = currVal + 1;
                                        tp.setValue("" + newVal);
                                        this.thing.setPropertyValue(tp.getPropertyName(), new IntegerPrimitive(newVal));
                                    } else {
                                        this.thing.setPropertyValue(tp.getPropertyName(), new StringPrimitive(tp.getValue()));
                                    }
                                }

                            }
                            this.thing.updateSubscribedProperties(10000);
                            LOG.info("TESTLOG ---- {} was updated, {} thread going to sleep now.", thing.getName());
                            //sleep "sleeptime" seconds to update again
                            Thread.sleep(this.sleepTime * 1000);
                        } catch (Exception e) {
                            LOG.warn("TESTLOG ---- Exception occurred while updating properties. (ThreadManager.java)");
                            e.printStackTrace();
                        }
                    } else {
                        LOG.warn("TESTLOG ---- Thing is not connected :(");
                    }
                } catch (Exception e) {
                    LOG.warn("TESTLOG ---- Thing {} could not be bound to the client.", this.thing.getName());
                }
            }
            LOG.info("TESTLOG ---- The client is shutdown. Finishing {} thread.", this.thing.getName());
        }
    }
}
