/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.savaco.testapplication;

import com.savaco.ConfigurationAgent.ThingProperty;
import com.savaco.ConfigurationAgent.AssetThing;
import com.savaco.ConfigurationAgent.ConfigurationAgent;
import com.thingworx.communications.client.ClientConfigurator;
import com.thingworx.communications.client.ConnectedThingClient;
import com.thingworx.types.primitives.IntegerPrimitive;
import com.thingworx.types.primitives.StringPrimitive;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Administrator
 */
public class ProductLineClient extends ConnectedThingClient {

    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);

    private final ConfigurationAgent agent;

    public ProductLineClient(ConfigurationAgent agent, ClientConfigurator config) throws Exception {
        super(config);
        this.agent = agent;
    }

    public void startInitialization() {
        try {
            List<AssetThing> things = this.agent.getAssetsAsThings();

            if (this.waitForConnection(20000)) {
                for (AssetThing thing : things) {
                    this.bindThing(thing);
                    if (this.isConnected()) {
                        try {
                            for (ThingProperty tp : thing.getDevice_Properties()) {
                                if (StringUtils.isNumeric(tp.getValue())) {
                                    thing.setPropertyValue(tp.getPropertyName(), new IntegerPrimitive(Integer.parseInt(tp.getValue())));
                                } else {
                                    thing.setPropertyValue(tp.getPropertyName(), new StringPrimitive(tp.getValue()));
                                }
                            }
                            thing.updateSubscribedProperties(10000);
                        } catch (Exception e) {
                            LOG.warn("TESTLOG ---- Exception occurred while initializing properties. (ProductLineClient.java)");
                        }
                    } else {
                        LOG.warn("TESTLOG ---- Thing is not connected. (ProductLineClient.java)");
                    }
                }
            } else {
                LOG.warn("TESTLOG ---- Client did not connect within 30 seconds. Exiting...\n");
            }
        } catch (Exception e) {
            LOG.warn("TESTLOG ---- An exception occurred while initializing the client.\n", e);
        }
        LOG.warn("TESTLOG ---- ProductLineClient is done. Exiting...");
    }

    public void startSimulation() {
        try {
            ThreadManager tManager = new ThreadManager(agent);
            tManager.start();
            TimeUnit.SECONDS.sleep(30);
            tManager.pause();
            TimeUnit.SECONDS.sleep(15);
            tManager.resume();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
