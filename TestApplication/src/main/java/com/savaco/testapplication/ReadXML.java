/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.savaco.testapplication;

import com.savaco.ConfigurationAgent.Asset;
import com.savaco.ConfigurationAgent.ConfigurationAgent;
import com.savaco.ConfigurationAgent.Line;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
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

    public static List<Line> read(File file) {

        SAXBuilder builder = new SAXBuilder();
        File xmlFile = file;

        //get all production lines
        List<Line> lines = new ArrayList<Line>();

        try {
            Document document = (Document) builder.build(xmlFile);
            Element dataSimulator = document.getRootElement();

            List<Element> list = dataSimulator.getChildren("ServerConfiguration");
            Element serverConfiguration = list.get(0);
            //System.out.println("Server info:");
            //System.out.println("\tServer: " + serverConfiguration.getChildText("Server"));
            //System.out.println("\tAppKey: " + serverConfiguration.getChildText("AppKey"));
            ReadXML.serverName = serverConfiguration.getChildText("Server");
            ReadXML.appKey = serverConfiguration.getChildText("AppKey");

            //System.out.println("\nProduction lines:");
            List<Element> productionLines = dataSimulator.getChildren("ProductionLine");
            //int i = 0;
            for (Element el : productionLines) {
                //i++;
                //System.out.println(i+ " - " + el.getChildText("Name"));
                Line line = new Line(el.getChildText("Name"), el.getChildText("ThingName"), el.getChildText("Description"));

                //get all line properties
                Element propertyDefinitions = (Element) el.getChildren("PropertyDefinitions").get(0);
                List<Element> lineProperties = propertyDefinitions.getChildren("Property");
                for (Element prop : lineProperties) {
                    //System.out.println("\t" + prop.getChildText("Name") + ": " + prop.getChildText("Value"));
                    //add lineproperties to Line object
                    ThingProperty lineProp = new ThingProperty(prop.getChildText("Name"), prop.getChildText("Value"));
                    lineProp.setMin(Integer.parseInt(prop.getChildText("Min")));
                    line.getProperties().add(lineProp);
                }

                //get all assets
                //System.out.println("\tAssets:");
                Element assetDefinitions = (Element) el.getChildren("AssetDefinitions").get(0);
                List<Element> assets = assetDefinitions.getChildren("Asset");
                //int a = 0;
                for (Element asset : assets) {
                    //a++;
                    //System.out.println("\t\t" + a + " - " + asset.getChildText("Name") + " (" + asset.getChildText("Description") + ")");

                    Asset machine = new Asset(asset.getChildText("Name"), asset.getChildText("ThingName"), asset.getChildText("Description"), Integer.parseInt(asset.getChildText("DataFrequencyPerHour")));

                    //get all asset properties
                    Element assetPropertyDefinitions = (Element) asset.getChildren("PropertyDefinitions").get(0);
                    List<Element> assetProperties = assetPropertyDefinitions.getChildren("Property");
                    for (Element prop : assetProperties) {
                        //System.out.println("\t\t\t" + prop.getChildText("Name") + ": " + prop.getChildText("Value"));
                        ThingProperty assetProp = new ThingProperty(prop.getChildText("Name"), prop.getChildText("Value"));
                        if (prop.getChildText("Min") != null) {
                            assetProp.setMin(Integer.parseInt(prop.getChildText("Min")));
                        }
                        if (prop.getChildText("Max") != null) {
                            assetProp.setMax(Integer.parseInt(prop.getChildText("Max")));
                        }
                        machine.getProperties().add(assetProp);
                    }
                    ThingProperty relatedLine = new ThingProperty("relatedLines", el.getChildText("Name"));
                    machine.getProperties().add(relatedLine);
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
