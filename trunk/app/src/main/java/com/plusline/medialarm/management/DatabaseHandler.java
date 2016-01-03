package com.plusline.medialarm.management;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.plusline.medialarm.type.Alarm;
import com.plusline.medialarm.type.DrugInfo;
import com.plusline.medialarm.ui.Convert;

import java.util.Calendar;

/**
 * 데이터베이스 관리
 *
 */
public class DatabaseHandler {
    private static final String tag = "DatabaseHandler";

    private MediAlarmDBOpenHelper dbOpenHelper;
    private SQLiteDatabase database;

    public DatabaseHandler(Context context) {
        dbOpenHelper = new MediAlarmDBOpenHelper(context);
        database = dbOpenHelper.getWritableDatabase();
    }


    public Cursor loadCartridge(int dayOfWeek) {
        final String selectQuery = String.format(
                "select no, day, type from CARTRIDGE where day = %d;",
                dayOfWeek);

        if(database.isOpen()) {
            return database.rawQuery(selectQuery, null);
        } else {
            Log.e(tag, "The database is not opened.");
            return null;
        }
    }


    public Cursor loadAllDrugInfo() {
        final String selectQuery = "select no, user, name, color, mount, unit, mg, gr, alarm_no from DRUG_INFO; ";
        if(database.isOpen()) {
            return database.rawQuery(selectQuery, null);
        } else {
            Log.e(tag, "The database is not opened.");
            return null;
        }
    }


    public Cursor loadDrugIdList(int cartridgeNo) {
        String selectQuery = String.format(
                "select drug_no from CARTRIDGE_DRUG_MAP where cart_no = %d;",
                cartridgeNo);
        if(database.isOpen()) {
            return database.rawQuery(selectQuery, null);
        } else {
            Log.e(tag, "The database is not opened.");
            return null;
        }
    }


    public Cursor loadAllAlarms() {
        final String selectQuery = "select no, drug_no, period, hourOfDay, minute, last_alarm_time from ALARM_INFO; ";
        if(database.isOpen()) {
            return database.rawQuery(selectQuery, null);
        } else {
            Log.e(tag, "The database is not opened.");
            return null;
        }
    }


    public void saveDrugInfo(int cartridgeNo, DrugInfo drugInfo) {
        Alarm currentAlarm = AlarmManager.get().getAlarm(drugInfo.getAlarmNo());
        if(null == currentAlarm) {
            return;
        }

        insertAlarm(currentAlarm);
        insertDrugInfo(drugInfo);
        insertCartridgeDrugMap(cartridgeNo, drugInfo.getNo());
    }


    public void updateDrugInfo(DrugInfo drugInfo) {
        Alarm currentAlarm = AlarmManager.get().getAlarm(drugInfo.getAlarmNo());
        if(null == currentAlarm) {
            return;
        }

        updateAlaram(currentAlarm);
        updateDrugInfoInternal(drugInfo);
    }


    private void insertDrugInfo(DrugInfo drugInfo) {
        final String queryTemplate =
                "insert into DRUG_INFO values (%d, '%s', '%s', %d, %d, %d, %d, %d, %d );";

        try {
            String insertQuery = String.format(
                    queryTemplate,
                    drugInfo.getNo(),
                    drugInfo.getUser(),
                    drugInfo.getName(),
                    drugInfo.getColorIndex(),
                    0,
                    drugInfo.getUnit(),
                    drugInfo.getMg(),
                    drugInfo.getGr(),
                    drugInfo.getAlarmNo());

            database.execSQL(insertQuery);
            Log.d(tag, "Success to insert alarm: " + drugInfo);
        } catch (Exception e) {
            Log.e(tag, "Failed to insert drug info. ");
            Log.e(tag, "Message: " + e.getMessage());
        }
    }

    private void updateDrugInfoInternal(DrugInfo drugInfo) {
        final String queryTemplate =
                "update DRUG_INFO set name='%s', color=%d, unit=%d, mg=%d, gr=%d where no=%d;";

        try {
            String updateQuery = String.format(
                    queryTemplate,
                    drugInfo.getName(),
                    drugInfo.getColorIndex(),
                    drugInfo.getUnit(),
                    drugInfo.getMg(),
                    drugInfo.getGr(),
                    drugInfo.getNo());

            database.execSQL(updateQuery);
        } catch (Exception e) {
            Log.e(tag, "Failed to update drug info. ");
            Log.e(tag, "Message: " + e.getMessage());
        }
    }

    private void insertCartridgeDrugMap(int cartridgeNo, int drugNO) {
        final String queryTemplate = "insert into CARTRIDGE_DRUG_MAP values (%d, %d);";

        try {
            String insertQuery = String.format(
                    queryTemplate,
                    cartridgeNo,
                    drugNO);

            database.execSQL(insertQuery);
            Log.d(tag, "Success to insert Cart-Drug map: " + cartridgeNo + " " + drugNO);
        } catch (Exception e) {
            Log.e(tag, "Failed to insert alarm info. ");
            Log.e(tag, "Message: " + e.getMessage());
        }
    }

    private void insertAlarm(Alarm alarm) {
        final String queryTemplate =
                "insert into ALARM_INFO values (%d, %d, %d, %d, %d, '%s');";
        try {
            String insertQuery = String.format(
                    queryTemplate,
                    alarm.getNo(),
                    alarm.getDrugNo(),
                    alarm.getPeriod().toInt(),
                    alarm.getHourOfDay(),
                    alarm.getMinute(),
                    Convert.toStr(alarm.getLastChimedTime()));

            database.execSQL(insertQuery);
            Log.d(tag, "Success to insert alarm: " + alarm);
        } catch (Exception e) {
            Log.e(tag, "Failed to insert alarm info. ");
            Log.e(tag, "Message: " + e.getMessage());
        }
    }

    private void updateAlaram(Alarm alarm) {
        final String queryTemplate =
                "update ALARM_INFO set period=%d, hourOfDay=%d, minute=%d where no=%d; ";
        try {
            String updateQuery = String.format(
                    queryTemplate,
                    alarm.getPeriod().toInt(),
                    alarm.getHourOfDay(),
                    alarm.getMinute(),
                    alarm.getNo());
            database.execSQL(updateQuery);
        } catch (Exception e) {
            Log.e(tag, "Failed to update alarm info. ");
            Log.e(tag, "Message: " + e.getMessage());
        }
    }



    public void deleteDrug(int drugNo) {
        String deleteQuery = String.format("delete from DRUG_INFO where no = %d;", drugNo);
        try {
            database.execSQL(deleteQuery);
            Log.d(tag, "Success to delete drug. no is " + drugNo);
        } catch (Exception e) {
            Log.e(tag, "Failed to delete drug info. no: " + drugNo);
            Log.e(tag, "Message: " + e.getMessage());
        }
    }

    public void deleteAlarm(int alarmNo) {
        String deleteQuery = String.format("delete from ALARM_INFO where no = %d;", alarmNo);
        try {
            database.execSQL(deleteQuery);
            Log.d(tag, "Success to delete alarm. no is " + alarmNo);
        } catch (Exception e) {
            Log.e(tag, "Failed to delete alarm info. no: " + alarmNo);
            Log.e(tag, "Message: " + e.getMessage());
        }
    }


    public void updateLastChimedTime(int alarmNo, Calendar lastTime) {
        String updateQuery = String.format(
                "update ALARM_INFO set last_alarm_time = '%s' where no = %d;",
                Convert.toStr(lastTime),
                alarmNo);
        try {
            database.execSQL(updateQuery);
            Log.d(tag, "Success to update last alarm time. no is " + alarmNo);
        } catch (Exception e) {
            Log.e(tag, "Failed to update last alarm time. no: " + alarmNo);
            Log.e(tag, "Message: " + e.getMessage());
        }
    }


    public void insertTakeLog(String mediName, boolean isTake) {
        String queryTemplate = "insert into TAKE_LOG values ('%s', '%s', '%s')";
        try {
            String insertQuery = String.format(
                    queryTemplate,
                    Convert.toStr(Calendar.getInstance()),
                    mediName,
                    String.valueOf(isTake));
            database.execSQL(insertQuery);
            Log.d(tag, "Success to insert take log: " + mediName);
        } catch (Exception e) {
            Log.e(tag, "Failed to insert take log: " + mediName);
            Log.e(tag, "Message: " + e.getMessage());
        }
    }


    public Cursor loadTakeLogs() {
        String selectQuery = "select take_time, medi_name, is_take from TAKE_LOG;";
        if(database.isOpen()) {
            return database.rawQuery(selectQuery, null);
        } else {
            Log.e(tag, "The database is not opened.");
            return null;
        }
    }
}
