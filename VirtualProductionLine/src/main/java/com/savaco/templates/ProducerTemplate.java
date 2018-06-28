/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.savaco.templates;

import com.thingworx.communications.client.ConnectedThingClient;

/**
 *
 * @author Administrator
 */
public abstract class ProducerTemplate extends BaseMachineTemplate {

    public ProducerTemplate(String name, String description, ConnectedThingClient client, BaseMachineTemplate nextMachine, BaseMachineTemplate prevMachine) {
        super(name, description, client, nextMachine, prevMachine);
    }
    
    public void buyResources(int amount){
        int freeSpace = this.bufferCapacity-this.bufferQuantity;
        if(freeSpace > amount){
            this.bufferQuantity += amount;
        } else {
            this.bufferQuantity += freeSpace;
        }
    }

    @Override
    public abstract void adjustMachines(Object origin) throws Exception;
}
