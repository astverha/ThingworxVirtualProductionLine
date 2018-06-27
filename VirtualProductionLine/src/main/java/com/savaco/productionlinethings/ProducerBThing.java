/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.savaco.productionlinethings;

import com.savaco.templates.BaseMachineTemplate;
import com.savaco.templates.ProducerTemplate;
import com.thingworx.communications.client.ConnectedThingClient;

/**
 *
 * @author Administrator
 */
public class ProducerBThing extends ProducerTemplate {

    public ProducerBThing(String name, String description, ConnectedThingClient client,
            BaseMachineTemplate nextMachine, BaseMachineTemplate prevMachine) {
        super(name, description, client, nextMachine, prevMachine);
    }

    @Override
    public void adjustMachines(Object origin) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void produce() throws Exception{
        if(this.bufferQuantity > 0){
            this.bufferQuantity--;
            if(this.nextMachine.getBufferQuantity() < this.nextMachine.getBufferCapacity()){
                this.nextMachine.setBufferQuantity(this.nextMachine.getBufferQuantity()+1);
            }
        }
        else {
            throw new Exception("Buffer is already empty.");
        }
    }
    
}
