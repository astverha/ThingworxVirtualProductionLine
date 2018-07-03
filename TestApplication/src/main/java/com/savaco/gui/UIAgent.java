/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.savaco.gui;

import com.savaco.ConfigurationAgent.AssetThing;
import com.savaco.ConfigurationAgent.ConfigurationAgent;
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
    
    public UIAgent(ConfigurationAgent agent){
        status = new ArrayList<>();
        prodRates = new ArrayList<>();
        this.agent = agent;
    }
    
    public void getAllStatus(){
        //System.out.println("TESTLOGG ---- Lengte lijst voor: " + status.size());
        for(AssetThing at : agent.getAssetsAsThings()){
            //status.add(at.getPropertyByName("status").getValue());
            System.out.println(at.getPropertyByName("status").getValue());
        }
    }
    
    public void getAllProdRates(){
        for(AssetThing at: agent.getAssetsAsThings()){
            prodRates.add(Integer.parseInt(at.getPropertyByName("ProductionRate").getValue()));
        }
    }
    
    public List<String> getStatus(){
        return status;
    }
    
    public List<Integer> getProdRates(){
        return prodRates;
    }
}
