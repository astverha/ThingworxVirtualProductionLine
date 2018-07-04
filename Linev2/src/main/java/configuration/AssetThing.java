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
import com.thingworx.types.primitives.BooleanPrimitive;
import com.thingworx.types.primitives.IntegerPrimitive;
import com.thingworx.types.primitives.NumberPrimitive;
import com.thingworx.types.primitives.StringPrimitive;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Administrator
 */
public class AssetThing extends VirtualThing {

    private static final Logger LOG = LoggerFactory.getLogger(AssetThing.class);

    private final String name;
    private final TypeEnum type;
    private final List<ThingProperty> assetProperties;

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
        } catch(Exception e){
            LOG.error("NOTIFICATIE [ERROR] - {} - An exception occurred while initializing an asset", AssetThing.class);
        }
    }

    public void initializeProperties(){
        
    }
    
    @Override
    public String getName() {
        return name;
    }

    public List<ThingProperty> getAssetProperties() {
        return assetProperties;
    }
}
