package com.savaco.configurationagent;

import java.util.ArrayList;
import java.util.List;

public class Asset {
    
    private final List<ThingProperty> properties;
    private final String name;
    private final String thingName;
    private final String description;
    
    /**
     * 
     * @param name
     * @param thingName
     * @param description
     * @param frequentie 
     */
    public Asset(String name, String thingName, String description){
        this.name = name;
        this.thingName = thingName;
        this.description = description;
        properties = new ArrayList<>();
    }

    public List<ThingProperty> getProperties() {
        return properties;
    }

    public String getName() {
        return name;
    }

    public String getThingName() {
        return thingName;
    }

    public String getDescription() {
        return description;
    }
 
}
