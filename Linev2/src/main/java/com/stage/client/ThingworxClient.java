package com.stage.client;

import com.thingworx.communications.client.ClientConfigurator;
import com.thingworx.communications.client.ConnectedThingClient;
import com.thingworx.types.primitives.IntegerPrimitive;
import configuration.AssetThing;
import configuration.ConfigurationAgent;
import configuration.ThingProperty;
import org.slf4j.LoggerFactory;
import utils.Utilities;

public class ThingworxClient extends ConnectedThingClient {

    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(ThingworxClient.class);

    private final ConfigurationAgent agent;

    public ThingworxClient(ConfigurationAgent agent) throws Exception {
        super(agent.getConfiguration());
        this.agent = agent;
    }

    public void initThingworx() {
        try {
            if (this.waitForConnection(20000)) {
                for(AssetThing myThing : this.agent.getThings()){
                    this.bindThing(myThing);
                    if(this.isConnected()){
                        myThing.initializeProperties();
                    }
                }
            }
        } catch (Exception e) {
            LOG.error("NOTIFICATIE [ERROR] - {} - Timed out waiting for client to connect to Thingworx.", ThingworxClient.class);
        }
    }

}
