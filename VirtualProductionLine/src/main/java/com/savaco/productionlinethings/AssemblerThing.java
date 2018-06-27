/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.savaco.productionlinethings;

import com.savaco.templates.AssemblerTemplate;
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
            name="ResourceALevel",
            description="The amount of resources of part A",
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
            name="ResourceBLevel",
            description="The amount of resources of part B",
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
            name="ResourceCLevel",
            description="The amount of resources of part C",
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


public class AssemblerThing extends AssemblerTemplate {
    
    public AssemblerThing(String name, String description, ConnectedThingClient client, BaseMachineTemplate nextMachine, BaseMachineTemplate prevMachine) {
        super(name, description, client, nextMachine, prevMachine);
    }
    
    
}
