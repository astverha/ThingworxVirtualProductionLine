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
    private int prodRate;
    private int newProdRate;
    private boolean isDown;

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
        this.isDown = false;
        

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
                this.newProdRate = Integer.parseInt(node.getValue());
                this.prodRate = Integer.parseInt(node.getValue());
            }
        }
        super.initialize();
    }

    /*
    This method simulates new data for this thing based on the production rate set in the UI
    Even when there are no changes in production rate, temperature must vary.
    When there are changes, temperature, as well as failure rate has to change.
     */
    public void simulateNewData(int prodRateValue) {
        prodRate = newProdRate;
        newProdRate = prodRateValue;
        double temp = -1;
        double failure = -1;
        for (ThingProperty tp : this.getDevice_Properties()) {
            if (tp.getPropertyName().equals("ProductionRate")) {
                tp.setValue(Integer.toString(newProdRate));
            } else if (tp.getPropertyName().equals("Temperature")) {
                temp = Double.parseDouble(tp.getValue());
            } else if (tp.getPropertyName().equals("PercentageFailure")) {
                failure = Double.parseDouble(tp.getValue());
            }
        }

        int deltaProdRate = newProdRate - prodRate;
        int sign = 1;
        if (newProdRate < prodRate) {
            sign = -1;
        }
        if (newProdRate == 0) {
            try {
                for (ThingProperty tp : this.getDevice_Properties()) {
                    if (tp.getPropertyName().equals("PercentageFailure")) {
                        tp.setValue("" + 100);
                    } else if (tp.getPropertyName().equals("status")){
                        tp.setValue("" + 4);
                    }
                }
                this.setPropertyValue("ProductionRate", new IntegerPrimitive(0));
                this.setPropertyValue("PercentageFailure", new NumberPrimitive(100));
                this.setPropertyValue("status", new IntegerPrimitive(4));
                //LOG.info("TESTLOG ---- [" + this.getName() + "] \tBROKEN" + "\tFail:" + failure + "->" + 100);
                LOG.info("TESTLOG ---- [\" + this.getName() + \"] BROKEN");
            } catch (Exception e) {
                LOG.warn("TESTLOG ---- Exception setting remote properties. (AssetThing - simulateNewData)");
            }
        } else if (temp != -1 && failure != -1 && deltaProdRate != 0) {
            double newTemp = temp + (Math.abs(deltaProdRate * 0.05) * sign);
            double newFailure = failure + (Math.abs(deltaProdRate * 0.025) * sign);
            newTemp = (double) Math.round(newTemp * 100d) / 100d;
            newFailure = (double) Math.round(newFailure * 100d) / 100d;
            try {
                for (ThingProperty tp : this.getDevice_Properties()) {
                    if (tp.getPropertyName().equals("Temperature")) {
                        tp.setValue("" + newTemp);
                    } else if (tp.getPropertyName().equals("PercentageFailure")) {
                        tp.setValue("" + newFailure);
                    } else if (tp.getPropertyName().equals("status")) {
                        tp.setValue("" + 2);
                    }
                }
                this.setPropertyValue("ProductionRate", new IntegerPrimitive(newProdRate));
                this.setPropertyValue("Temperature", new NumberPrimitive(newTemp));
                this.setPropertyValue("PercentageFailure", new NumberPrimitive(newFailure));
                this.setPropertyValue("status", new IntegerPrimitive(2));
                //LOG.info("TESTLOG ---- [" + this.getName() +  "] \tdeltaProdRate: " + deltaProdRate + "\ttemp:" + temp + "->" + newTemp + "\tFail:" + failure + "->" + newFailure);
                LOG.info("TESTLOG ---- [\" + this.getName() + \"] RUNNING (changed prodRate)");
            } catch (Exception e) {
                LOG.warn("TESTLOG ---- Exception setting remote properties. (AssetThing - simulateNewData)");
            }
        } else {
            //only randomize temp a little when there is no change in prodrate
            Random random = new Random();
            double newTemp = temp + (random.nextDouble() / 10 * temp * (random.nextBoolean() ? 1 : -1));
            newTemp = (double) Math.round(newTemp * 100d) / 100d;
            try {
                this.setPropertyValue("Temperature", new NumberPrimitive(newTemp));
                for (ThingProperty tp : this.getDevice_Properties()) {
                    if (tp.getPropertyName().equals("Temperature")) {
                        tp.setValue("" + newTemp);
                    }
                }
                //LOG.info("TESTLOG ---- [" + this.getName() + "] \tdeltaProdRate: " + deltaProdRate + "\ttemp:" + temp + "->" + newTemp);
                LOG.info("TESTLOG ---- [" + this.getName() + "] RUNNING");
            } catch (Exception e) {
                LOG.warn("TESTLOG ---- Exception setting remote properties. (AssetThing - simulateNewData): " + this.getName());
                e.printStackTrace();
            }
        }
    }

    public void breakThing() {
        this.simulateNewData(0);
        this.isDown = true;
    }

    public void restartThing(int initialProdRate) {
        try {
            for (ThingProperty tp : this.getDevice_Properties()) {
                if (tp.getPropertyName().equals("Temperature")) {
                    tp.setValue("" + (initialProdRate/20));
                } else if (tp.getPropertyName().equals("PercentageFailure")) {
                    tp.setValue("" + 10);
                } else if (tp.getPropertyName().equals("status")) {
                    tp.setValue("" + 2);
                }
            }
            this.setPropertyValue("ProductionRate", new IntegerPrimitive(newProdRate));
            this.setPropertyValue("Temperature", new NumberPrimitive(initialProdRate/20));
            this.setPropertyValue("PercentageFailure", new NumberPrimitive());
            this.setPropertyValue("status", new IntegerPrimitive(2));
            LOG.info("TESTLOG ---- [" + this.getName() +  "] RESTARTED \tinitProdRate: " + initialProdRate + "\ttemp:" + initialProdRate/20 + "\tFail:" + 10);
        } catch (Exception e) {
            LOG.warn("TESTLOG ---- Exception setting remote properties. (AssetThing - simulateNewData): " + this.getName());
        }
        this.isDown = false;
    }

    public List<ThingProperty> getDevice_Properties() {
        return device_Properties;
    }

    public boolean isIsDown() {
        return isDown;
    }

    public void setIsDown(boolean isDown) {
        this.isDown = isDown;
    }
}
