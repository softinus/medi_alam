package com.plusline.medialarm.type;

import android.database.Cursor;

import com.plusline.medialarm.ui.Convert;

import java.util.Calendar;

/**
 *
 */
public class TakeLog {

    private TakeLog() {

    }

    private Calendar takeTime;
    private String drugName;
    private boolean isTaken;

    public Calendar getTakeTime() {
        return takeTime;
    }

    public String getDrugName() {
        return drugName;
    }

    public boolean isTaken() {
        return isTaken;
    }

    @Override
    public String toString() {
        return String.format("Take time: %s, result: %s", Convert.toStr(takeTime), isTaken);
    }

    //
    //
    public static TakeLog create(Cursor cursor) {
        TakeLog log = new TakeLog();

        log.takeTime = Convert.toCalendar(cursor.getString(cursor.getColumnIndex("take_time")));
        log.drugName = cursor.getString(cursor.getColumnIndex("medi_name"));
        log.isTaken = Boolean.parseBoolean(cursor.getString(cursor.getColumnIndex("is_take")));

        return log;
    }

}
