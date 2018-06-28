/*
 * Demo project internship Savaco
 * Arne Verhaeghe & Dwight Van Lancker
 */
package com.savaco.interfaces;

/**
 *
 * @author Administrator
 */
public interface IBaseMachine {
    public void produce() throws Exception;
    public void adjustMachines(Object origin) throws Exception;
}
