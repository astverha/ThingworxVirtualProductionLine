package configuration;

import com.stage.client.ThingworxClient;
import com.thingworx.communications.client.things.VirtualThing;
import com.thingworx.metadata.PropertyDefinition;
import com.thingworx.relationships.RelationshipTypes.ThingworxEntityTypes;
import com.thingworx.types.BaseTypes;
import com.thingworx.types.InfoTable;
import com.thingworx.types.collections.AspectCollection;
import com.thingworx.types.constants.Aspects;
import com.thingworx.types.constants.DataChangeType;
import com.thingworx.types.constants.QualityStatus;
import com.thingworx.types.primitives.BooleanPrimitive;
import com.thingworx.types.primitives.IntegerPrimitive;
import com.thingworx.types.primitives.NumberPrimitive;
import com.thingworx.types.primitives.StringPrimitive;
import com.thingworx.types.primitives.structs.VTQ;
import java.util.List;
import java.util.Random;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.Utilities;

public class AssetThing extends VirtualThing {

    private static final Logger LOG = LoggerFactory.getLogger(AssetThing.class);

    private final String name;
    private final TypeEnum type;
    private final List<ThingProperty> assetProperties;
    private ThingworxClient client;
    private int initProdRate;   //ProdRate used when Asset is restarted after (un)planned_downtime
    private int GUIProdRate;
    private int prodRate;
    private double failure;

    public AssetThing(String name, TypeEnum type, List<ThingProperty> assetProperties, ThingworxClient client) {
        super(name, "", client);
        this.name = name;
        this.type = type;
        this.assetProperties = assetProperties;
        this.client = client;

        try {
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
            PropertyDefinition goodCount;
            aspects = new AspectCollection();
            goodCount = new PropertyDefinition("GoodCount", "", BaseTypes.NUMBER);
            aspects.put(Aspects.ASPECT_DATACHANGETYPE, new StringPrimitive("VALUE"));
            aspects.put(Aspects.ASPECT_DATACHANGETHRESHOLD, new NumberPrimitive(0.0));
            aspects.put(Aspects.ASPECT_CACHETIME, new IntegerPrimitive(0));
            aspects.put(Aspects.ASPECT_ISPERSISTENT, new BooleanPrimitive(true));
            aspects.put(Aspects.ASPECT_ISREADONLY, new BooleanPrimitive(true));
            aspects.put("pushType", new StringPrimitive(DataChangeType.ALWAYS.name()));
            goodCount.setAspects(aspects);
            super.defineProperty(goodCount);

            //GoodCount
            PropertyDefinition badCount;
            aspects = new AspectCollection();
            badCount = new PropertyDefinition("BadCount", "", BaseTypes.NUMBER);
            aspects.put(Aspects.ASPECT_DATACHANGETYPE, new StringPrimitive("VALUE"));
            aspects.put(Aspects.ASPECT_DATACHANGETHRESHOLD, new NumberPrimitive(0.0));
            aspects.put(Aspects.ASPECT_CACHETIME, new IntegerPrimitive(0));
            aspects.put(Aspects.ASPECT_ISPERSISTENT, new BooleanPrimitive(true));
            aspects.put(Aspects.ASPECT_ISREADONLY, new BooleanPrimitive(true));
            aspects.put("pushType", new StringPrimitive(DataChangeType.ALWAYS.name()));
            badCount.setAspects(aspects);
            super.defineProperty(badCount);
        } catch (Exception e) {
            LOG.error("NOTIFICATIE [ERROR] - {} - An exception occurred while initializing an asset", AssetThing.class);
        }
    }

    public void initializeProperties(ThingworxClient myClient) {
        try {
            for (ThingProperty tp : this.assetProperties) {
                // Set pushedStatus, Temperature and NextAsset
                if (tp.getName().equals("pushedStatus")) {
                    this.setRemoteProperty(tp.getName(), tp.getValue());
                } else if (tp.getName().equals("Temperature")) {
                    this.setRemoteProperty(tp.getName(), tp.getValue());
                } else if (tp.getName().equals("NextAsset")) {
                    this.setRemoteProperty(tp.getName(), "Asset_" + tp.getValue());
                } else if (tp.getName().equals("ProductionRate")) {
                    this.prodRate = Integer.parseInt(tp.getValue());
                    this.GUIProdRate = prodRate;
                    this.initProdRate = prodRate;
                } else if (tp.getName().equals("PercentageFailure")) {
                    this.failure = Double.parseDouble(tp.getValue());
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
            e.printStackTrace();
        }
    }

    public void setRemoteProperty(String name, String value) {
        try {
            VTQ vtq = new VTQ();
            if (Utilities.isInteger(value)) {
                vtq.setValue(new IntegerPrimitive(Integer.parseInt(value)));
            } else if (Utilities.isDouble(value)) {
                vtq.setValue(new NumberPrimitive(Double.parseDouble(value)));
            } else {
                vtq.setValue(new StringPrimitive(value));
            }
            //vtq.setTime(new DateTime());
            //vtq.setQuality(QualityStatus.GOOD);
            //this.setPropertyVTQ(name, vtq, true);
            client.writeProperty(ThingworxEntityTypes.Things, this.getName(), name, vtq.getValue(), Integer.SIZE);
        } catch (Exception e) {
            LOG.error("NOTIFICATIE [ERROR] - {} - Unable to update property {} of thing {}.", AssetThing.class, name, this.getName());
            e.printStackTrace();
        }
    }

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

    public void simulateData() {
        try {
            int dProdRate = this.GUIProdRate - this.prodRate;

            if (dProdRate != 0) {
                for (ThingProperty tp : this.assetProperties) {
                    if (!tp.getName().equalsIgnoreCase("pushedStatus")
                            && !tp.getName().equalsIgnoreCase("ProductionRate")
                            && !tp.getName().equalsIgnoreCase("PercentageFailure")
                            && !tp.getName().equalsIgnoreCase("NextAsset")) {
                        double val = Double.parseDouble(tp.getValue());
                        val = val * (this.GUIProdRate / this.prodRate);
                        tp.setValue(Double.toString(val));
                    }
                }
                this.failure = this.failure * (this.GUIProdRate / this.prodRate);
            } else {
                Random random = new Random();
                for (ThingProperty tp : this.assetProperties) {
                    if (!tp.getName().equalsIgnoreCase("pushedStatus")
                            && !tp.getName().equalsIgnoreCase("ProductionRate")
                            && !tp.getName().equalsIgnoreCase("PercentageFailure")
                            && !tp.getName().equalsIgnoreCase("NextAsset")) {
                        double val = Double.parseDouble(tp.getValue());
                        val = val + ((random.nextBoolean() ? 1 : -1) * (random.nextDouble() / 10 * val));
                        tp.setValue(Double.toString(val));
                    }
                }
            }

            this.prodRate = this.GUIProdRate;
            double production = this.prodRate / 60 * 5;
            int goodCount = (int) (((1 - this.failure) * production) + 0.5);
            int badCount = (int) ((this.failure * production) + 0.5);

            this.setRemoteProperty("GoodCount", Integer.toString(goodCount));
            this.setRemoteProperty("BadCount", Integer.toString(badCount));
            for (ThingProperty tp : this.assetProperties) {
                if (!tp.getName().equalsIgnoreCase("pushedStatus")
                        && !tp.getName().equalsIgnoreCase("ProductionRate")
                        && !tp.getName().equalsIgnoreCase("PercentageFailure")
                        && !tp.getName().equalsIgnoreCase("NextAsset")) {
                    this.setRemoteProperty(tp.getName(), tp.getValue());
                }
            }

            this.updateSubscribedProperties(1000);
        } catch (Exception e) {
            LOG.error("NOTIFICATIE [ERROR] - {} - Unable to simulate data of thing {}.", AssetThing.class, this.getName());
        }
    }

    public void setNewProdRate(int rate) {
        this.GUIProdRate = rate;
    }
}
