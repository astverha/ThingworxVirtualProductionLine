package configuration;

import com.stage.client.ThingworxClient;
import com.thingworx.communications.client.things.VirtualThing;
import com.thingworx.metadata.PropertyDefinition;
import com.thingworx.relationships.RelationshipTypes;
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
import com.thingworx.types.primitives.structs.VTQ;
import java.util.List;
import java.util.Random;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.Utilities;

public class AssetThing extends VirtualThing {

    private static final Logger LOG = LoggerFactory.getLogger(AssetThing.class);

    private final String name;
    private final TypeEnum type;
    private final List<ThingProperty> assetProperties;
    private ThingworxClient client;
    private int initProdRate;
    private int GUIProdRate;
    private int prodRate;
    private int failure;
    private int goodCount;
    private int badCount;
    private boolean machineDown;

    public AssetThing(String name, TypeEnum type, List<ThingProperty> assetProperties, ThingworxClient client) {
        super(name, "", client);
        this.name = name;
        this.type = type;
        this.assetProperties = assetProperties;
        this.client = client;
        this.machineDown = false;
        this.goodCount = 0;
        this.badCount = 0;
        try {
            //Define properties for the remote thing
            //for loop voor standaard props,
            //specifiek voor BufferQuantity, GoodCount en BadCount
            for (int i = 0; i < this.assetProperties.size(); i++) {
                ThingProperty node = this.assetProperties.get(i);

                if (!node.getName().equalsIgnoreCase("ProductionRate") && !node.getName().equalsIgnoreCase("PercentageFailure")) {
                    PropertyDefinition pd;
                    AspectCollection aspects = new AspectCollection();

                    if (StringUtils.isNumeric(node.getValue())) {
                        pd = new PropertyDefinition(node.getName(), "", BaseTypes.NUMBER);
                    } else if ("true".equals(node.getValue()) || "false".equals(node.getValue())) {
                        pd = new PropertyDefinition(node.getName(), "", BaseTypes.BOOLEAN);
                    } else {
                        pd = new PropertyDefinition(node.getName(), "", BaseTypes.STRING);
                    }

                    aspects.put(Aspects.ASPECT_DATACHANGETYPE, new StringPrimitive("VALUE"));
                    aspects.put(Aspects.ASPECT_DATACHANGETHRESHOLD, new NumberPrimitive(0.0));
                    aspects.put(Aspects.ASPECT_CACHETIME, new IntegerPrimitive(0));
                    aspects.put(Aspects.ASPECT_ISPERSISTENT, new BooleanPrimitive(true));
                    aspects.put(Aspects.ASPECT_ISREADONLY, new BooleanPrimitive(true));
                    aspects.put("pushType", new StringPrimitive(DataChangeType.ALWAYS.name()));
                    aspects.put(Aspects.ASPECT_ISLOGGED, new BooleanPrimitive(true));

                    pd.setAspects(aspects);
                    super.defineProperty(pd);
                }
            }

            //BufferQuantity
            PropertyDefinition bufferQuantity;
            AspectCollection aspects = new AspectCollection();
            bufferQuantity = new PropertyDefinition("BufferQuantity", "", BaseTypes.NUMBER);
            aspects.put(Aspects.ASPECT_DATACHANGETYPE, new StringPrimitive("VALUE"));
            aspects.put(Aspects.ASPECT_DATACHANGETHRESHOLD, new NumberPrimitive(0.0));
            aspects.put(Aspects.ASPECT_CACHETIME, new IntegerPrimitive(0));
            aspects.put(Aspects.ASPECT_ISPERSISTENT, new BooleanPrimitive(true));
            aspects.put(Aspects.ASPECT_ISREADONLY, new BooleanPrimitive(true));
            aspects.put("pushType", new StringPrimitive(DataChangeType.ALWAYS.name()));
            bufferQuantity.setAspects(aspects);
            super.defineProperty(bufferQuantity);

            //GoodCount
            PropertyDefinition gc;
            aspects = new AspectCollection();
            gc = new PropertyDefinition("GoodCount", "", BaseTypes.NUMBER);
            aspects.put(Aspects.ASPECT_DATACHANGETYPE, new StringPrimitive("VALUE"));
            aspects.put(Aspects.ASPECT_DATACHANGETHRESHOLD, new NumberPrimitive(0.0));
            aspects.put(Aspects.ASPECT_CACHETIME, new IntegerPrimitive(0));
            aspects.put(Aspects.ASPECT_ISPERSISTENT, new BooleanPrimitive(true));
            aspects.put(Aspects.ASPECT_ISREADONLY, new BooleanPrimitive(true));
            aspects.put("pushType", new StringPrimitive(DataChangeType.ALWAYS.name()));
            gc.setAspects(aspects);
            super.defineProperty(gc);

            //BadCount
            PropertyDefinition bc;
            aspects = new AspectCollection();
            bc = new PropertyDefinition("BadCount", "", BaseTypes.NUMBER);
            aspects.put(Aspects.ASPECT_DATACHANGETYPE, new StringPrimitive("VALUE"));
            aspects.put(Aspects.ASPECT_DATACHANGETHRESHOLD, new NumberPrimitive(0.0));
            aspects.put(Aspects.ASPECT_CACHETIME, new IntegerPrimitive(0));
            aspects.put(Aspects.ASPECT_ISPERSISTENT, new BooleanPrimitive(true));
            aspects.put(Aspects.ASPECT_ISREADONLY, new BooleanPrimitive(true));
            aspects.put("pushType", new StringPrimitive(DataChangeType.ALWAYS.name()));
            bc.setAspects(aspects);
            super.defineProperty(bc);
        } catch (Exception e) {
            LOG.error("NOTIFICATIE [ERROR] - {} - An exception occurred while initializing an asset", AssetThing.class);
        }
    }

    /**
     * Initializes thing properties, remote and local (DEPRECATED)
     *
     * @param myClient
     */
    public void initializeProperties(ThingworxClient myClient) {
        try {
            for (ThingProperty tp : this.assetProperties) {
                if (!tp.getName().equals("ProductionRate")
                        && !tp.getName().equals("PercentageFailure")
                        && !tp.getName().equals("NextAsset")) {
                    this.setRemoteProperty(tp.getName(), tp.getValue());
                } else if (tp.getName().equals("NextAsset")) {
                    this.setRemoteProperty(tp.getName(), "Asset_" + tp.getValue());
                } else if (tp.getName().equals("ProductionRate")) {
                    this.prodRate = Integer.parseInt(tp.getValue());
                    this.GUIProdRate = prodRate;
                    this.initProdRate = prodRate;
                } else if (tp.getName().equals("PercentageFailure")) {
                    this.failure = Integer.parseInt(tp.getValue());
                }
            }

            // Initialize BufferQuantity, GoodCount and BadCount
            this.setRemoteProperty("BufferQuantity", Integer.toString(5000));
            this.setRemoteProperty("GoodCount", Double.toString(0.0));
            this.setRemoteProperty("BadCount", Double.toString(0.0));

            // Wait for updates
            this.updateSubscribedProperties(1000);
        } catch (Exception e) {
            LOG.error("NOTIFICATIE [ERROR] - {} - Exception waiting for update of property of thing {}).", AssetThing.class, this.getName());
        }
    }

    /**
     * Sets the remote property of a thing (does not set local ThingProperty).
     *
     * @param name
     * @param value
     */
    public void setRemoteProperty(String name, String value) {
        try {
            VTQ vtq = new VTQ();
            if (Utilities.isDouble(value)) {
                vtq.setValue(new NumberPrimitive(Double.parseDouble(value)));
            } else if (Utilities.isInteger(value)) {
                vtq.setValue(new IntegerPrimitive(Integer.parseInt(value)));
            } else {
                vtq.setValue(new StringPrimitive(value));
            }
            LOG.info("NOTIFICATIE: {} - property " + name + " is now " + value, this.getName());
            //this.setPropertyVTQ(name, vtq, true);
            client.writeProperty(ThingworxEntityTypes.Things, this.getName(), name, vtq.getValue(), Integer.SIZE);
        } catch (Exception e) {
            LOG.error("NOTIFICATIE [ERROR] - {} - Unable to update property {} of thing {}.", AssetThing.class, name, this.getName());
            e.printStackTrace();
        }
    }

    /**
     * Returns the local ThingProperty.
     *
     * @param name
     * @return
     */
    public ThingProperty getPropertyByName(String name) {
        for (ThingProperty tp : this.assetProperties) {
            if (tp.getName().equals(name)) {
                return tp;
            }
        }
        return null;
    }

    @Override
    public String getName() {
        return name;
    }

    public List<ThingProperty> getAssetProperties() {
        return assetProperties;
    }

    /**
     * Simulates parameter changes of the virtual things.
     */
    public void simulateData() {
        try {
            //Get the pushedStatus of the Thing
            InfoTable resultaat = client.readProperty(RelationshipTypes.ThingworxEntityTypes.Things, this.getName(), "pushedStatus", 10000);
            String status = resultaat.getFirstRow().getStringValue("pushedStatus");
            if (status.equals("2")) {
                this.machineDown = false;
            } else if (status.equals("3") || status.equals("4")) {
                this.machineDown = true;
            }
            this.getPropertyByName("pushedStatus").setValue(status);
            if (!machineDown) {
                Random random = new Random();
                try {

                    //Calculate difference between new production rate and old production rate.
                    int dProdRate = this.GUIProdRate - this.prodRate;
                    //If there is a difference, set all parameters according to the new production rate,
                    //as well as the new percentage failure stat.
                    if (dProdRate != 0) {
                        for (ThingProperty tp : this.assetProperties) {
                            if (!tp.getName().equalsIgnoreCase("pushedStatus")
                                    && !tp.getName().equalsIgnoreCase("ProductionRate")
                                    && !tp.getName().equalsIgnoreCase("PercentageFailure")
                                    && !tp.getName().equalsIgnoreCase("NextAsset")
                                    && !tp.getName().equalsIgnoreCase("IdealRunRate")) {
                                double val = Double.parseDouble(tp.getValue());
                                val = val * (this.GUIProdRate / new Double(this.prodRate));
                                val = (double) Math.round(val * 100d) / 100d;
                                tp.setValue(Double.toString(val));
                            }
                        }
                        this.failure = this.failure + dProdRate / 30 + random.nextInt(10) - 5;
                    }

                    //Set the production rate equal to the new production rate.
                    this.prodRate = this.GUIProdRate;
                    //calculate the amount of items produced every 5 seconds (simulationspeed)
                    double production = this.prodRate / 60 * 5;
                    int deltaGoodCount = (int) (((1 - (this.failure / 100.0)) * production) + 0.5);

                    //check if there are enough resources to produce
                    ValueCollection params = new ValueCollection();
                    params.put("amount", new IntegerPrimitive(deltaGoodCount));
                    InfoTable result = client.invokeService(ThingworxEntityTypes.Things, this.getName(), "checkIfCanProduce", params, 5000);
                    if (result.getFirstRow().getStringValue("result").equalsIgnoreCase("true")) {
                        //Check pushedstatus
                        if(this.getPropertyByName("pushedStatus").getValue().equals("4")){
                            this.setRemoteProperty("pushedStatus", "" + StatusEnum.UNPLANNED_DOWNTIME.ordinal());
                            for (ThingProperty pt : this.getAssetProperties()) {
                                if (pt.getName().equals("pushedStatus")) {
                                    pt.setValue("" + StatusEnum.UNPLANNED_DOWNTIME.ordinal());
                                }
                            }
                        }
                            
                        //calculate good and bad count
                        this.goodCount = deltaGoodCount + this.goodCount;
                        this.badCount = (int) (((this.failure / 100.0) * production) + 0.5 + this.badCount);

                        //Set the local production rate and percentage failure (for GUI)
                        for (ThingProperty tp : this.assetProperties) {
                            if (tp.getName().equalsIgnoreCase("ProductionRate")) {
                                tp.setValue(Integer.toString(this.prodRate));
                            } else if (tp.getName().equalsIgnoreCase("PercentageFailure")) {
                                if (this.failure > 100) {
                                    tp.setValue(Integer.toString(100));
                                } else if (this.failure < 0) {
                                    tp.setValue(Integer.toString(0));
                                } else {
                                    tp.setValue(Integer.toString(this.failure));
                                }
                            }
                        }

                        //add the new products to good and badcount
                        this.setRemoteProperty("GoodCount", Integer.toString(this.goodCount));
                        this.setRemoteProperty("BadCount", Integer.toString(this.badCount));
                    } else {
                        LOG.error("NOTIFICATIE [INFO] - {} - No production possible for {}.", AssetThing.class, this.getName());
                        //Can't produce, so unplanned downtime (both local and remote)
                        this.setRemoteProperty("pushedStatus", "" + StatusEnum.UNPLANNED_DOWNTIME.ordinal());
                        for (ThingProperty pt : this.getAssetProperties()) {
                            if (pt.getName().equals("pushedStatus")) {
                                pt.setValue("" + StatusEnum.UNPLANNED_DOWNTIME.ordinal());
                            }
                        }
                    }

                    //simulate small variations in parameter values
                    for (ThingProperty tp : this.assetProperties) {
                        if (!tp.getName().equalsIgnoreCase("pushedStatus")
                                && !tp.getName().equalsIgnoreCase("ProductionRate")
                                && !tp.getName().equalsIgnoreCase("PercentageFailure")
                                && !tp.getName().equalsIgnoreCase("NextAsset")
                                && !tp.getName().equalsIgnoreCase("IdealRunRate")) {
                            double val = Double.parseDouble(tp.getValue());
                            val = val + ((random.nextBoolean() ? 1 : -1) * (random.nextDouble() / 10 * val));
                            val = (double) Math.round(val * 100d) / 100d;
                            tp.setValue(Double.toString(val));
                            this.setRemoteProperty(tp.getName(), Double.toString(val));
                        }
                    }
                    this.updateSubscribedProperties(1000);
                } catch (Exception e) {
                    LOG.error("NOTIFICATIE [ERROR] - {} - Unable to simulate data of thing {}.", AssetThing.class, this.getName());
                    e.printStackTrace();
                }
                for (ThingProperty tp : this.assetProperties) {
                    if (!tp.getName().equalsIgnoreCase("pushedStatus")
                            && !tp.getName().equalsIgnoreCase("ProductionRate")
                            && !tp.getName().equalsIgnoreCase("PercentageFailure")
                            && !tp.getName().equalsIgnoreCase("NextAsset")
                            && !tp.getName().equalsIgnoreCase("IdealRunRate")) {
                        try {
                            InfoTable result = client.readProperty(ThingworxEntityTypes.Things, this.getName(), tp.getName(), true, Integer.SIZE);
                            String value = result.getFirstRow().getStringValue(tp.getName());
                            tp.setValue(value);
                        } catch (Exception ex) {
                            LOG.error("NOTIFICATIE [ERROR] - {} - Unable to read property {} of thing {}.", AssetThing.class, tp.getName(), this.getName());
                        }
                    }
                }
            }
        } catch (Exception ex) {
            LOG.error("NOTIFICATIE [ERROR] - {} - Unable te retrieve status for Thing {}.", AssetThing.class, this.getName());
        }
    }

    public void breakMachine() {
        this.setRemoteProperty("pushedStatus", "" + StatusEnum.UNPLANNED_DOWNTIME.ordinal());
        for (ThingProperty pt : this.getAssetProperties()) {
            if (pt.getName().equals("pushedStatus")) {
                pt.setValue("" + StatusEnum.UNPLANNED_DOWNTIME.ordinal());
            }
        }
        this.machineDown = true;
        LOG.info("NOTIFICATIE [INFO] - Thing {} is BROKEN. ", this.getName());
    }

    public void performMaintenance() {
        this.setRemoteProperty("pushedStatus", "" + StatusEnum.PLANNED_DOWNTIME.ordinal());
        for (ThingProperty pt : this.getAssetProperties()) {
            if (pt.getName().equals("pushedStatus")) {
                pt.setValue("" + StatusEnum.PLANNED_DOWNTIME.ordinal());
            }
        }
        this.machineDown = true;
        LOG.info("NOTIFICATIE [INFO] - Thing {} is DOWN FOR MAINTENANCE. ", this.getName());
    }

    public void restartMachine() {
        this.setRemoteProperty("pushedStatus", "" + StatusEnum.RUNNING.ordinal());
        for (ThingProperty pt : this.getAssetProperties()) {
            if (pt.getName().equals("pushedStatus")) {
                pt.setValue("" + StatusEnum.RUNNING.ordinal());
            }
        }
        this.machineDown = false;
        this.prodRate = initProdRate;
        LOG.info("NOTIFICATIE [INFO] - Thing {} was RESTARTED. ", this.getName());
    }

    public void setNewProdRate(int rate) {
        this.GUIProdRate = rate;
    }

}
