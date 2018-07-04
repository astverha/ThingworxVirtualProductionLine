/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package configuration;

import com.stage.client.ThingworxClient;
import com.thingworx.communications.client.ConnectedThingClient;
import com.thingworx.communications.client.things.VirtualThing;
import com.thingworx.metadata.PropertyDefinition;
import com.thingworx.types.BaseTypes;
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
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.Utilities;

/**
 *
 * @author Administrator
 */
public class AssetThing extends VirtualThing {

    private static final Logger LOG = LoggerFactory.getLogger(AssetThing.class);

    private final String name;
    private final TypeEnum type;
    private final List<ThingProperty> assetProperties;
    private int prodRate;
    private double failure;

    public AssetThing(String name, TypeEnum type, List<ThingProperty> assetProperties, ThingworxClient client) {
        super(name, "", client);
        this.name = name;
        this.type = type;
        this.assetProperties = assetProperties;

        try {
            for (int i = 0; i < this.assetProperties.size(); i++) {
                ThingProperty node = this.assetProperties.get(i);
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
                aspects.put(Aspects.ASPECT_ISPERSISTENT, new BooleanPrimitive(false));
                aspects.put(Aspects.ASPECT_ISREADONLY, new BooleanPrimitive(true));
                aspects.put("pushType", new StringPrimitive(DataChangeType.ALWAYS.name()));
                aspects.put(Aspects.ASPECT_ISLOGGED, new BooleanPrimitive(true));

                pd.setAspects(aspects);
                super.defineProperty(pd);
            }
        } catch (Exception e) {
            LOG.error("NOTIFICATIE [ERROR] - {} - An exception occurred while initializing an asset", AssetThing.class);
        }
    }

    public void initializeProperties() {
        try {
            for (ThingProperty tp : this.assetProperties) {
                // Set pushedStatus, Temperature and NextAsset
                if (tp.getName().equals("pushedStatus")) {
                    this.setRemoteProperty(tp.getName(), tp.getValue());
                } else if(tp.getName().equals("Temperature")){
                    this.setRemoteProperty(tp.getName(), tp.getValue());
                } else if(tp.getName().equals("NextAsset")){
                    this.setRemoteProperty(tp.getName(), "Asset_" + tp.getValue());
                } else if(tp.getName().equals("ProductionRate")){
                    this.prodRate = Integer.parseInt(tp.getValue());
                } else if(tp.getName().equals("PercentageFailure")){
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
            LOG.error("NOTIFICATIE [ERROR] - {} - Timeout waiting for update of property of thing {).", AssetThing.class, this.getName());
        }
    }

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
            vtq.setTime(new DateTime());
            vtq.setQuality(QualityStatus.GOOD);
            this.setPropertyVTQ(name, vtq, true);
        } catch (Exception e) {
            LOG.error("NOTIFICATIE [ERROR] - {} - Unable to update property of thing {).", AssetThing.class, this.getName());
        }
    }

    @Override
    public String getName() {
        return name;
    }

    public List<ThingProperty> getAssetProperties() {
        return assetProperties;
    }
}
