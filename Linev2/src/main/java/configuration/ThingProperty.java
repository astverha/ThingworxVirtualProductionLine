package configuration;

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
