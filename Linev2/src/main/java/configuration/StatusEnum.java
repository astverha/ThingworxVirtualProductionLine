/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package configuration;

public enum StatusEnum {
    NOT_CONFIGURED,
    WARNING,
    RUNNING,
    PLANNED_DOWNTIME,
    UNPLANNED_DOWNTIME,
    UNAVAILABLE;
    
    public static String convertIntToState(int numb){
        String status = null;
        switch(numb){
            case 0: status = StatusEnum.NOT_CONFIGURED.toString();
                break;
            case 1: status = StatusEnum.WARNING.toString();
                break;
            case 2: status = StatusEnum.RUNNING.toString();
                break;
            case 3: status = StatusEnum.PLANNED_DOWNTIME.toString();
                break;
            case 4: status = StatusEnum.UNPLANNED_DOWNTIME.toString();
                break;
            case 5: status = status = StatusEnum.UNAVAILABLE.toString();
                break;
        }
        return status;
    }
}

