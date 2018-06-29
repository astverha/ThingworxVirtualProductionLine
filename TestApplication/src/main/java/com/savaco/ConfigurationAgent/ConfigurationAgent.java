/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.savaco.ConfigurationAgent;

import com.savaco.testapplication.AssetThing;
import com.savaco.testapplication.ProductLineClient;
import com.savaco.testapplication.ReadXML;
import com.savaco.testapplication.ThingProperty;
import com.thingworx.communications.client.ClientConfigurator;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author Administrator
 */
public class ConfigurationAgent {
    
    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(ConfigurationAgent.class);
    
    private String serverName;
    private String appKey;
    private List<Line> lines;
    private ProductLineClient client;
    
    public ConfigurationAgent(String file) {
        try {
            ClassLoader loader = getClass().getClassLoader();
            File xmlFile = new File(loader.getResource(file).getFile());
            
            lines = ReadXML.read(xmlFile);
            this.serverName = ReadXML.getServerName();
            this.appKey = ReadXML.getAppKey();
            
            client = new ProductLineClient(this.getConfiguration());
            
        } catch (Exception e) {
            LOG.error("There was an error reading the file \'{}\'", file);
            e.printStackTrace();
        }
    }
    
    public ClientConfigurator getConfiguration() {
        ClientConfigurator config = new ClientConfigurator();
        config.setUri(serverName);
        config.setAppKey(appKey);
        config.ignoreSSLErrors(true);
        return config;
    }
    
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
