/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.savaco.testapplication;

import java.io.File;
import java.io.IOException;
import java.util.List;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

/**
 *
 * @author Administrator
 */
public class Reading {

    public static void main(String[] args) {
        SAXBuilder builder = new SAXBuilder();
        File xmlFile = new File("C:\\Users\\Administrator\\Desktop\\FirstDraft\\ThingworxVirtualProductionLine\\TestApplication\\src\\main\\java\\com\\savaco\\resources\\configuration.xml");

        try {
            Document document = (Document) builder.build(xmlFile);
            Element dataSimulator = document.getRootElement();
            
            List<Element> list = dataSimulator.getChildren("ServerConfiguration");
            Element serverConfiguration = list.get(0);
            System.out.println("Server info:");
            System.out.println("\tServer: " + serverConfiguration.getChildText("Server"));
            System.out.println("\tAppKey: " + serverConfiguration.getChildText("AppKey"));
            
            //get all production lines
            System.out.println("\nProduction lines:");
            List<Element> productionLines = dataSimulator.getChildren("ProductionLine");
            int i = 0;
            for(Element el : productionLines){
                i++;
                System.out.println(i+ " - " + el.getChildText("Name"));
                
                //get all line properties
                Element propertyDefinitions = (Element) el.getChildren("PropertyDefinitions").get(0);
                List<Element> lineProperties = propertyDefinitions.getChildren("Property");
                for(Element prop : lineProperties){
                    System.out.println("\t" + prop.getChildText("Name") + ": " + prop.getChildText("Value"));
                }
                
                //get all assets
                System.out.println("\tAssets:");
                Element assetDefinitions = (Element) el.getChildren("AssetDefinitions").get(0);
                List<Element> assets = assetDefinitions.getChildren("Asset");
                int a = 0;
                for(Element asset : assets){
                    a++;
                    System.out.println("\t\t" + a + " - " + asset.getChildText("Name") + " (" + asset.getChildText("Description") + ")");

                    //get all asset properties
                    Element assetPropertyDefinitions = (Element) asset.getChildren("PropertyDefinitions").get(0);
                    List<Element> assetProperties = assetPropertyDefinitions.getChildren("Property");
                    for(Element prop : assetProperties){
                        System.out.println("\t\t\t" + prop.getChildText("Name") + ": " + prop.getChildText("Value"));
                    }
                }
            }
            
            /*for (int i = 0; i < list.size(); i++) {

                Element node = (Element) list.get(i);

                System.out.println("First Name : " + node.getChildText("firstname"));
                System.out.println("Last Name : " + node.getChildText("lastname"));
                System.out.println("Nick Name : " + node.getChildText("nickname"));
                System.out.println("Salary : " + node.getChildText("salary"));

            }*/

        } catch (IOException | JDOMException io) {
            System.out.println(io.getMessage());
        }
    }
}
