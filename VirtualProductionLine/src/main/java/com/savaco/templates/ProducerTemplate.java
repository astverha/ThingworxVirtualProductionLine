/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.savaco.templates;

/**
 *
 * @author Administrator
 */
public abstract class ProducerTemplate extends BaseMachineTemplate {
    
    public ProducerTemplate(){}
    
    public void buyResources(int amount){
        super.bufferCapacity += amount;
    }
}
