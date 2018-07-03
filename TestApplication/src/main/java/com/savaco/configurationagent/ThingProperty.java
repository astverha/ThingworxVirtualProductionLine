package com.savaco.configurationagent;

public class ThingProperty {
    private final String propertyName;
    private String value;
    private int min;
    private int max;

    /**
     * 
     * @param propertyName
     * @param value 
     */
    public ThingProperty(String propertyName, String value) {
        this.propertyName = propertyName;
        this.value = value;
    }
    
    public String getPropertyName() {
        return propertyName;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }
}
