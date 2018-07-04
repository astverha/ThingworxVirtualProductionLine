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
