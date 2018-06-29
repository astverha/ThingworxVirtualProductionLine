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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
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
        ClientConfigurator config = new ClientConfigurator();
        //server URI
        config.setUri("ws://iottest07:80/Thingworx/WS");
        //appKey
        config.setAppKey("434cfef9-9993-478f-ad49-0738fb8948b3");
        //ignore ssl errors
        config.ignoreSSLErrors(true);
        
        //Build the things by reading the XML file
        List<ThingProperty> assetProps = new ArrayList<ThingProperty>();
        assetProps.add(new ThingProperty("Temperature", "20"));
        assetProps.add(new ThingProperty("Humidity", "20"));

        try {
            ProductLineClient client = new ProductLineClient(config);
            
            AssetThing thing = new AssetThing("Asset_TestAssetArne", "", client, assetProps);

            client.start();

            if (client.waitForConnection(20000)) {
                LOG.info("\n\n---- The {} is now Connected ----\n", client.toString());

                //bind ArneThing
                client.bindThing(thing);
                if (client.isConnected()) {
                    LOG.info("\n\n---- Thing is connected ----\n\n");

                    for (int i = 0; i < 1000; i++) {
                        //even scannen naar nieuwe property waarden voor thing
                        thing.processScanRequest();

                        //LOG.info("++++++++++++++++++++++++++++++++" + thing.getProperty("Temperature").toString() + "+++++++++++++++++++++++++++++++");
                        //read props from thingworx platform
                        InfoTable result = client.readProperty(ThingworxEntityTypes.Things, "Asset_TestAssetArne", "Temperature", 10000);
                        String temp = result.getFirstRow().getStringValue("Temperature");
                        result = client.readProperty(ThingworxEntityTypes.Things, "ArneThing2", "Humidity", 10000);
                        String hum = result.getFirstRow().getStringValue("Humidity");
                        LOG.info("\n\n---- temp: {} - hum: {} ----\n", temp, hum);

                        //write props
                        //invoke services
                        
                        // effe wachten
                        TimeUnit.SECONDS.sleep(5);
                    }

                } else {
                    LOG.info("\n\n---- Thing is not connected :( ----\n\n");
                }
            } else {
                LOG.warn("\n\nClient did not connect within 30 seconds. Exiting...\n");
            }

            client.shutdown();
        } catch (Exception e) {
            LOG.error("\n\nAn exception occurred while initializing the client.\n", e);
        }

        LOG.info("\n\n---- ArneThingClient is done. Exiting... ----\n");
    }
}
