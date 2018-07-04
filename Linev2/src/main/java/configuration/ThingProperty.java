/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package configuration;

/**
 *
 * @author Administrator
 */
public class ThingProperty {
    private final String name;
    private String value;
    
    public ThingProperty(String propertyName, String value){
        this.name = propertyName;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }
    
    public void setValue(String value){
        this.value = value;
    }
}
