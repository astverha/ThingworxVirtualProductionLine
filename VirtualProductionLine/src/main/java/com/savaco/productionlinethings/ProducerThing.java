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
    
    private final char part;
    private final AssemblerThing assembler;

    public ProducerThing(String name, String description, ConnectedThingClient client, BaseMachineTemplate nextMachine, BaseMachineTemplate prevMachine, char part) {
        super(name, description, client, nextMachine, prevMachine);
        this.part = part;
        this.assembler = (AssemblerThing) nextMachine;
        LOG.info("{} is turned on.", this.name);
    }
    
    
    
    @Override
    public void buyResources(int amount){
        LOG.info("{} bought resources.", this.name);
        int freeSpace = this.bufferCapacity-this.bufferQuantity;
        if(freeSpace > amount){
            this.bufferQuantity += amount;
        } else {
            this.bufferQuantity += freeSpace;
        }
    }

    @Override
    public void produce() throws Exception {
        if(this.bufferQuantity > 0 && ( this.state == State.RUNNING || this.state == State.WARNING )){
            //deplete own buffer
            this.bufferQuantity--;
            
            //add to correct resourceLevel of assembler
            if(this.nextMachine.getBufferQuantity() < this.nextMachine.getBufferCapacity()){
                switch(part){
                    case 'B':
                        this.assembler.addResourceB();
                        break;
                    case 'C':
                        this.assembler.addResourceC();
                        break;
                }
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
        
        LOG.info("{} produced.", this.name);
    }

    @Override
    public void adjustMachines(Object origin) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    
}
