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
    private final String propertyName;
    private String value;
    
    public ThingProperty(String propertyName, String value){
        this.propertyName = propertyName;
        this.value = value;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public String getValue() {
        return value;
    }
    
    
}
