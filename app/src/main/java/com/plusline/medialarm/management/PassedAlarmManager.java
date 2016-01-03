package com.plusline.medialarm.management;

import com.plusline.medialarm.type.DrugInfo;

import java.util.Stack;

/**
 *
 *
 */
public class PassedAlarmManager {

    private Stack<DrugInfo> drugInfoStack = new Stack<>();
    private DrugInfo lastDrugInfo = null;

    //
    //
    //
    private PassedAlarmManager() {

    }

    public void pushDrug(DrugInfo drugInfo) {
        lastDrugInfo = drugInfo;
        //drugInfoStack.push(drugInfo);
    }


    public DrugInfo getDrug() {
//        if(drugInfoStack.isEmpty()) {
//            return null;
//        }
//
//        return drugInfoStack.pop();

        DrugInfo tempDrug = lastDrugInfo;
        lastDrugInfo = null;
        return tempDrug;
    }


    private static PassedAlarmManager manager = new PassedAlarmManager();
    public static PassedAlarmManager get() {
        return manager;
    }

}
