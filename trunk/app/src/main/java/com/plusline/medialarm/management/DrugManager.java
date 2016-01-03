package com.plusline.medialarm.management;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.plusline.medialarm.type.DrugInfo;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 *  약물 관리 클래스
 */
public class DrugManager {
    private static final String tag = "DrugManager";

    private int maxNo = 0;
    private Map<Integer, DrugInfo> drugInfoMap = new HashMap<>();

    DatabaseHandler databaseHandler;

    //
    //
    //
    private DrugManager() {

    }

    private void init(Context context) {
        databaseHandler = new DatabaseHandler(context);
        loadAllDrugInfo();
    }

    private void loadAllDrugInfo() {
        Cursor cursor = databaseHandler.loadAllDrugInfo();
        if(null != cursor) {
            while (cursor.moveToNext()) {
                DrugInfo drugInfo = DrugInfo.create(cursor);
                if(maxNo < drugInfo.getNo()) {
                    maxNo = drugInfo.getNo();
                }

                Log.d(tag, "Loaded DrugInfo: " + drugInfo);
                drugInfoMap.put(drugInfo.getNo(), drugInfo);
            }
        }
    }


    public int getNextNo() {
        return ++maxNo;
    }

    // 약물 추가는 현재 카트리지를 선택한 UI 서만 가능하다.
    public void addDrugInfo(DrugInfo drugInfo) {
        databaseHandler.saveDrugInfo(
                SelectedDrugCase.get().getSelectedCartridge().getNo(),
                drugInfo);
        drugInfoMap.put(drugInfo.getNo(), drugInfo);
    }


    public void updateDrugInfo(DrugInfo drugInfo) {
        databaseHandler.updateDrugInfo(drugInfo);
        drugInfoMap.put(drugInfo.getNo(), drugInfo);
    }


    public DrugInfo getDrug(int drugNo) {
        return drugInfoMap.get(drugNo);
    }

    public void removeDrug(int drugNo) {
        Log.d(tag, "Try to remove drug: " + drugNo);
        DrugInfo drugInfo = drugInfoMap.get(drugNo);
        if(null == drugInfo) {
            return;
        }

        drugInfoMap.remove(drugNo);
        AlarmManager.get().removeAlarm(drugInfo.getAlarmNo());
        databaseHandler.deleteDrug(drugNo);
    }



    ////////////////////////////////////////////////////////////////////////////////////////////
    // static methods

    private static DrugManager manager = new DrugManager();
    public static DrugManager get() {
        return manager;
    }

    public static void initialize(Context context) {
        manager.init(context);
    }

}
