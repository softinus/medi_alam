package com.plusline.medialarm.management;

import android.content.Context;
import android.database.Cursor;

import com.plusline.medialarm.type.TakeLog;

import java.util.ArrayList;
import java.util.List;

/**
 *  복용 로그를 기록/관리한다.
 */
public class TakeLogManager {

    private DatabaseHandler databaseHandler;

    //
    //
    //
    private TakeLogManager() {
    }

    private void init(Context context) {
        databaseHandler = new DatabaseHandler(context);
    }


    public void insertLog(String mediName, boolean isTake) {
        databaseHandler.insertTakeLog(mediName, isTake);
    }

    public List<TakeLog> loadAllLog() {
        List<TakeLog> logs = new ArrayList<>();

        Cursor cursor = databaseHandler.loadTakeLogs();
        if(null == cursor) {
            return logs;
        }

        while(cursor.moveToNext()) {
            TakeLog log = TakeLog.create(cursor);
            logs.add(log);
        }
        cursor.close();

        return logs;
    }






    ////////////////////////////////////////////////////////////////////////////////////////////////
    //

    private static TakeLogManager manager = new TakeLogManager();
    public static TakeLogManager get() {
        return manager;
    }

    public static void initialize(Context context) {
        manager.init(context);
    }
}
