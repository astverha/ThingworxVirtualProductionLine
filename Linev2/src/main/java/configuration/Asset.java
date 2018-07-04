/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package configuration;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Administrator
 */
public class Asset {
    private final List<ThingProperty> properties;
    private final String name;
    private final String description;
    private String relatedLine;
    
    /**
     * 
     * @param name
     * @param thingName
     * @param description
     * @param frequentie 
     */
    public Asset(String name, String description, String relatedLine){
        this.name = name;
        this.description = description;
        properties = new ArrayList<>();
    }

    public List<ThingProperty> getProperties() {
        return properties;
    }

    public String getName() {
        return name;
    }

    public String getRelatedLine() {
        return relatedLine;
    }
       
}
