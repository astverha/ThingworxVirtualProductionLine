package configuration;

import java.util.ArrayList;
import java.util.List;

public class Line {
    
    private final List<Asset> assets;
    private final List<ThingProperty> properties;
    private final String name;
    private final String description;
    
    public Line(String name, String description){
        this.name = name;
        this.description = description;
        assets = new ArrayList<>();
        properties = new ArrayList<>();
    }
    
    public List<Asset> getAssets(){
        return assets;
    }
    
    public List<ThingProperty> getProperties(){
        return properties;
    }

    public String getName() {
        return name;
    }
    
}
