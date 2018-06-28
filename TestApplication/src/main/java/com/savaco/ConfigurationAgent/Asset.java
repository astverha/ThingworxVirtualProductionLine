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
public class Asset {
    
    private List<ThingProperty> properties;
    private String name;
    private String description;
    private int frequentie;
    
    public Asset(String name, String description, int frequentie){
        this.name = name;
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
 
    
    
}
