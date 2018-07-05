package configuration;

import com.stage.client.ThingworxClient;
import com.thingworx.communications.client.ClientConfigurator;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.LoggerFactory;

public final class ConfigurationAgent {

    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(ConfigurationAgent.class);

    private String serverName;
    private String appKey;
    private List<Line> lines;
    private List<AssetThing> things;

    private ThingworxClient client;

    public ConfigurationAgent(String fileName) {
        try {
            ClassLoader loader = this.getClass().getClassLoader();
            File xmlFile = new File(loader.getResource(fileName).getFile());
            
            lines = ReadXML.read(xmlFile);
            things = new ArrayList<>();

            this.serverName = ReadXML.getServerName();
            this.appKey = ReadXML.getAppKey();
            
            this.client = new ThingworxClient(this);
            
            this.classToThingConversion();
        } catch (Exception e) {
            LOG.error("NOTIFICATIE [ERROR] - {} - An exception occurred while reading the file.", ConfigurationAgent.class);
        }
    }

    public ClientConfigurator getConfiguration() {
        ClientConfigurator config = new ClientConfigurator();
        config.setUri(this.serverName);
        config.setAppKey(this.appKey);
        config.ignoreSSLErrors(true);
        return config;
    }

    public void classToThingConversion() {
        try {
            for (Line line : lines) {
                AssetThing lineThing = new AssetThing("Line_" + line.getName(), TypeEnum.LINE, line.getProperties(), client);
                things.add(lineThing);
                for (Asset asset : line.getAssets()) {
                    AssetThing assetThing = new AssetThing("Asset_" + asset.getName(), TypeEnum.ASSET, asset.getProperties(), client);
                    things.add(assetThing);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            LOG.error("NOTIFICATIE [ERROR] - {} - An exception occurred while converting assets to things.", ConfigurationAgent.class);
        }
    }

    public AssetThing getThingByName(String name) {
        for (AssetThing at : this.things) {
            if (at.getName().equals(name)) {
                return at;
            }
        }
        return null;
    }

    public List<AssetThing> getThings() {
        return things;
    }

    public ThingworxClient getClient() {
        return client;
    }

    public void togglePause() {
        client.togglePause();
    }
}
