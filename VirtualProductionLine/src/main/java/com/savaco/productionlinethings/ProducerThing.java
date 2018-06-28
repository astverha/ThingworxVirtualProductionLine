/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.savaco.productionlinethings;

import com.savaco.interfaces.IProducer;
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
    )
})

public class ProducerThing extends BaseMachineTemplate implements IProducer {

    public ProducerThing(String name, String description, ConnectedThingClient client, BaseMachineTemplate nextMachine, BaseMachineTemplate prevMachine) {
        super(name, description, client, nextMachine, prevMachine);
    }
    
    @Override
    public void buyResources(int amount){
        int freeSpace = this.bufferCapacity-this.bufferQuantity;
        if(freeSpace > amount){
            this.bufferQuantity += amount;
        } else {
            this.bufferQuantity += freeSpace;
        }
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
