/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.savaco.productionlinethings;

import com.savaco.interfaces.IQualityChecker;
import com.savaco.templates.BaseMachineTemplate;
import com.thingworx.communications.client.ConnectedThingClient;
import com.thingworx.metadata.annotations.ThingworxPropertyDefinition;
import com.thingworx.metadata.annotations.ThingworxPropertyDefinitions;

/**
 *
 * @author Administrator
 */

@ThingworxPropertyDefinitions(properties = {
    @ThingworxPropertyDefinition(
            name="State",
            description="The state of the machine",
            baseType="STRING",
            aspects={"dataChangeType:ALWAYS",
                     "cacheTime:-1",
                     "isPersistent:TRUE",
                     "isReadOnly:FALSE",
                     "pushType:ALWAYS",
                     "defaultValue:RUNNING"
            }
    ),
    @ThingworxPropertyDefinition(
            name="Temperature",
            description="The temperature of the machine",
            baseType="INTEGER",
            aspects={"dataChangeType:ALWAYS",
                     "dataChangeThreshold:0",
                     "cacheTime:-1",
                     "isPersistent:TRUE",
                     "isReadOnly:FALSE",
                     "pushType:ALWAYS",
                     "defaultValue:0"
            }
    ),
    @ThingworxPropertyDefinition(
            name="ProductionRate",
            description="The production rate of the machine",
            baseType="INTEGER",
            aspects={"dataChangeType:ALWAYS",
                     "dataChangeThreshold:0",
                     "cacheTime:-1",
                     "isPersistent:TRUE",
                     "isReadOnly:FALSE",
                     "pushType:ALWAYS",
                     "defaultValue:0"
            }
    ),
    @ThingworxPropertyDefinition(
            name="BufferCapacity",
            description="The maximum capacity of this machine",
            baseType="INTEGER",
            aspects={"dataChangeType:ALWAYS",
                     "dataChangeThreshold:0",
                     "cacheTime:-1",
                     "isPersistent:TRUE",
                     "isReadOnly:FALSE",
                     "pushType:ALWAYS",
                     "defaultValue:0"
            }
    ),
    @ThingworxPropertyDefinition(
            name="BufferQuantity",
            description="The current amount in the buffer of the machine",
            baseType="INTEGER",
            aspects={"dataChangeType:ALWAYS",
                     "dataChangeThreshold:0",
                     "cacheTime:-1",
                     "isPersistent:TRUE",
                     "isReadOnly:FALSE",
                     "pushType:ALWAYS",
                     "defaultValue:0"
            }
    ),
    @ThingworxPropertyDefinition(
            name="TotalCount",
            description="The amount of products already produces",
            baseType="INTEGER",
            aspects={"dataChangeType:ALWAYS",
                     "dataChangeThreshold:0",
                     "cacheTime:-1",
                     "isPersistent:TRUE",
                     "isReadOnly:FALSE",
                     "pushType:ALWAYS",
                     "defaultValue:0"
            }
    ),
    @ThingworxPropertyDefinition(
            name="BadCount",
            description="The amount of bad products produced",
            baseType="INTEGER",
            aspects={"dataChangeType:ALWAYS",
                     "dataChangeThreshold:0",
                     "cacheTime:-1",
                     "isPersistent:TRUE",
                     "isReadOnly:FALSE",
                     "pushType:ALWAYS",
                     "defaultValue:0"
            }
    ),
    @ThingworxPropertyDefinition(
            name="Quota",
            description="Percentage of good products produced",
            baseType="INTEGER",
            aspects={"dataChangeType:ALWAYS",
                     "dataChangeThreshold:0",
                     "cacheTime:-1",
                     "isPersistent:TRUE",
                     "isReadOnly:FALSE",
                     "pushType:ALWAYS",
                     "defaultValue:0"
            }
    ),
    @ThingworxPropertyDefinition(
            name="DesiredQuota",
            description="Percentage of good products needed",
            baseType="INTEGER",
            aspects={"dataChangeType:ALWAYS",
                     "dataChangeThreshold:0",
                     "cacheTime:-1",
                     "isPersistent:TRUE",
                     "isReadOnly:FALSE",
                     "pushType:ALWAYS",
                     "defaultValue:0"
            }
    )
})


public class QualityCheckerThing extends BaseMachineTemplate implements IQualityChecker{
    
    protected int totalCount;
    protected int badCount;
    protected int quota;
    protected int desiredQuota;
    
    public QualityCheckerThing(String name, String description, ConnectedThingClient client, BaseMachineTemplate nextMachine, BaseMachineTemplate prevMachine){
        super(name, description, client, nextMachine, prevMachine);
        this.initializeFromAnnotations();
    }

    @Override
    public void checkQuality() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void produce() throws Exception {
        super.produce(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void adjustMachines(Object origin) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
