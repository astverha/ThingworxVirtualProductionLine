/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.savaco.ConfigurationAgent;

import com.savaco.testapplication.ProductLineClient;
import com.savaco.testapplication.ThingProperty;
import java.io.File;
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

    public ConfigurationAgent(String file) {
        try {
            ClassLoader loader = getClass().getClassLoader();
            File xmlFile = new File(loader.getResource(file).getFile());
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = dbFactory.newDocumentBuilder();
            Document doc = builder.parse(xmlFile);

            doc.getDocumentElement().normalize();

            LOG.info("Started with parsing, root element is: {}", doc.getDocumentElement().getNodeName());

            //Information about the server
            NodeList nList = doc.getElementsByTagName("ServerConfiguration");

            for (int i = 0; i < nList.getLength(); i++) {
                Node nNode = nList.item(i);
                LOG.info("Current element is: {}", nNode.getNodeName());

                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;

                    this.serverName = eElement.getElementsByTagName("Server").item(0).getTextContent();
                    this.appKey = eElement.getElementsByTagName("AppKey").item(0).getTextContent();

                    LOG.info("ServerName: {}", this.serverName);
                    LOG.info("AppKey: {}", this.appKey);
                }
            }

            //Reading production line
            nList = doc.getElementsByTagName("ProductionLine");

            for (int i = 0; i < nList.getLength(); i++) {
                //ProductionLine
                Element productionLineNode = (Element) nList.item(i);
                
                String name = productionLineNode.getElementsByTagName("Name").item(0).getTextContent();
                String description = productionLineNode.getElementsByTagName("Description").item(0).getTextContent();
                
                Line productionLine = new Line(name, description);
                
                //PropertyDefinition of production line
                Element propertyDefinitions = (Element) productionLineNode.getElementsByTagName("PropertyDefinitions").item(0);
                NodeList properties = propertyDefinitions.getElementsByTagName("Property");
                
                for(int j = 0 ; j < properties.getLength() ; j++){
                    Element property = (Element) properties.item(j);
                    
                    String propertyName = property.getElementsByTagName("Name").item(0).getTextContent();
                    String value = property.getElementsByTagName("Value").item(0).getTextContent();
                    int min = Integer.parseInt(property.getElementsByTagName("Min").item(0).getTextContent());
                    
                    ThingProperty pt = new ThingProperty(propertyName, value);
                    pt.setMin(min);  
                    
                    productionLine.getProperties().add(pt);
                }
                
                //AssetDefinitions of production line
                Element assetDefinitions = (Element) productionLineNode.getElementsByTagName("AssetDefinitions").item(0);
                NodeList assets = assetDefinitions.getElementsByTagName("Asset");
                
                for(int k = 0 ; k < assets.getLength() ; k++){
                    Element asset = (Element) assets.item(k);
                    String assetName = asset.getElementsByTagName("Name").item(0).getTextContent();
                    String value = asset.getElementsByTagName("Description").item(0).getTextContent();
                    int frequentie = Integer.parseInt(asset.getElementsByTagName("DataFrequencyPerHour").item(0).getTextContent());
                    
                    Asset machine = new Asset(assetName, value, frequentie);
                    
                    Element assetPropertiesDefinitions = (Element) asset.getElementsByTagName("PropertyDefinitions").item(0);
                    NodeList assetProperties = assetPropertiesDefinitions.getElementsByTagName("Property");
                    
                    for(int z = 0 ; z < assetProperties.getLength() ; z++){
                        Element assetProperty = (Element) assetProperties.item(z);
                        
                        String assetPropertyName = assetProperty.getElementsByTagName("Name").item(0).getTextContent();
                        int assetPropertyMin = Integer.parseInt(assetProperty.getElementsByTagName("Min").item(0).getTextContent());
                        int assetPropertyMax = Integer.parseInt(assetProperty.getElementsByTagName("Max").item(0).getTextContent());
                        String assetPropertyValue = assetProperty.getElementsByTagName("Max").item(0).getTextContent();
                        
                        ThingProperty apt = new ThingProperty(assetPropertyName, value);
                        apt.setMin(assetPropertyMin);
                        apt.setMax(assetPropertyMax);
                        
                        machine.getProperties().add(apt);
                    }
                    
                    productionLine.getAssets().add(machine);
                }
                /*String name = productionLineNode.getElementsByTagName("Name").item(0).getTextContent();
                    String description = productionLineNode.getElementsByTagName("Description").item(0).getTextContent();
                    
                    LOG.info("Name of the production line: {}", name);
                    
                    Line productionLine = new Line(name, description);
                    
                    NodeList propertyList = productionLineNode.getChildNodes();
                    LOG.info("Size of properties: {}", propertyList.getLength());
                    
                    for(int j = 0 ; j < propertyList.getLength() ; j++){
                        Element pElement = (Element) propertyList.item(i);
                        
                        String pNaam = pElement.getElementsByTagName("Name").item(0).getTextContent();
                        String pVal = pElement.getElementsByTagName("Value").item(0).getTextContent();
                        
                        LOG.info("Name and value of the property: {} -- {}", pNaam, pVal);
                        
                        ThingProperty pt = new ThingProperty(pNaam, pVal);
                        productionLine.getProperties().add(pt);                        
                    }
                    
                    lines.add(productionLine);*/
            }

        } catch (Exception e) {
            LOG.error("There was an error reading the file \'{}\'", file);
            e.printStackTrace();
        }
    }
}
