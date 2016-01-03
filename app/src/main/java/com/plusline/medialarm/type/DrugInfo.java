package com.plusline.medialarm.type;

import android.database.Cursor;

/**
 *
 */
public class DrugInfo {

    //
    //  constructor
    //
    private DrugInfo() {

    }

    public DrugInfo(int drugNo) {
        this.no = drugNo;
    }

    private int no;
    private int alarmNo = -1;
    private String user;
    private String name;
    private int colorIndex = -1;
    private int unit = 0;
    private int mg = 0;
    private int gr = 0;
    //private int drugType;


    public int getNo() {
        return no;
    }

    public int getAlarmNo() {
        return alarmNo;
    }

    public String getUser() {
        return user;
    }

    public String getName() {
        return name;
    }

    public int getColorIndex() {
        return colorIndex;
    }

    public int getUnit() {
        return unit;
    }

    public int getMg() {
        return mg;
    }

    public int getGr() {
        return gr;
    }

    public void setAlarmNo(int alarmNo) {
        this.alarmNo = alarmNo;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setColorIndex(int colorIndex) {
        this.colorIndex = colorIndex;
    }

    public void setUnit(int unit) {
        this.unit = unit;
    }

    public void setMg(int mg) {
        this.mg = mg;
    }

    public void setGr(int gr) {
        this.gr = gr;
    }

    @Override
    public String toString() {
        return String.format("Drug no: %d, name: %s", no, name);
    }


    public static DrugInfo create(Cursor cursor) {
        DrugInfo info = new DrugInfo();
        info.no = cursor.getInt(cursor.getColumnIndex("no"));
        info.alarmNo = cursor.getInt(cursor.getColumnIndex("alarm_no"));
        info.user = cursor.getString(cursor.getColumnIndex("user"));
        info.name = cursor.getString(cursor.getColumnIndex("name"));
        info.colorIndex = cursor.getInt(cursor.getColumnIndex("color"));
        info.unit = cursor.getInt(cursor.getColumnIndex("unit"));
        info.mg = cursor.getInt(cursor.getColumnIndex("mg"));
        info.gr = cursor.getInt(cursor.getColumnIndex("gr"));
        return info;
    }
}
