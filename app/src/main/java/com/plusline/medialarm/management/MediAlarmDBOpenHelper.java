package com.plusline.medialarm.management;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.plusline.medialarm.type.Cartridge;

import java.util.Calendar;

/**
 * 메디 알람 DB Open Helper
 */
public class MediAlarmDBOpenHelper extends SQLiteOpenHelper {
    private static final String tag = "MediAlarmDBOpenHelper";
    private static final int DB_VERSION = 2;
    private static final String DB_NAME = "medialarm.database";

    //
    //
    //
    public MediAlarmDBOpenHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            // CARTRIDGE
            db.execSQL("create table CARTRIDGE ( " +
                    " no integer, " +
                    " day integer, " +              // 일(1), 월, 화, 수, 목, 금, 토(7)
                    " type integer, " +             // 아침, 점심, 저녁
                    " PRIMARY KEY(no)); ");

            // DRUG_INFO
            db.execSQL("create table DRUG_INFO ( " +
                    " no integer, " +
                    " user text, " +
                    " name text, " +
                    " color integer, " +
                    " mount integer, " +
                    " unit integer, " +
                    " mg integer, " +
                    " gr integer, " +
                    " alarm_no integer, " +
                    " PRIMARY KEY(no));");

            // ALARM_INFO
            db.execSQL("create table ALARM_INFO ( " +
                    " no integer, " +
                    " drug_no integer, " +
                    " period integer, " +           // 알람유형: 1회, 매일, 주기적으로
                    " hourOfDay integer, " +
                    " minute integer, " +
                    " last_alarm_time text, " +
                    " PRIMARY KEY(no) );");

            // CARTRIDGE_DRUG_MAP
            db.execSQL("create table CARTRIDGE_DRUG_MAP ( " +
                    " cart_no integer, " +
                    " drug_no integer);");

            // TAKE_LOG
            db.execSQL("create table TAKE_LOG ( " +
                    "take_time text, " +
                    "medi_name text, " +
                    "is_take boolean);");


        } catch (Exception e) {
            Log.e(tag, "Failed to create table. " + e.getMessage());
            return;
        }

        insertCartridgeData(db);
    }

    //
    //  Summary:
    //      DayOfWeek: 1(SUN) ~ 7(SAT)
    //      아침(11), 점심(12), 저녁(13)
    //
    //
    private void insertCartridgeData(SQLiteDatabase db) {
        int cartNo = -1;
        insertCartridgeRecord(db, ++cartNo, Calendar.SUNDAY, Cartridge.Type.Morning.toInt());
        insertCartridgeRecord(db, ++cartNo, Calendar.SUNDAY, Cartridge.Type.Lunch.toInt());
        insertCartridgeRecord(db, ++cartNo, Calendar.SUNDAY, Cartridge.Type.Evening.toInt());

        insertCartridgeRecord(db, ++cartNo, Calendar.MONDAY, Cartridge.Type.Morning.toInt());
        insertCartridgeRecord(db, ++cartNo, Calendar.MONDAY, Cartridge.Type.Lunch.toInt());
        insertCartridgeRecord(db, ++cartNo, Calendar.MONDAY, Cartridge.Type.Evening.toInt());

        insertCartridgeRecord(db, ++cartNo, Calendar.TUESDAY, Cartridge.Type.Morning.toInt());
        insertCartridgeRecord(db, ++cartNo, Calendar.TUESDAY, Cartridge.Type.Lunch.toInt());
        insertCartridgeRecord(db, ++cartNo, Calendar.TUESDAY, Cartridge.Type.Evening.toInt());

        insertCartridgeRecord(db, ++cartNo, Calendar.WEDNESDAY, Cartridge.Type.Morning.toInt());
        insertCartridgeRecord(db, ++cartNo, Calendar.WEDNESDAY, Cartridge.Type.Lunch.toInt());
        insertCartridgeRecord(db, ++cartNo, Calendar.WEDNESDAY, Cartridge.Type.Evening.toInt());

        insertCartridgeRecord(db, ++cartNo, Calendar.THURSDAY, Cartridge.Type.Morning.toInt());
        insertCartridgeRecord(db, ++cartNo, Calendar.THURSDAY, Cartridge.Type.Lunch.toInt());
        insertCartridgeRecord(db, ++cartNo, Calendar.THURSDAY, Cartridge.Type.Evening.toInt());

        insertCartridgeRecord(db, ++cartNo, Calendar.FRIDAY, Cartridge.Type.Morning.toInt());
        insertCartridgeRecord(db, ++cartNo, Calendar.FRIDAY, Cartridge.Type.Lunch.toInt());
        insertCartridgeRecord(db, ++cartNo, Calendar.FRIDAY, Cartridge.Type.Evening.toInt());

        insertCartridgeRecord(db, ++cartNo, Calendar.SATURDAY, Cartridge.Type.Morning.toInt());
        insertCartridgeRecord(db, ++cartNo, Calendar.SATURDAY, Cartridge.Type.Lunch.toInt());
        insertCartridgeRecord(db, ++cartNo, Calendar.SATURDAY, Cartridge.Type.Evening.toInt());
    }


    private void insertCartridgeRecord(SQLiteDatabase db, int no, int dayOfWeek, int type) {
        final String queryTemplate = "insert into CARTRIDGE values(%d, %d, %d)";
        try {
            String insertQuery = String.format(queryTemplate, no, dayOfWeek, type);
            db.execSQL(insertQuery);
        } catch (Exception e) {
            Log.e(tag, "Failed to insert default data. " + e.getMessage());
        }
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
