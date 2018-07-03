/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.savaco.gui;

import com.savaco.ConfigurationAgent.AssetThing;
import com.savaco.ConfigurationAgent.ConfigurationAgent;
import com.savaco.ConfigurationAgent.ThingProperty;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Administrator
 */
public class UIAgent {

    private List<String> status;
    private List<Integer> prodRates;
    private ConfigurationAgent agent;

    public UIAgent(ConfigurationAgent agent) {
        status = new ArrayList<>();
        prodRates = new ArrayList<>();
        this.agent = agent;
    }

    public void getAllStatus() {
        status.clear();
        ThingProperty pt = null;
        for (AssetThing at : agent.getAssetsAsThings()) {
            pt = at.getPropertyByName("status");
            if(pt != null){
                status.add(pt.getValue());
            }
        }
    }

    public void getAllProdRates() {
        prodRates.clear();
        ThingProperty pt = null;
        for (AssetThing at : agent.getAssetsAsThings()) {
            pt = at.getPropertyByName("ProductionRate");
            if(pt != null){
                prodRates.add(Integer.parseInt(pt.getValue()));
            }
        }
    }

    public List<String> getStatus() {
        return status;
    }

    public List<Integer> getProdRates() {
        return prodRates;
    }
}
