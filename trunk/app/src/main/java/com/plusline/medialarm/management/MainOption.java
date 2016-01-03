package com.plusline.medialarm.management;


import android.content.Context;
import android.media.RingtoneManager;
import android.net.Uri;

/**
 * Delivery Knock Main Option class
 *
 */
public class MainOption extends Option {

    private static final String OPTION_NAME = "MediAlarmMainOptions";

    //
    //  constructor
    //
    private MainOption() {

    }


    public void init(Context context) {
        super.init(context, OPTION_NAME);

    }


    private static final String IS_SIMPLE_MODE = "isSimpleMode";
    private static final String IS_USE_PASSWORD = "isUsePassword";
    private static final String INTRO_PASSWORD = "introPassword";
    private static final String ALARM_MESSAGE = "alarmMessage";
    private static final String ALARM_SOUND = "alarmSound";

    //
    // 단순 모드
    public boolean isSimpleMode() {
        return getBoolean(IS_SIMPLE_MODE, false);
    }

    public void setSimpleMode(boolean simpleMode) {
        setBooleanValue(IS_SIMPLE_MODE, simpleMode);
        dispatchChangedMessage();
    }

    public boolean checkPasswordOnStart() {
        return isUsePassword() && !getPassword().isEmpty();
    }

    //
    //  암호사용
    public boolean isUsePassword() {
        return getBoolean(IS_USE_PASSWORD, false);
    }

    public void setUsePassword(boolean usePassword) {
        setBooleanValue(IS_USE_PASSWORD, usePassword);
    }

    //
    // 패스워드
    public String getPassword() {
        return getString(INTRO_PASSWORD, "");
    }

    public void setPassword(String password) {
        setStringValue(INTRO_PASSWORD, password);
    }


    //
    // 알람 알림 메시지
    public String getAlarmMessage() {
        return getString(ALARM_MESSAGE, "약을 복용할 시간입니다.");
    }

    public void setAlarmMessage(String message) {
        setStringValue(ALARM_MESSAGE, message);
    }

    //
    // 알람 음
    public Uri getAlarmSound() {
        String alarmSound = getString(ALARM_SOUND, "");
        if(!alarmSound.isEmpty()) {
            return Uri.parse(alarmSound);
        } else {
            return RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        }
    }

    public void setAlarmSound(Uri alarmSound) {
        setStringValue(ALARM_SOUND, alarmSound.toString());
    }





   ///////////////////////////////////////////////////////////////////////////////////////////////
    // static variables & methods

    private static MainOption option = new MainOption();
    public static MainOption get() {
        return option;
    }
}
