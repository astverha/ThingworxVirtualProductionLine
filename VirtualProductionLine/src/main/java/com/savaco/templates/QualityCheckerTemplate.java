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
public abstract class QualityCheckerTemplate extends BaseMachineTemplate {
    
    private int totalCount;
    private int badCount;
    private int quota;
    private int desiredQuota;
    
    public QualityCheckerTemplate(String name, String description, ConnectedThingClient client, BaseMachineTemplate nextMachine, BaseMachineTemplate prevMachine){
        super(name, description, client, nextMachine, prevMachine);
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public int getBadCount() {
        return badCount;
    }

    public void setBadCount(int badCount) {
        this.badCount = badCount;
    }

    public int getQuota() {
        return quota;
    }

    public void setQuota(int quota) {
        this.quota = quota;
    }

    public int getDesiredQuota() {
        return desiredQuota;
    }

    public void setDesiredQuota(int desiredQuota) {
        this.desiredQuota = desiredQuota;
    }
    
    
    
}
