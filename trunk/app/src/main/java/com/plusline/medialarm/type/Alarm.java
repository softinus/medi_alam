package com.plusline.medialarm.type;

import android.database.Cursor;
import android.util.Log;

import com.plusline.medialarm.management.AlarmManager;
import com.plusline.medialarm.ui.Convert;
import com.plusline.medialarm.util.TimeUtil;

import java.util.Calendar;

/**
 *
 */
public class Alarm {
    private static final String tag = "Alarm";

    //
    //
    //
    public Alarm() {
        no = AlarmManager.get().getNextNo();
    }

    private int no;
    private int drugNo;
    private AlarmPeriod period;
    private int hourOfDay;
    private int minute;
    private Calendar lastChimedTime;


    public int getNo() {
        return no;
    }

    public int getDrugNo() {
        return drugNo;
    }

    public AlarmPeriod getPeriod() {
        return period;
    }

    public int getHourOfDay() {
        return hourOfDay;
    }

    public int getMinute() {
        return minute;
    }

    public Calendar getLastChimedTime() {
        return lastChimedTime;
    }

    public Calendar getNextChimeTime() {
        Calendar nextChimedTime = TimeUtil.getInitCalendar();

        switch (period) {
            case Once:
                return lastChimedTime;
            case EveryDay:
                return getNextDailyTime(hourOfDay, minute);
            case Repeat:
                return getNextRepeatTime(hourOfDay, minute);
        }

        return nextChimedTime;
    }

    @Override
    public String toString() {
        return String.format("No: %d, Type: %s, Prev: %s, Next: ",
                no,
                AlarmPeriod.toString(period),
                Convert.toStr(lastChimedTime, "MM/dd HH:mm:"));
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof Alarm) {
            Alarm other = (Alarm) o;
            return no == other.no;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return no;
    }


    public void updateLastChimedTimeByCurrentTime() {
        lastChimedTime = Calendar.getInstance();
        AlarmManager.get().updateLastChimedTime(this);
    }


    // 매일 1회로 설정된 알람의 다음 알람 시간을 계산하여 반환한다.
    private Calendar getNextDailyTime(int hourOfDay, int minute) {
        Calendar currentTime = Calendar.getInstance();
        Calendar alarmTime = TimeUtil.getInitCalendar();
        alarmTime.set(Calendar.HOUR, hourOfDay);
        alarmTime.set(Calendar.MINUTE, minute);

        if(alarmTime.before(currentTime)) {
            alarmTime.add(Calendar.DAY_OF_MONTH, 1);
            return alarmTime;
        } else {
            return alarmTime;
        }
    }

    // 지정된 시간주기로 설정된 알람의 다음 알람 시간을 계산하여 반환한다.
    private Calendar getNextRepeatTime(int hourOfDay, int minute) {
        //Log.d(tag, "Repeat Last Alarm Time: " + Convert.toStr(lastChimedTime));
        Calendar alarmTime = Calendar.getInstance();
        alarmTime.set(
                lastChimedTime.get(Calendar.YEAR), lastChimedTime.get(Calendar.MONTH), lastChimedTime.get(Calendar.DAY_OF_MONTH),
                lastChimedTime.get(Calendar.HOUR_OF_DAY), lastChimedTime.get(Calendar.MINUTE), 0);
        alarmTime.add(Calendar.HOUR_OF_DAY, hourOfDay);
        alarmTime.add(Calendar.MINUTE, minute);

        //Log.d(tag, "Repeat Next Alarm Time: " + Convert.toStr(alarmTime));
        return alarmTime;
    }

    public void update(AlarmPeriod period, int hourOfDay, int minute) {
        this.period = period;
        this.hourOfDay = hourOfDay;
        this.minute = minute;

        if(AlarmPeriod.Once == period) {
            // 알람주기가 1회인 경우에는 lastChimedTime 을 다음 알람 시간으로 세팅한다.
            this.lastChimedTime = getNextDailyTime(hourOfDay, minute);
        } else {
            // 반복되는 알람의 경우에는 마지막 알람 시간을 저장하여 다음 알람 시간을 계산한다.
            this.lastChimedTime = Calendar.getInstance();
        }
    }


    public static Alarm crete(int drugNo, AlarmPeriod period, int hourOfDay, int minute) {
        Alarm alarm = new Alarm();
        alarm.drugNo = drugNo;
        alarm.period = period;
        alarm.hourOfDay = hourOfDay;
        alarm.minute = minute;

        if(AlarmPeriod.Once == period) {
            // 알람주기가 1회인 경우에는 lastChimedTime 을 다음 알람 시간으로 세팅한다.
            alarm.lastChimedTime = alarm.getNextDailyTime(hourOfDay, minute);
        } else {
            // 반복되는 알람의 경우에는 마지막 알람 시간을 저장하여 다음 알람 시간을 계산한다.
            alarm.lastChimedTime = Calendar.getInstance();
        }

        return alarm;
    }


    public static Alarm create(Cursor cursor) {
        Alarm alarm = new Alarm();
        alarm.no = cursor.getInt(cursor.getColumnIndex("no"));
        alarm.drugNo = cursor.getInt(cursor.getColumnIndex("drug_no"));
        alarm.period = AlarmPeriod.parse(cursor.getInt(cursor.getColumnIndex("period")));
        alarm.hourOfDay = cursor.getInt(cursor.getColumnIndex("hourOfDay"));
        alarm.minute = cursor.getInt(cursor.getColumnIndex("minute"));
        alarm.lastChimedTime = Convert.toCalendar(cursor.getString(cursor.getColumnIndex("last_alarm_time")));
        return alarm;
    }
}
