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

public class AssemblerThing extends BaseMachineTemplate implements IProducer {
    
    protected int resourceALevel;
    protected int resourceBLevel;
    protected int resourceCLevel;
    
    public AssemblerThing(String name, String description, ConnectedThingClient client, BaseMachineTemplate nextMachine, BaseMachineTemplate prevMachine){
        super(name, description, client, nextMachine, prevMachine);
        this.initializeFromAnnotations();
        
        this.resourceALevel = 400;
        this.resourceBLevel = 400;
        this.resourceCLevel = 400;
        LOG.info("{} is turned on.", this.name);
    }
    
    @Override
    public void buyResources(int amount){
        this.resourceALevel += amount;
    }

    @Override
    public void adjustMachines(Object origin) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void produce() throws Exception {
        //we 2 parts A, 2 parts B and 1 parts C = 5 parts
        if(this.bufferQuantity >= 5 && ( this.state == State.RUNNING || this.state == State.WARNING )){
            this.bufferQuantity-=5;
            if(this.nextMachine.getBufferQuantity() < this.nextMachine.getBufferCapacity()){
                this.nextMachine.setBufferQuantity(this.nextMachine.getBufferQuantity()+1);
            }
            else {
                //trigger alarm for full buffer of next machine
            }
        }
        else {
            //throw new Exception("Buffer is already empty.");
            this.state = State.UNPLANNED_DOWN;
        }
        
        //trigger alarm for low resources (5% of capacity)
        if(this.bufferQuantity < this.bufferCapacity*0.05){
            this.state = State.WARNING;
        }
    }

    @Override
    public void processScanRequest() throws Exception {
        super.processScanRequest();
        
        
    }

    void addResourceB() {
        this.resourceBLevel++;
        this.bufferQuantity++;
    }

    void addResourceC() {
        this.resourceCLevel++;
        this.bufferQuantity++;
    }
    
    
}
