package com.savaco.testapplication;

import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thingworx.communications.client.ConnectedThingClient;
import com.thingworx.communications.client.things.VirtualThing;
import com.thingworx.metadata.PropertyDefinition;
import com.thingworx.types.BaseTypes;
import com.thingworx.types.collections.AspectCollection;
import com.thingworx.types.constants.Aspects;
import com.thingworx.types.constants.DataChangeType;
import com.thingworx.types.primitives.BooleanPrimitive;
import com.thingworx.types.primitives.IntegerPrimitive;
import com.thingworx.types.primitives.NumberPrimitive;
import com.thingworx.types.primitives.StringPrimitive;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

//These props have to be te same as on thingworx composer
/*@ThingworxPropertyDefinitions(properties = {
    // Each value that is collected will be pushed to the platform from within the
    // processScanRequest() method.
    @ThingworxPropertyDefinition(name = "Temperature", description = "The device temperature",
            baseType = "NUMBER",
            aspects = {"dataChangeType:NEVER", "dataChangeThreshold:0", "cacheTime:0",
                "isPersistent:FALSE", "isReadOnly:FALSE", "pushType:ALWAYS",
                "isFolded:FALSE", "defaultValue:0"})
    ,

        // This property is also pushed to the platform, but only when the value
        // of the property has changed.
        @ThingworxPropertyDefinition(name = "Humidity", description = "The device humidity",
            baseType = "NUMBER",
            aspects = {"dataChangeType:VALUE", "dataChangeThreshold:0", "cacheTime:0",
                "isPersistent:FALSE", "isReadOnly:FALSE", "pushType:VALUE",
                "defaultValue:0"})
    ,

        // This property is never pushed to the platform. The platform will always
        // request the values current value from the application.
        @ThingworxPropertyDefinition(name = "SetPoint", description = "The desired temperature",
            baseType = "NUMBER",
            aspects = {"dataChangeType:NEVER", "dataChangeThreshold:0", "cacheTime:-1",
                "isPersistent:TRUE", "isReadOnly:FALSE", "pushType:NEVER",
                "defaultValue:70"})})*/

public class AssetThing extends VirtualThing {

    private static final Logger LOG = LoggerFactory.getLogger(AssetThing.class);

    private final String name;
    private final ConnectedThingClient client;
    private final List<ThingProperty> device_Properties;

    /**
     * @param name The name of the thing.
     * @param description A description of the thing.
     * @param client The client that this thing is associated with.
     * @param device_Properties
     * @throws java.lang.Exception
     */
    public AssetThing(String name, String description, ConnectedThingClient client, List<ThingProperty> device_Properties)
            throws Exception {

        super(name, description, client);
        this.name = name;
        this.client = client;

        this.device_Properties = device_Properties;

        for (int i = 0; i < this.device_Properties.size(); i++) {
            ThingProperty node = this.device_Properties.get(i);
            PropertyDefinition pd;
            AspectCollection aspects = new AspectCollection();

            //determine Basetype of property (NUMBER, BOOLEAN or STRING)
            if (StringUtils.isNumeric(node.getValue())) {
                pd = new PropertyDefinition(node.getPropertyName(), " ", BaseTypes.NUMBER);
            } else if ("true".equals(node.getValue()) || "false".equals(node.getValue())) {
                pd = new PropertyDefinition(node.getPropertyName(), " ", BaseTypes.BOOLEAN);
            } else {
                pd = new PropertyDefinition(node.getPropertyName(), " ", BaseTypes.STRING);
            }
            
            //Add the dataChangeType aspect
            aspects.put(Aspects.ASPECT_DATACHANGETYPE, new StringPrimitive("VALUE"));
            //Add the dataChangeThreshold aspect
            aspects.put(Aspects.ASPECT_DATACHANGETHRESHOLD, new NumberPrimitive(0.0));
            //Add the cacheTime aspect
            aspects.put(Aspects.ASPECT_CACHETIME, new IntegerPrimitive(0));
            //Add the isPersistent aspect
            aspects.put(Aspects.ASPECT_ISPERSISTENT, new BooleanPrimitive(false));
            //Add the isReadOnly aspect
            aspects.put(Aspects.ASPECT_ISREADONLY, new BooleanPrimitive(true));
            //Add the pushType aspect
            aspects.put("pushType", new StringPrimitive(DataChangeType.ALWAYS.name()));
            //Add the isLogged aspect
            aspects.put(Aspects.ASPECT_ISLOGGED, new BooleanPrimitive(true));
            //Add the defaultValue aspect if needed...
            //aspects.put(Aspects.ASPECT_DEFAULTVALUE, new BooleanPrimitive(true));

            pd.setAspects(aspects);

            super.defineProperty(pd);
        }

//you need to comment initializeFromAnnotations() and use instead the initialize() in order for this to work.
//super.initializeFromAnnotations();
        super.initialize();
    }

    /**
     * This method provides a common interface amongst VirtualThings for
     * processing periodic requests. It is an opportunity to access data
     * sources, update property values, push new values to the server, and take
     * other actions.
     */
    @Override
    public void processScanRequest() {
        //here we randomize, usually you read from a sensor
        Random random = new Random();
        int temperature = 50 + random.nextInt(51);
        int humidity = random.nextInt(101);

        try {

            // Here we set the thing's internal property values to the new values
            // that we accessed above. This does not update the server. It simply
            // sets the new property value in memory.
            this.setPropertyValue("Temperature", new IntegerPrimitive(temperature));
            this.setPropertyValue("Humidity", new IntegerPrimitive(humidity));

            // This call evaluates all properties and determines if they should be pushed
            // to the server, based on their pushType aspect. A pushType of ALWAYS means the
            // property will always be sent to the server when this method is called. A
            // setting of VALUE means it will be pushed if has changed since the last
            // push. A setting of NEVER means it will never be pushed.
            // Our Temperature property is set to ALWAYS, so its value will be pushed
            // every time processScanRequest is called. This allows the platform to get
            // periodic updates and store the time series data. Humidity is set to
            // VALUE, so it will only be pushed if it changed. SetPoint is set to NEVER,
            // so it will never be updated.
            this.updateSubscribedProperties(10000);

        } catch (Exception e) {
            // This will occur if we provide an unknown property name. We'll ignore
            // the exception in this case and just log it.
            LOG.error("Exception occurred while updating properties (AssetThing.java).", e);
        }
    }

    public List<ThingProperty> getDevice_Properties() {
        return device_Properties;
    }
    
    
}
