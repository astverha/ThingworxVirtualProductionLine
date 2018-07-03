package com.savaco.ConfigurationAgent;

import java.util.ArrayList;
import java.util.List;

public class Asset {
    
    private final List<ThingProperty> properties;
    private final String name;
    private final String thingName;
    private final String description;
    private final int frequentie;
    
    /**
     * 
     * @param name
     * @param thingName
     * @param description
     * @param frequentie 
     */
    public Asset(String name, String thingName, String description, int frequentie){
        this.name = name;
        this.thingName = thingName;
        this.description = description;
        this.frequentie = frequentie;
        properties = new ArrayList<>();
    }

    public List<ThingProperty> getProperties() {
        return properties;
    }

    public String getName() {
        return name;
    }

    public int getFrequentie() {
        return frequentie;
    }

    public String getThingName() {
        return thingName;
    }

    public String getDescription() {
        return description;
    }
 
}
