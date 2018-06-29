/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.savaco.testapplication;

import com.savaco.ConfigurationAgent.ConfigurationAgent;
import com.thingworx.communications.client.ClientConfigurator;
import com.thingworx.communications.client.ConnectedThingClient;
import com.thingworx.relationships.RelationshipTypes.ThingworxEntityTypes;
import com.thingworx.types.InfoTable;
import com.thingworx.types.primitives.IntegerPrimitive;
import com.thingworx.types.primitives.StringPrimitive;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Administrator
 */
public class ProductLineClient extends ConnectedThingClient {

    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(ProductLineClient.class);

    public ProductLineClient(ClientConfigurator config) throws Exception {
        super(config);
    }

    public static void main(String[] args) {
        ConfigurationAgent agent = new ConfigurationAgent("configuration.xml");

        try {
            ProductLineClient client = agent.getClient();

            List<AssetThing> things = agent.getAssetsAsThings();
            client.start();

            if (client.waitForConnection(20000)) {
                LOG.warn("TESTLOG ---- The {} is now Connected ----", client.toString());
                for (AssetThing thing : things) {
                    client.bindThing(thing);

                    if (client.isConnected()) {
                        LOG.warn("TESTLOG ---- {} is connected", thing.getName());
                        TimeUnit.SECONDS.sleep(5);
                        try {
                            for (ThingProperty tp : thing.getDevice_Properties()) {
                                if (StringUtils.isNumeric(tp.getValue())) {
                                    LOG.warn("TESTLOG ---- \t Property, number: {} ({})", tp.getPropertyName(), tp.getValue());
                                    thing.setPropertyValue(tp.getPropertyName(), new IntegerPrimitive(Integer.parseInt(tp.getValue())));
                                } else {
                                    LOG.warn("TESTLOG ---- \t Property, string: {} ({})", tp.getPropertyName(), tp.getValue());
                                    thing.setPropertyValue(tp.getPropertyName(), new StringPrimitive(tp.getValue()));
                                }

                            }
                            thing.updateSubscribedProperties(10000);
                        } catch (Exception e) {
                            LOG.warn("Exception occurred while updating properties. (ProductLineClient.java)");
                            e.printStackTrace();
                        }

                        //Invoke actions you need to do
                        //Threading
                    } else {
                        LOG.warn("TESTLOG ---- Thing is not connected :( ----");
                    }

                }

            } else {
                LOG.warn("\n\nClient did not connect within 30 seconds. Exiting...\n");
            }
            TimeUnit.MINUTES.sleep(2);
            client.shutdown();
        } catch (Exception e) {
            LOG.warn("\n\nAn exception occurred while initializing the client.\n", e);
            e.printStackTrace();
        }

        LOG.warn("\n\n---- ProductLineClient is done. Exiting... ----\n");
    }
}
