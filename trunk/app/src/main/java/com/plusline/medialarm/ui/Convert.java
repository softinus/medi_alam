package com.plusline.medialarm.ui;

import java.text.FieldPosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 *
 *
 */
public class Convert {

    public static String toStr(Calendar date) {
        return toStr(date, "yyyy-MM-dd HH:mm:ss");
    }


    public static String toStr(Calendar date, String format) {
        return toStr(date, format, Locale.KOREA);
    }


    public static String toStr(Calendar date, String format, Locale locale) {
        if(null == date) {
            return "";
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat(format, locale);
        StringBuffer dateBuffer = new StringBuffer();

        dateFormat.format(date.getTime(), dateBuffer, new FieldPosition(0));
        return dateBuffer.toString();
    }


    public static Calendar toCalendar(String value) {
        return toCalendar(value, "yyyy-MM-dd HH:mm:ss");
    }


    public static Calendar toCalendar(String value, String format) {
        try {
            if(null == value || value.isEmpty()) {
                return null;
            }

            SimpleDateFormat simpleFormat = new SimpleDateFormat(format, Locale.KOREA);
            Date ttsDate = simpleFormat.parse(value);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(ttsDate);
            return calendar;
        } catch(Exception e) {
            return null;
        }
    }
}
