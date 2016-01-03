package com.plusline.medialarm.management;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.plusline.medialarm.type.Alarm;
import com.plusline.medialarm.type.DrugInfo;
import com.plusline.medialarm.util.Debug;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *  알람 관리 클래스
 */
public class AlarmManager {
    private static final String tag = "AlarmManager";

    private int maxNo = 0;
    private Map<Integer, Alarm> alarmMap = new HashMap<>();
    private DatabaseHandler databaseHandler;

    //
    //
    //
    private AlarmManager() {}

    private void init(Context context) {
        databaseHandler = new DatabaseHandler(context);
        loadAllAlarms();
    }

    private void loadAllAlarms() {
        Cursor cursor = databaseHandler.loadAllAlarms();
        if(null != cursor) {
            while (cursor.moveToNext()) {
                Alarm alarm = Alarm.create(cursor);
                if(maxNo < alarm.getNo()) {
                    maxNo = alarm.getNo();
                }

                Log.d(tag, "Loaded alarm: " + alarm);
                alarmMap.put(alarm.getNo(), alarm);
            }
        }
    }


    public int getNextNo() {
        return ++maxNo;
    }

    public List<Alarm> getAlarms() {
        List<Alarm> alarms = new ArrayList<>();
        alarms.addAll(alarmMap.values());
        return alarms;
    }

    public void addAlarm(Alarm alarm) {
        alarmMap.put(alarm.getNo(), alarm);
    }

    public Alarm getAlarm(int alarmNo) {
        if(!alarmMap.containsKey(alarmNo)) {
            Log.e(tag, "Can't find alarm info. no is " + alarmNo);
            return null;
        }
        return alarmMap.get(alarmNo);
    }

    public void removeAlarm(int alarmNo) {
        // 알람과 약물은 1:1 관계이므로 알람이 삭제되면 약도 삭제되어야 한다.
        Log.d(tag, "Try to remove alarm: " + alarmNo);
        Alarm alarm = alarmMap.get(alarmNo);
        if(null == alarm) {
            return;
        }

        alarmMap.remove(alarmNo);
        databaseHandler.deleteAlarm(alarmNo);

        DrugManager.get().removeDrug(alarm.getDrugNo());
    }

    public void updateLastChimedTime(Alarm alarm) {
        databaseHandler.updateLastChimedTime(
                alarm.getNo(),
                alarm.getLastChimedTime());
    }





    //////////////////////////////////////////////////////////////////////////////////////////////
    // static methods


    private static AlarmManager manager = new AlarmManager();
    public static AlarmManager get() {
        return manager;
    }

    public static void initialize(Context context) {
        manager.init(context);
    }

}
