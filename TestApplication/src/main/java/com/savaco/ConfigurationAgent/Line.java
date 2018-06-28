/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.savaco.ConfigurationAgent;

import com.savaco.testapplication.ThingProperty;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Administrator
 */
public class Line {
    
     private List<Asset> assets;
     private List<ThingProperty> properties;
     private final String name;
     private final String description;
     
     public Line(String name, String description){
         this.name = name;
         this.description = description;
         assets = new ArrayList<Asset>();
         properties = new ArrayList<ThingProperty>();
     }

    public List<Asset> getAssets() {
        return assets;
    }

    public List<ThingProperty> getProperties() {
        return properties;
    }
         
}
