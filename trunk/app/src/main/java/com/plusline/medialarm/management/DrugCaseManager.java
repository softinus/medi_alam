package com.plusline.medialarm.management;

import android.content.Context;
import android.database.Cursor;

import com.plusline.medialarm.type.DrugCase;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 *  7개의 약통을 관리한다.
 */
public class DrugCaseManager {

    private Map<Integer, DrugCase> drugCases = new HashMap<>();
    DatabaseHandler databaseHandler;

    //
    //  constructor
    //
    private DrugCaseManager() {

    }

    private void init(Context context) {
        databaseHandler = new DatabaseHandler(context);
        loadDrugCases();
    }

    public void loadDrugCases() {
        drugCases.clear();

        for(int i=0; i<7; ++i) {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_MONTH, i);

            int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
            Cursor cartridgeCursor = databaseHandler.loadCartridge(dayOfWeek);
            DrugCase drugCase = DrugCase.create(calendar, cartridgeCursor);
            drugCases.put(calendar.get(Calendar.DAY_OF_MONTH), drugCase);
        }
    }

    public DrugCase getCase(int dayOfMonth) {
        return drugCases.get(dayOfMonth);
    }

    public DatabaseHandler getDatabaseHandler() {
        return databaseHandler;
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    // static methods

    private static DrugCaseManager caseManager = new DrugCaseManager();
    public static DrugCaseManager get() {
        return caseManager;
    }

    public static void initialize(Context context) {
        caseManager.init(context);
    }

}
