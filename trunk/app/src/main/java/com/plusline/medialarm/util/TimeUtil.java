package com.plusline.medialarm.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * 시간과 관련된 유틸리티 함수를 제공한다.
 */
public class TimeUtil {


    private TimeUtil() {

    }

    // 현재 년도와 날짜에서 시/분/초 만 초기화 한다.
    public static Calendar getInitCalendar() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        java.util.Date date = calendar.getTime();

        String Today = new SimpleDateFormat("yyyyMMdd").format(date);
        try {
            calendar.setTime(formatter.parse(Today));
        } catch (ParseException e1) {
            return null;
        }

        return calendar;
    }
}
