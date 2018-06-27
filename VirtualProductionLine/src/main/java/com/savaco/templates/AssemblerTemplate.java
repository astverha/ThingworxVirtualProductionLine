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
public abstract class AssemblerTemplate extends BaseMachineTemplate {
    
    private int resourceALevel;
    private int resourceBLevel;
    private int resourceCLevel;
    
    public AssemblerTemplate(String name, String description, ConnectedThingClient client, BaseMachineTemplate nextMachine, BaseMachineTemplate prevMachine){
        super(name, description, client, nextMachine, prevMachine);
    }
    
    public void buyResourceA(int amount){
        this.resourceALevel += amount;
    }

    public int getResourceALevel() {
        return resourceALevel;
    }

    public void setResourceALevel(int resourceALevel) {
        this.resourceALevel = resourceALevel;
    }

    public int getResourceBLevel() {
        return resourceBLevel;
    }

    public void setResourceBLevel(int resourceBLevel) {
        this.resourceBLevel = resourceBLevel;
    }

    public int getResourceCLevel() {
        return resourceCLevel;
    }

    public void setResourceCLevel(int resourceCLevel) {
        this.resourceCLevel = resourceCLevel;
    }
    
    
}
