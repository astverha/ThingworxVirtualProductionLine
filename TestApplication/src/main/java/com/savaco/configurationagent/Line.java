package com.savaco.configurationagent;

import java.util.ArrayList;
import java.util.List;

public class Line {
    
     private final List<Asset> assets;
     private final List<ThingProperty> properties;
     private final String name;
     private final String thingName;
     private final String description;
     
    /**
     * 
     * @param name
     * @param thingName
     * @param description 
     */
    public Line(String name, String thingName, String description){
         this.name = name;
         this.description = description;
         this.thingName = thingName;
         
         assets = new ArrayList<>();
         properties = new ArrayList<>();
    }

    public List<Asset> getAssets() {
        return assets;
    }

    public List<ThingProperty> getProperties() {
        return properties;
    }

    public String getThingName() {
        return thingName;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
