package gui;

import configuration.Asset;
import configuration.AssetThing;
import configuration.ConfigurationAgent;
import configuration.Line;
import java.util.ArrayList;
import java.util.List;

public class GUIAgent {
    
    private final ConfigurationAgent agent;
    private AssetThing selectedAsset;
    private Line selectedLine;
    private List<AssetThing> assets;
    
    public GUIAgent(ConfigurationAgent agent){
        this.agent = agent;
        assets = new ArrayList<>();
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

    public void togglePause() {
        agent.togglePause();
    }
    
    public List<Line> getLines(){
        return agent.getLines();
    }

    public void setSelectedLine(String name) {
        this.selectedLine = agent.getLineByName(name);
        
        //GET ALL ASSETS FOR NEW SELECTED LINE
        this.assets.clear();
        for(Asset a : this.selectedLine.getAssets()){
            assets.add(agent.getThingByName("Asset_" + a.getName()));
        }
    }
    
    public List<AssetThing> getAssetThingsFromLine(){
        return assets;
    }
      
}
