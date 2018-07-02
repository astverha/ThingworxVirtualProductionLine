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
        Random random = new Random();
        oldProductionRate = currentProductionRate;
        currentProductionRate = value;

        int temp = -1;
        int failure = -1;

        //get local
        for (ThingProperty tp : this.getDevice_Properties()) {
            if (tp.getPropertyName().equals("ProductionRate")) {
                tp.setValue(Integer.toString(currentProductionRate));
            } else if (tp.getPropertyName().equals("Temperature")) {
                temp = Integer.parseInt(tp.getValue());
            } else if (tp.getPropertyName().equals("PercentageFailure")) {
                failure = Integer.parseInt(tp.getValue());
            }
        }

        //simulate
        int newTemp = -1;
        int newFailure = -1;
        double deltaProdRate = currentProductionRate - oldProductionRate;
        if (temp != -1 && failure != -1) {
            //DIT MOET GEFIXED WORDEN
            newTemp = temp + random.nextInt(10);
            newFailure = failure + random.nextInt(20);

            try {
                //set local
                for (ThingProperty tp : this.getDevice_Properties()) {
                    if (tp.getPropertyName().equals("Temperature")) {
                        tp.setValue(Integer.toString(newTemp));
                    } else if (tp.getPropertyName().equals("PercentageFailure")) {
                        tp.setValue(Integer.toString(newFailure));
                    }
                }

                //set remote
                this.setPropertyValue("ProductionRate", new IntegerPrimitive(currentProductionRate));
                this.setPropertyValue("Temperature", new IntegerPrimitive(newTemp));
                this.setPropertyValue("PercentageFailure", new IntegerPrimitive(newFailure));

                LOG.info("TESTLOG ---- [" + this.getName() +  "] deltaProdRate: " + deltaProdRate + "\ttemp:" + temp + "->" + newTemp + "\tFail:" + failure + "->" + newFailure);
            } catch (Exception e) {
                LOG.warn("TESTLOG ---- Exception setting remote properties. (AssetThing - simulateNewData)");
            }
        }

    }

    public List<ThingProperty> getDevice_Properties() {
        return device_Properties;
    }
}
