/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.savaco.ConfigurationAgent;

import com.savaco.testapplication.ProductLineClient;
import com.savaco.testapplication.ReadXML;
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
            
            ReadXML.read(xmlFile);
            serverName = ReadXML.getServerName();
            appKey = ReadXML.getAppKey();
        } catch (Exception e) {
            LOG.error("There was an error reading the file \'{}\'", file);
            e.printStackTrace();
        }
    }
}
