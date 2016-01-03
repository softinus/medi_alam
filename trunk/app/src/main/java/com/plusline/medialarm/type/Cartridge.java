package com.plusline.medialarm.type;

import android.database.Cursor;

import com.plusline.medialarm.management.DatabaseHandler;
import com.plusline.medialarm.management.DrugCaseManager;
import com.plusline.medialarm.management.DrugManager;

import java.util.ArrayList;
import java.util.List;

/**
 * 카트리지
 */
public class Cartridge {


    public enum Type {
        Morning(11),
        Lunch(12),
        Evening(13);

        private int value;
        Type(int value) {
            this.value = value;
        }

        public int toInt() {
            return this.value;
        }

        public static Type parse(int typeValue) {
            switch (typeValue) {
                case 11:
                    return Morning;
                case 12:
                    return Lunch;
                case 13:
                    return Evening;
            }
            return null;
        }

        public static String toString(Type type) {
            switch (type) {
                case Morning:
                    return "아침";
                case Lunch:
                    return "점심";
                case Evening:
                    return "저녁";
            }
            return "";
        }
    }


    private Cartridge() {}


    private int no;
    private int dayOfWeek;
    private Type type;


    public int getNo() {
        return no;
    }

    public int getDayOfWeek() {
        return dayOfWeek;
    }

    public Type getType() {
        return type;
    }


    @Override
    public String toString() {
        return String.format("[Cartridge] No: %d, Day: %d, Type: %s", no, dayOfWeek, type);
    }

    public static Cartridge create(Cursor cursor) {
        Cartridge cartridge = new Cartridge();

        cartridge.no = cursor.getInt(cursor.getColumnIndex("no"));
        cartridge.dayOfWeek = cursor.getInt(cursor.getColumnIndex("day"));
        cartridge.type = Type.parse(cursor.getInt(cursor.getColumnIndex("type")));

        return cartridge;
    }


    public List<DrugInfo> getDrugs() {
        List<DrugInfo> drugs = new ArrayList<>();

        DatabaseHandler handler = DrugCaseManager.get().getDatabaseHandler();
        Cursor cursor = handler.loadDrugIdList(no);
        if(null != cursor) {
            while (cursor.moveToNext()) {
                int drugNo = cursor.getInt(cursor.getColumnIndex("drug_no"));
                DrugInfo drugInfo = DrugManager.get().getDrug(drugNo);
                if(null != drugInfo) {
                    drugs.add(drugInfo);
                }
            }
        }

        return drugs;
    }
}
