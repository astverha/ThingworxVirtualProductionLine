package application;

import com.stage.client.ThingworxClient;
import configuration.ConfigurationAgent;
import java.util.concurrent.TimeUnit;
import org.slf4j.LoggerFactory;

public class RunApplication {

    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(RunApplication.class);

    public static void main(String[] args) {
        //Initialization of ConfigurationAgent (reads xml file, creates and exposes assetThings)
        ConfigurationAgent agent = new ConfigurationAgent("configuration.xml");
        try {
            //Initialization of ThingworxClient (responsible for connection to thingworx)
            ThingworxClient client = new ThingworxClient(agent);
            agent.setClient(client);
            
            //Sets the initial values of the things in Thingworx
            client.initThingworx();
            
        } catch (Exception e) {
            LOG.error("NOTIFICATIE [ERROR] - {} - An exception occurred while initializing the client.", RunApplication.class);
        }
    }
}
