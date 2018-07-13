package com.stage.client;

import com.thingworx.communications.client.ConnectedThingClient;
import configuration.AssetThing;
import configuration.ConfigurationAgent;
import java.util.concurrent.TimeUnit;
import org.slf4j.LoggerFactory;

public class ThingworxClient extends ConnectedThingClient {

    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(ThingworxClient.class);

    private final ConfigurationAgent agent;
    private int simulationSpeed;
    private ThreadManager tManager;

    public ThingworxClient(ConfigurationAgent agent) throws Exception {
        super(agent.getConfiguration());
        this.agent = agent;
    }

    public void initThingworx() {
        try {
            if (this.waitForConnection(20000)) {
                for (AssetThing myThing : this.agent.getThings()) {
                    this.bindThing(myThing);
                    if (this.isConnected()) {
                        myThing.initializeProperties(this);
                    }
                }
                LOG.info("NOTIFICATIE [INFO] - {} - AssetThings succesfully initialized.", ThingworxClient.class);
            } else {
                LOG.warn("NOTIFICATIE [WARNING] - {} - Timed out waiting for client to connect to Thingworx (initThingworx).", ThingworxClient.class);
            }
        } catch (Exception e) {
            LOG.error("NOTIFICATIE [ERROR] - {} - Failed to initialize things (initThingworx).", ThingworxClient.class);
        }
    }

    public void startSimulation(int runTime) {
        try {
            tManager = new ThreadManager(agent);
            tManager.start();
            LOG.info("NOTIFICATIE [INFO] - {} - ThreadManager succesfully started. Expected runtime: {} minutes.", ThingworxClient.class, runTime);
            TimeUnit.MINUTES.sleep(runTime);
        } catch (Exception e) {
            LOG.error("NOTIFICATIE [ERROR] - {} - Failed to start simulation (startSimulation).", ThingworxClient.class);
        }

    }

    public void togglePause() {
        tManager.togglePause();
    }
}
