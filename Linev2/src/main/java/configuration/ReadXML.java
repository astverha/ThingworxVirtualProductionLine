/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package configuration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Administrator
 */
public class ReadXML {
    private static String serverName;
    private static String appKey;
    
    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(ReadXML.class);
    
    public static List<Line> read(File filename){
        SAXBuilder builder = new SAXBuilder();
        
        List<Line> lines = new ArrayList<>();
        try {
            Document document = (Document) builder.build(filename);
            Element dataSimulator = document.getRootElement();
            
            List<Element> list = dataSimulator.getChildren("ServerConfiguration");
            Element serverConfiguration = list.get(0);
            
            //SERVER INFORMATION
            ReadXML.serverName = serverConfiguration.getChildText("Server");
            ReadXML.appKey = serverConfiguration.getChildText("AppKey");
            
            List<Element> productionLines = dataSimulator.getChildren("ProductionLine");
            
            //PRODUCTION LINE
            for(Element productionLine : productionLines){
                Line line = new Line(productionLine.getChildText("Name"), productionLine.getChildText("Description"));
                
                //PROPERTIES OF A PRODUCTION LINE
                Element propertyDefinitions = (Element) productionLine.getChildren("PropertyDefinitions").get(0);
                List<Element> lineProperties = propertyDefinitions.getChildren("Property");
                
                for(Element lineProperty : lineProperties){
                    ThingProperty lineProp = new ThingProperty(lineProperty.getChildText("Name"), lineProperty.getChildText("Value"));
                    line.getProperties().add(lineProp);
                }
                
                //ASSETS OF A PRODUCTION LINE
                Element assetDefinitions = (Element) productionLine.getChildren("AssetDefinitions").get(0);
                List<Element> assets = assetDefinitions.getChildren("Asset");
                for(Element asset : assets){
                    Asset machine = new Asset(asset.getChildText("Name"), "", productionLine.getChildText("Name"));
                    
                    //PROPERTIES OF AN ASSET
                    Element assetPropertyDefinitions = (Element) asset.getChildren("PropertyDefinitions").get(0);
                    List<Element> assetProperties = assetPropertyDefinitions.getChildren("Property");
                    for(Element assetProperty : assetProperties){
                        ThingProperty assetProp = new ThingProperty(assetProperty.getChildText("Name"), assetProperty.getChildText("Value"));
                        machine.getProperties().add(assetProp);
                    }
                    line.getAssets().add(machine);
                }
                lines.add(line);
            }
        } catch(Exception e){
            LOG.error("NOTIFICATIE [ERROR] - {} - An exception occurred while reading {}", ReadXML.class, filename);
        }
        return lines;
    }
    
    public static String getServerName(){
        return ReadXML.serverName;
    }
    
    public static String getAppKey(){
        return ReadXML.appKey;
    }
}
