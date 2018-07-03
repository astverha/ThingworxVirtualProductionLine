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

    public enum State {
        NOT_CONFIGURED, WARNING, RUNNING, PLANNED_DOWNTIME, UNPLANNED_DOWNTIME, UNAVAILABLE
    };

    private static final Logger LOG = LoggerFactory.getLogger(AssetThing.class);

    private final String name;
    private final ConnectedThingClient client;
    private final List<ThingProperty> device_Properties;
    private int prodRate;
    private int newProdRate;
    private boolean down;

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
        this.down = false;

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
        this.setProperty("ProductionRate", "" + newProdRate);
        double temp = Double.parseDouble(this.getPropertyByName("Temperature").getValue());
        double failure = Double.parseDouble(this.getPropertyByName("PercentageFailure").getValue());

        int deltaProdRate = newProdRate - prodRate;
        int sign = 1;
        if (newProdRate < prodRate) {
            sign = -1;
        }
        
        //Production rate is down to 0 (no longer running) --> broken or maintenance
        if (newProdRate == 0) {
            try {
                this.setProperty("Temperature", "" + temp);
                this.setProperty("PercentageFailure", "" + failure);
                this.setProperty("status", "" + State.UNPLANNED_DOWNTIME.toString());
                LOG.info("TESTLOG ---- [" + this.getName() + "] BROKEN");
            } catch (Exception e) {
                LOG.warn("TESTLOG ---- Exception setting remote properties. (AssetThing - simulateNewData)");
            }
        } //change production rate
        else if (temp != -1 && failure != -1 && deltaProdRate != 0) {
            double newTemp = temp + (Math.abs(deltaProdRate * 0.05) * sign);
            double newFailure = failure + (Math.abs(deltaProdRate * 0.025) * sign);
            newTemp = (double) Math.round(newTemp * 100d) / 100d;
            newFailure = (double) Math.round(newFailure * 100d) / 100d;
            try {
                this.setProperty("Temperature", "" + newTemp);
                this.setProperty("PercentageFailure", "" + newFailure);
                this.setProperty("status", "" + State.RUNNING.toString());
                LOG.info("TESTLOG ---- [" + this.getName() + "] RUNNING (changed prodRate)");
            } catch (Exception e) {
                LOG.warn("TESTLOG ---- Exception setting remote properties. (AssetThing - simulateNewData)");
            }
        } //no change in production rate --> just keep running (little temp variation)
        else {
            Random random = new Random();
            double newTemp = temp + (random.nextDouble() / 10 * temp * (random.nextBoolean() ? 1 : -1));
            newTemp = (double) Math.round(newTemp * 100d) / 100d;
            try {
                this.setProperty("Temperature", "" + newTemp);
                LOG.info("TESTLOG ---- [" + this.getName() + "] RUNNING");
            } catch (Exception e) {
                LOG.warn("TESTLOG ---- Exception setting remote properties. (AssetThing - simulateNewData): " + this.getName());
            }
        }
    }

    public void breakThing() {
        this.simulateNewData(0);
        this.down = true;
    }

    public void restartThing(int initialProdRate) {
        try {
            this.setProperty("ProductionRate", ""+initialProdRate);
            this.setProperty("Temperature", ""+initialProdRate/20);
            this.setProperty("PercentageFailure", ""+10);
            this.setProperty("status", ""+State.RUNNING.toString());
            LOG.info("TESTLOG ---- [" + this.getName() + "] RESTARTED");
        } catch (Exception e) {
            LOG.warn("TESTLOG ---- Exception setting remote properties. (AssetThing - simulateNewData): " + this.getName());
        }
        this.down = false;
    }

    public List<ThingProperty> getDevice_Properties() {
        return this.device_Properties;
    }

    public ThingProperty getPropertyByName(String name) {
        for (ThingProperty pt : this.device_Properties) {
            if (pt.getPropertyName().equalsIgnoreCase(name)) {
                return pt;
            }
        }
        return null;
    }   

    public State convertToState(int numb) {
        State status = null;
        switch (numb) {
            case 0:
                status = State.NOT_CONFIGURED;
                break;
            case 1:
                status = State.WARNING;
                break;
            case 2:
                status = State.RUNNING;
                break;
            case 3:
                status = State.PLANNED_DOWNTIME;
                break;
            case 4:
                status = State.UNPLANNED_DOWNTIME;
                break;
            case 5:
                status = State.UNAVAILABLE;
                break;
        }
        return status;
    }

    public boolean isDown() {
        return down;
    }

    public void setDown(boolean isDown) {
        this.down = isDown;
    }

    
    /*
        Specific methods to use in GUI
    */
    
    public String getStatus(){
        String status = this.getPropertyByName("status").getValue();
        status = this.convertToState(Integer.parseInt(status)).toString();
        return status;
    }
    
    public int getProductionRate(){
        return(Integer.parseInt(this.getPropertyByName("ProductionRate").getValue()));
    }
    //////////////////////////////////////

    public void setProperty(String propName, String propVal) {
        try {
            for (ThingProperty tp : this.getDevice_Properties()) {
                if (tp.getPropertyName().equals(name)) {
                    tp.setValue("" + propVal);
                }
            }
            if (isInteger(propVal)) {
                this.setPropertyValue(propName, new IntegerPrimitive(Integer.parseInt(propVal)));
            } else if (isDouble(propVal)) {
                double val = (double) Math.round(Double.parseDouble(propVal) * 100d) / 100d;
                this.setPropertyValue(propName, new NumberPrimitive(val));
            } else {
                this.setPropertyValue(propName, new StringPrimitive(propVal));
            }
        } catch (Exception e) {
            LOG.warn("TESTLOG ---- Exception updating property " + propName + "(" + propVal + ") of thing " + this.getName());
            e.printStackTrace();
        }

    }

    private static boolean isInteger(String str) {
        if (str == null) {
            return false;
        }
        int length = str.length();
        if (length == 0) {
            return false;
        }
        int i = 0;
        if (str.charAt(0) == '-') {
            if (length == 1) {
                return false;
            }
            i = 1;
        }
        for (; i < length; i++) {
            char c = str.charAt(i);
            if (c < '0' || c > '9') {
                return false;
            }
        }
        return true;
    }

    private boolean isDouble(String value) {
        try {
            Double.parseDouble(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
