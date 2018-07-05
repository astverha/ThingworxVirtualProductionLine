/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import configuration.AssetThing;
import configuration.ConfigurationAgent;
import java.util.List;

/**
 *
 * @author Administrator
 */
public class GUIAgent {
    
    private final ConfigurationAgent agent;
    
    public GUIAgent(ConfigurationAgent agent){
        this.agent = agent;
    }
    
    public List<AssetThing> getAllThings(){
        return agent.getThings();
    }
}
