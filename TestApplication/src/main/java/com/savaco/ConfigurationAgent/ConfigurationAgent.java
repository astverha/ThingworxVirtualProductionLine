package com.savaco.ConfigurationAgent;

import com.savaco.testapplication.ProductLineClient;
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
    private ProductLineClient client;
    
    /**
     * Initializes the ConfigurationClass. Supply the path to the XML file.
     * @param file 
     */
    public ConfigurationAgent(String file) {
        try {
            ClassLoader loader = getClass().getClassLoader();
            File xmlFile = new File(loader.getResource(file).getFile());
            
            lines = ReadXML.read(xmlFile);
            this.serverName = ReadXML.getServerName();
            this.appKey = ReadXML.getAppKey();
            
            client = new ProductLineClient(this, this.getConfiguration());
            
        } catch (Exception e) {
            LOG.error("TESTLOG ---- There was an error reading the file \'{}\'", file);
        }
    }
    
    /**
     * Returns the configuration variables needed for a Thingworx connection.
     * @return 
     */
    public ClientConfigurator getConfiguration() {
        ClientConfigurator config = new ClientConfigurator();
        config.setUri(serverName);
        config.setAppKey(appKey);
        config.ignoreSSLErrors(true);
        return config;
    }
    
    /**
     * Returns the assets constructed based on the XML as Virtual Things (AssetThing)
     * @return 
     */
    public List<AssetThing> getAssetsAsThings() {
        List<AssetThing> things = new ArrayList<>();
        try {
            for (Line line : lines) {
                AssetThing lineThing = new AssetThing(line.getThingName(), line.getDescription(), this.getClient(), line.getProperties());
                things.add(lineThing);
                for (Asset asset : line.getAssets()) {
                    AssetThing assetThing = new AssetThing(asset.getThingName(), asset.getDescription(), this.getClient(), asset.getProperties());
                    things.add(assetThing);
                }
                
            }
        } catch (Exception e) {
            LOG.error("Exception occurred while creating assets.");
        }
        return things;
    }
    
    public String getServerName() {
        return serverName;
    }
    
    public String getAppKey() {
        return appKey;
    }
    
    public ProductLineClient getClient() {
        return client;
    }
}
