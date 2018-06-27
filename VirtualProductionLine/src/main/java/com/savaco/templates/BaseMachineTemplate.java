/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.savaco.templates;

import com.thingworx.communications.client.ConnectedThingClient;
import com.thingworx.communications.client.things.VirtualThing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Administrator
 */
public abstract class BaseMachineTemplate extends VirtualThing {
    
    protected static final Logger LOG = LoggerFactory.getLogger(BaseMachineTemplate.class);

    protected String state;
    
    protected int temperature;
    protected int productionRate;
    protected int bufferCapacity;
    protected int bufferQuantity;
    
    protected BaseMachineTemplate nextMachine;
    protected BaseMachineTemplate prevMachine;
    
    protected final String name;
    protected final ConnectedThingClient client;
    
    /**
     * @param name The name of the thing.
     * @param description A description of the thing.
     * @param client The client that this thin is associated with.
     */
    public BaseMachineTemplate(String name, String description, ConnectedThingClient client,
            BaseMachineTemplate nextMachine, BaseMachineTemplate prevMachine){
        super(name, description, client);
        this.initializeFromAnnotations();
        
        this.name = name;
        this.client = client;
        this.nextMachine = nextMachine;
        this.prevMachine = prevMachine;
    }
    
    public abstract void produce() throws Exception;
    public abstract void adjustMachines(Object origin) throws Exception;

    public String getState() {
        return state;
    }

    public int getBufferCapacity() {
        return bufferCapacity;
    }

    public int getBufferQuantity() {
        return bufferQuantity;
    }

    public void setBufferQuantity(int bufferQuantity) {
        this.bufferQuantity = bufferQuantity;
    }
    
    
}
