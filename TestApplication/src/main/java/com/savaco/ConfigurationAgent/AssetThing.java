package com.savaco.ConfigurationAgent;

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
import java.util.Random;
import org.apache.commons.lang3.StringUtils;

public class AssetThing extends VirtualThing {

    private static final Logger LOG = LoggerFactory.getLogger(AssetThing.class);

    private final String name;
    private final ConnectedThingClient client;
    private final List<ThingProperty> device_Properties;
    private int oldProductionRate;
    private int currentProductionRate;

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

            if (StringUtils.isNumeric(node.getValue())) {
                pd = new PropertyDefinition(node.getPropertyName(), " ", BaseTypes.NUMBER);
            } else if ("true".equals(node.getValue()) || "false".equals(node.getValue())) {
                pd = new PropertyDefinition(node.getPropertyName(), " ", BaseTypes.BOOLEAN);
            } else {
                pd = new PropertyDefinition(node.getPropertyName(), " ", BaseTypes.STRING);
            }

            aspects.put(Aspects.ASPECT_DATACHANGETYPE, new StringPrimitive("VALUE"));
            aspects.put(Aspects.ASPECT_DATACHANGETHRESHOLD, new NumberPrimitive(0.0));
            aspects.put(Aspects.ASPECT_CACHETIME, new IntegerPrimitive(0));
            aspects.put(Aspects.ASPECT_ISPERSISTENT, new BooleanPrimitive(false));
            aspects.put(Aspects.ASPECT_ISREADONLY, new BooleanPrimitive(true));
            aspects.put("pushType", new StringPrimitive(DataChangeType.ALWAYS.name()));
            aspects.put(Aspects.ASPECT_ISLOGGED, new BooleanPrimitive(true));
            //aspects.put(Aspects.ASPECT_DEFAULTVALUE, new BooleanPrimitive(true));

            pd.setAspects(aspects);
            super.defineProperty(pd);

            if (node.getPropertyName().equals("ProductionRate")) {
                this.currentProductionRate = Integer.parseInt(node.getValue());
                this.oldProductionRate = Integer.parseInt(node.getValue());
            }
        }
        super.initialize();
    }

    public void simulateNewData(int value) {
        Random r = new Random();
        oldProductionRate = currentProductionRate;
        currentProductionRate = value;

        int temp = -1;
        int failure = -1;

        for (ThingProperty tp : this.getDevice_Properties()) {
            if (tp.getPropertyName().equals("ProductionRate")) {
                tp.setValue(Integer.toString(currentProductionRate));
            } else if (tp.getPropertyName().equals("Temperature")) {
                temp = Integer.parseInt(tp.getValue());
            } else if (tp.getPropertyName().equals("PercentageFailure")) {
                failure = Integer.parseInt(tp.getValue());
            }
        }

        int newTemp = -1;
        int newFailure = -1;
        if (temp != -1 && failure != -1) {
            int tempFluctuation = r.nextInt(Math.abs(currentProductionRate - oldProductionRate)) / 20;
            int failureFluctuation = r.nextInt(Math.abs(currentProductionRate - oldProductionRate)) / 30;
            if (currentProductionRate - oldProductionRate < 0) {
                while (temp - tempFluctuation < 0) {
                    tempFluctuation = r.nextInt(Math.abs(currentProductionRate - oldProductionRate)) / 20;
                }
                newTemp = temp - tempFluctuation;
                while (failure - failureFluctuation < 0) {
                    failureFluctuation = r.nextInt(Math.abs(currentProductionRate - oldProductionRate)) / 30;
                }
                newFailure = failure - failureFluctuation;
            }
        }

        if (newTemp != -1 && newFailure != -1) {
            try {
                for (ThingProperty tp : this.getDevice_Properties()) {
                    if (tp.getPropertyName().equals("Temperature")) {
                        tp.setValue(Integer.toString(newTemp));
                    } else if (tp.getPropertyName().equals("PercentageFailure")) {
                        tp.setValue(Integer.toString(newFailure));
                    }
                }
                
                this.setPropertyValue("ProductionRate", new IntegerPrimitive(currentProductionRate));
                this.setPropertyValue("Temperature", new IntegerPrimitive(newTemp));
                this.setPropertyValue("PercentageFailure", new IntegerPrimitive(newFailure));
            } catch(Exception e){
                
            }
        }

    }

    public List<ThingProperty> getDevice_Properties() {
        return device_Properties;
    }
}
