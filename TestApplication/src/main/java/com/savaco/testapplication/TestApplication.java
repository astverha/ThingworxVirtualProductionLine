/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.savaco.testapplication;

import com.savaco.ConfigurationAgent.ConfigurationAgent;

/**
 *
 * @author Administrator
 */
public class TestApplication {
    public static void main(String[] args){
        ConfigurationAgent config = new ConfigurationAgent("configuration.xml");
        ProductLineClient client = config.getClient();
        client.startInitialization();
    }
}
