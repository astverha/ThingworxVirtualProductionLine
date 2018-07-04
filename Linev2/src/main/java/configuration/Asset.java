package configuration;

import java.util.ArrayList;
import java.util.List;

public class Asset {
    private final List<ThingProperty> properties;
    private final String name;
    private final String description;
    private String relatedLine;
    
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
