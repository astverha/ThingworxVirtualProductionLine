/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.savaco.templates;

import com.savaco.interfaces.IBaseMachine;
import com.thingworx.communications.client.ConnectedThingClient;
import com.thingworx.communications.client.things.VirtualThing;
import com.thingworx.types.primitives.BooleanPrimitive;
import com.thingworx.types.primitives.IntegerPrimitive;
import com.thingworx.types.primitives.NumberPrimitive;
import com.thingworx.types.primitives.StringPrimitive;
import java.util.Random;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Administrator
 */
public abstract class BaseMachineTemplate extends VirtualThing implements IBaseMachine {
    
    protected static final Logger LOG = LoggerFactory.getLogger(BaseMachineTemplate.class);

    public enum State {
        RUNNING,
        PLANNED_DOWN,
        UNPLANNED_DOWN,
        WARNING,
        UNAVAILABLE
    };
    
    protected State state;
    
    protected int temperature;
    protected int bufferQuantity;
    protected int productionRate;
    protected int bufferCapacity;
    
    protected BaseMachineTemplate nextMachine;
    protected BaseMachineTemplate prevMachine;
    
    protected final String name;
    protected final ConnectedThingClient client;
    
    /**
     * @param name The name of the thing.
     * @param description A description of the thing.
     * @param client The client that this thin is associated with.
     * @param nextMachine The next machine in the production line.
     * @param prevMachine The next machine in the production line.
     */
    public BaseMachineTemplate(String name, String description, ConnectedThingClient client,
            BaseMachineTemplate nextMachine, BaseMachineTemplate prevMachine) {
        super(name, description, client);
        this.initializeFromAnnotations();
        
        this.name = name;
        this.client = client;
        
        //this should init from json file
        this.temperature = 20;
        this.nextMachine = nextMachine;
        this.prevMachine = prevMachine;
        this.state = State.RUNNING;
        this.productionRate = 200;
        this.bufferCapacity = 10000;
        this.bufferQuantity = 7000;
    }
    
    @Override
    public void produce() throws Exception {
        if(this.bufferQuantity > 0 && ( this.state == State.RUNNING || this.state == State.WARNING )){
            this.bufferQuantity--;
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
    public abstract void adjustMachines(Object origin) throws Exception;

    public int getBufferCapacity() {
        return bufferCapacity;
    }

    public int getBufferQuantity() {
        return bufferQuantity;
    }

    public void setBufferQuantity(int bufferQuantity) {
        this.bufferQuantity = bufferQuantity;
    }

    @Override
    public void processScanRequest() throws Exception {
        Random random = new Random();
        
        //make temperature vary up to five degrees
        int dTemperature = random.nextInt(5);
        int sign = random.nextInt(2);
        if(sign == 0){
            sign = -1;
        }
        this.temperature = this.temperature + (sign * dTemperature);
        
        //Start producing. We assume a processScanRequest every minute, productionRate is in units/min
        if(this.state == State.RUNNING || this.state == State.WARNING){
            for(int i=0; i<this.productionRate; i++){
                produce();
            }
        }
        
        try {
            this.setPropertyValue("Temperature", new IntegerPrimitive(this.temperature));
            this.setPropertyValue("BufferQuantity", new IntegerPrimitive(this.bufferQuantity));
            this.setPropertyValue("BufferCapacity", new IntegerPrimitive(this.bufferCapacity));
            this.setPropertyValue("ProductionRate", new IntegerPrimitive(this.productionRate));
            this.setPropertyValue("ProductionRate", new IntegerPrimitive(this.productionRate));

            this.updateSubscribedProperties(10000);
        } catch (Exception e) {
            LOG.error("There was a problem with setting properties values", e);
        }
    }
    
    
}
