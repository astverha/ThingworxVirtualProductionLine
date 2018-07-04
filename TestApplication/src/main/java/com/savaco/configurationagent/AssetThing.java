package com.savaco.configurationagent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thingworx.communications.client.ConnectedThingClient;
import com.thingworx.communications.client.things.VirtualThing;
import com.thingworx.metadata.PropertyDefinition;
import com.thingworx.relationships.RelationshipTypes.ThingworxEntityTypes;
import com.thingworx.types.BaseTypes;
import com.thingworx.types.InfoTable;
import com.thingworx.types.collections.AspectCollection;
import com.thingworx.types.collections.ValueCollection;
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

            pd.setAspects(aspects);
            super.defineProperty(pd);

            if (node.getPropertyName().equals("ProductionRate")) {
                this.newProdRate = Integer.parseInt(node.getValue());
                this.prodRate = Integer.parseInt(node.getValue());
            }
        }
        super.initialize();
    }

    /**
     * produces the correct amount based on production rate and simulation speed
     *
     */
    private void produce() {
        //if not a line and not down, produce
        if (!this.getName().contains("Line") && !this.isDown()) {
            //calculate amount to be produced
            int amountToProduce = newProdRate / 60 * Integer.parseInt(this.getPropertyByName("SimulationSpeed").getValue());
            if (amountToProduce < 1) {
                amountToProduce = 1;
            }
            //invoke Produce service
            for (int i = 0; i < amountToProduce; i++) {
                ValueCollection params = new ValueCollection();
                try {
                    client.invokeService(ThingworxEntityTypes.Things, this.getName(), "Produce", params, 1000);
                } catch (Exception e) {
                    LOG.warn("TESTLOG ---- Exception invoking produce service in " + this.getClass());
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Simulates parameters based on the production rate of a machine.
     *
     * @param prodRateValue Production Rate at which to simulate the machine
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
                this.setProperty("PercentageFailure", "" + 100);
            } catch (Exception e) {
                LOG.warn("TESTLOG ---- Exception setting remote properties. (AssetThing - simulateNewData)");
            }
        } else if (temp != -1 && failure != -1 && deltaProdRate != 0) {
            double newTemp = temp + (Math.abs(deltaProdRate * 0.05) * sign);
            double newFailure = failure + (Math.abs(deltaProdRate * 0.025) * sign);
            newTemp = (double) Math.round(newTemp * 100d) / 100d;
            newFailure = (double) Math.round(newFailure * 100d) / 100d;
            try {
                this.setProperty("Temperature", "" + newTemp);
                this.setProperty("PercentageFailure", "" + newFailure);
                this.setProperty("status", "" + State.RUNNING.ordinal());
                LOG.info("TESTLOG ---- [" + this.getName() + "] RUNNING (changed prodRate)");
            } catch (Exception e) {
                LOG.warn("TESTLOG ---- Exception setting remote properties. (AssetThing - simulateNewData)");
            }
        } else {
            Random random = new Random();
            double newTemp = temp + (random.nextDouble() / 10 * temp * (random.nextBoolean() ? 1 : -1));
            newTemp = (double) Math.round(newTemp * 100d) / 100d;
            try {
                this.setProperty("Temperature", "" + newTemp);
                this.setProperty("status", "" + State.RUNNING.ordinal());
                LOG.info("TESTLOG ---- [" + this.getName() + "] RUNNING");
            } catch (Exception e) {
                LOG.warn("TESTLOG ---- Exception setting remote properties. (AssetThing - simulateNewData): " + this.getName());
            }
        }
        this.produce();
    }

    /**
     * breaks a machine (UNPLANNED_DOWNTIME and ProductionRate to zero)
     */
    public void breakThing() {
        this.setProperty("status", "" + State.UNPLANNED_DOWNTIME.ordinal());
        LOG.info("TESTLOG ---- [" + this.getName() + "] BROKEN");
        this.simulateNewData(0);
        this.down = true;
    }

    /**
     * puts a machine under maintenance (PLANNED_DOWNTIME and ProductionRate to
     * zero)
     */
    public void performMaintenance() {
        this.setProperty("status", "" + State.PLANNED_DOWNTIME.ordinal());
        LOG.info("TESTLOG ---- [" + this.getName() + "] UNDER MAINTENANCE");
        this.simulateNewData(0);
        this.down = true;
    }

    /**
     * Restarts a machine with a production rate of initialProdRate
     *
     * @param initialProdRate
     */
    public void restartThing(int initialProdRate) {
        try {
            this.setProperty("ProductionRate", "" + initialProdRate);
            this.setProperty("Temperature", "" + initialProdRate / 20);
            this.setProperty("PercentageFailure", "" + 10);
            this.setProperty("status", "" + State.RUNNING.ordinal());
            LOG.info("TESTLOG ---- [" + this.getName() + "] RESTARTED");
        } catch (Exception e) {
            LOG.warn("TESTLOG ---- Exception setting remote properties. (AssetThing - simulateNewData): " + this.getName());
        }
        this.down = false;
    }

    /**
     * Grants access to device properties
     *
     * @return
     */
    public List<ThingProperty> getDevice_Properties() {
        return this.device_Properties;
    }

    /**
     * Gets a ThingProperty by the property name
     *
     * @param name
     * @return
     */
    public ThingProperty getPropertyByName(String name) {
        ThingProperty tp = null;
        for (ThingProperty pt : this.device_Properties) {
            if (pt.getPropertyName().equalsIgnoreCase(name)) {
                tp = pt;
            }
        }
        return tp;
    }

    /**
     * Checks whether a machine is down. Returns true if the machine is down
     * (production rate = 0)
     *
     * @return
     */
    public boolean isDown() {
        return down;
    }

    /**
     * Sets the down boolean.
     *
     * @param isDown
     */
    public void setDown(boolean isDown) {
        this.down = isDown;
    }

    /**
     * Sets the value of a property locally as well as remotely.
     *
     * @param propName
     * @param propVal
     */
    public void setProperty(String propName, String propVal) {
        try {
            for (ThingProperty tp : this.getDevice_Properties()) {
                if (tp.getPropertyName().equals(propName)) {
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

    /**
     * Checks whether or not a string can be parsed as an integer.
     *
     * @param str
     * @return
     */
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

    /**
     * Checks whether or not a string can be parsed as a double.
     *
     * @param value
     * @return
     */
    private boolean isDouble(String value) {
        try {
            Double.parseDouble(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
