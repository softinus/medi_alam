package com.plusline.medialarm.util;

import android.database.Cursor;
import android.util.Log;

import com.plusline.medialarm.ui.Convert;

import java.util.Calendar;

/**
 *
 *
 */
public class Debug {
    private static final String tag = "Debug";


    private Debug() {

    }

    public static void printCursor(Cursor cursor) {
        for(int i = 0; i < cursor.getColumnCount(); ++i) {
            String name = cursor.getColumnName(i);
            switch(cursor.getType(i)) {
                case Cursor.FIELD_TYPE_STRING:
                    Log.d(tag, name + ": " + cursor.getString(i));
                    break;
                case Cursor.FIELD_TYPE_INTEGER:
                    Log.d(tag, name + ": " + cursor.getInt(i));
                    break;
                case Cursor.FIELD_TYPE_FLOAT:
                    Log.d(tag, name + ": " + cursor.getFloat(i));
                    break;
                default:
                    Log.d(tag, name + ": ");
                    break;
            }
        }
    }


    public static void printCalendar(Calendar calendar) {
        Log.d(tag, "Calendar: " + Convert.toStr(calendar));
    }
}
