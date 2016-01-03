package com.plusline.medialarm.type;

/**
 * 알람 설정 주기
 */
public enum AlarmPeriod {
    Once(21),           // 1회
    EveryDay(22),       // 매일
    Repeat(23);         // 지정된 시간마다

    private int value;
    AlarmPeriod(int value) {
        this.value = value;
    }

    public int toInt() {
        return this.value;
    }

    public static AlarmPeriod parse(int value) {
        switch (value) {
            case 21:
                return Once;
            case 22:
                return EveryDay;
            case 23:
                return Repeat;
        }
        return null;
    }

    public static String toString(AlarmPeriod period) {
        switch (period) {
            case Once:
                return "1회 복용";
            case EveryDay:
                return "매일 복용";
            case Repeat:
                return "지정된 시간 후에 알림";
        }
        return "";
    }


}
