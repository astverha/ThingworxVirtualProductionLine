package application;

import com.stage.client.ThingworxClient;
import configuration.ConfigurationAgent;
import org.slf4j.LoggerFactory;

public class RunApplication {

    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(RunApplication.class);

    public static void main(String[] args) {
        //Initialization of ConfigurationAgent (reads xml file, creates and exposes assetThings)
        ConfigurationAgent agent = new ConfigurationAgent("carproductionline.xml");
        try {
            //Initialization of ThingworxClient (responsible for connection to thingworx)
            ThingworxClient client = agent.getClient();
            client.start();
            
            //Sets the initial values of the things in Thingworx
            client.initThingworx();
            
            //Starts the simulation for <int> minutes
            client.startSimulation(15);
        } catch (Exception e) {
            LOG.error("NOTIFICATIE [ERROR] - {} - An exception occurred while initializing the client.", RunApplication.class);
        }
    }
}
