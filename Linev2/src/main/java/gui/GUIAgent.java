package gui;

import configuration.AssetThing;
import configuration.ConfigurationAgent;
import java.util.List;

public class GUIAgent {
    
    private final ConfigurationAgent agent;
    private AssetThing selectedAsset;
    
    public GUIAgent(ConfigurationAgent agent){
        this.agent = agent;
    }
    
    public List<AssetThing> getAllThings(){
        return agent.getThings();
    }

    public void setSelectedAsset(AssetThing asset) {
        this.selectedAsset = asset;
    }

    public AssetThing getSelectedAsset() {
        return this.selectedAsset;
    }
    
    public AssetThing getAssetThingByName(String name){
       return this.agent.getThingByName(name);
    }
    
    public void setProductionRate(int rate){
        selectedAsset.setNewProdRate(rate);
    }
    
    public void breakMachine(){
        selectedAsset.breakMachine();
    }
    
    public void performMaintenance(){
        selectedAsset.performMaintenance();
    }
    
    public void restartMachine(){
        selectedAsset.restartMachine();
    }

    void togglePause() {
        agent.togglePause();
    }
    
    
}
