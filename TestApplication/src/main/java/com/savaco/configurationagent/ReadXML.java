package com.savaco.configurationagent;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.slf4j.LoggerFactory;

public class ReadXML {

    private static String serverName;
    private static String appKey;

    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(ReadXML.class);

    /**
     * 
     * @param file
     * @return 
     */
    public static List<Line> read(File file) {

        SAXBuilder builder = new SAXBuilder();
        File xmlFile = file;
        
        List<Line> lines = new ArrayList<>();
        try {
            Document document = (Document) builder.build(xmlFile);
            Element dataSimulator = document.getRootElement();

            List<Element> list = dataSimulator.getChildren("ServerConfiguration");
            Element serverConfiguration = list.get(0);
            
            //SERVER INFORMATION
            ReadXML.serverName = serverConfiguration.getChildText("Server");
            ReadXML.appKey = serverConfiguration.getChildText("AppKey");

            List<Element> productionLines = dataSimulator.getChildren("ProductionLine");
            
            //PRODUCTION LINE
            for (Element productionLine : productionLines) {
                Line line = new Line(productionLine.getChildText("Name"), productionLine.getChildText("ThingName"), productionLine.getChildText("Description"));

                //PROPERTIES OF A PRODUCTION LINE
                Element propertyDefinitions = (Element) productionLine.getChildren("PropertyDefinitions").get(0);
                List<Element> lineProperties = propertyDefinitions.getChildren("Property");

                for (Element lineProperty : lineProperties) {
                    ThingProperty lineProp = new ThingProperty(lineProperty.getChildText("Name"), lineProperty.getChildText("Value"));
                    lineProp.setMin(Integer.parseInt(lineProperty.getChildText("Min")));
                    line.getProperties().add(lineProp);
                }
                
                //ASSETS OF A PRODUCTION LINE
                Element assetDefinitions = (Element) productionLine.getChildren("AssetDefinitions").get(0);
                List<Element> assets = assetDefinitions.getChildren("Asset");
                for (Element asset : assets) {
                    Asset machine = new Asset(asset.getChildText("Name"), asset.getChildText("ThingName"), asset.getChildText("Description"));

                    //PROPERTIES OF AN ASSET
                    Element assetPropertyDefinitions = (Element) asset.getChildren("PropertyDefinitions").get(0);
                    List<Element> assetProperties = assetPropertyDefinitions.getChildren("Property");

                    for (Element assetProperty : assetProperties) {
                        ThingProperty assetProp = new ThingProperty(assetProperty.getChildText("Name"), assetProperty.getChildText("Value"));
                        if (assetProperty.getChildText("Min") != null) {
                            assetProp.setMin(Integer.parseInt(assetProperty.getChildText("Min")));
                        }
                        if (assetProperty.getChildText("Max") != null) {
                            assetProp.setMax(Integer.parseInt(assetProperty.getChildText("Max")));
                        }
                        machine.getProperties().add(assetProp);
                    }
                    ThingProperty relatedLines = new ThingProperty("relatedLines", productionLine.getChildText("Name"));
                    machine.getProperties().add(relatedLines);
                    line.getAssets().add(machine);
                }
                lines.add(line);
            }
        } catch (IOException | JDOMException io) {
            System.out.println(io.getMessage());
        }
        return lines;
    }

    public static String getServerName() {
        return ReadXML.serverName;
    }

    public static String getAppKey() {
        return ReadXML.appKey;
    }
}
