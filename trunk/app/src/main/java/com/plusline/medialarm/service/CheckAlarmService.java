package com.plusline.medialarm.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import com.plusline.medialarm.R;
import com.plusline.medialarm.management.AlarmManager;
import com.plusline.medialarm.management.DrugManager;
import com.plusline.medialarm.management.MainOption;
import com.plusline.medialarm.management.PassedAlarmManager;
import com.plusline.medialarm.type.Alarm;
import com.plusline.medialarm.type.AlarmPeriod;
import com.plusline.medialarm.type.DrugInfo;
import com.plusline.medialarm.ui.Convert;
import com.plusline.medialarm.ui.MainActivity;

import java.net.BindException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * 30초 마다 알람 리스트를 체크한다.
 */
public class CheckAlarmService extends Service {
    private static final String tag = "CheckAlarmService";
    private static final int CHECK_ALARM_INTERVAL = 5000;  // 5초
    private static final int CHECK_UNIT_TIME = 500;

    private ChimeAlarmHandler chimeAlarmHandler;
    private boolean continueCheck = true;

    private Set<OnAlarmListener> alarmListeners = new HashSet<>();

    //
    //
    //
    public CheckAlarmService() {
    }


    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(tag, ">>>>>>>>>>>>>>>>>>>>>>>>>>> CheckAlarmService::onCreate()");

        chimeAlarmHandler = new ChimeAlarmHandler(this);
        removePassedAlarm();
        new CheckTimeThread().start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(tag, ">>>>>>>>>>>>>>>>>>>>>>>>>>> CheckAlarmService::onDestroy()");
        continueCheck = false;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }


    public void addOnAlarmListener(OnAlarmListener listener) {
        alarmListeners.add(listener);
    }

    public void removeAlarmListener(OnAlarmListener listener) {
        alarmListeners.remove(listener);
    }


    private void removePassedAlarm() {
        List<Integer> toRemoveAlarm = new ArrayList<>();
        List<Alarm> alarms = AlarmManager.get().getAlarms();

        Calendar currentTime = Calendar.getInstance();
        for(Alarm alarm : alarms) {
            if(alarm.getPeriod() == AlarmPeriod.Repeat) {
                continue;
            }
            // 현재시간 보다 이전 일회성 알람은 모두 삭제한다.
            if(currentTime.after(alarm.getNextChimeTime())) {
                Log.d(tag, "This alarm is passed. " + alarm + ", " + Convert.toStr(alarm.getLastChimedTime()));
                toRemoveAlarm.add(alarm.getNo());
            }
        }

        for(int alarmNo : toRemoveAlarm) {
            AlarmManager.get().removeAlarm(alarmNo);
        }
    }


    public void handleMessage(Message msg) {
        int alarmNo = msg.what;
        Alarm chimeAlarm = AlarmManager.get().getAlarm(alarmNo);
        DrugInfo drug = DrugManager.get().getDrug(chimeAlarm.getDrugNo());
        PassedAlarmManager.get().pushDrug(drug);

        sendNotification();
        for(OnAlarmListener listener : alarmListeners) {
            listener.onChime(chimeAlarm);
        }

        // 만료된 알람에 대해 1회용 알람은 삭제하고, 반복 알람은 마지막 알람 시간을 업데이트 한다.
        AlarmPeriod alarmPeriod = chimeAlarm.getPeriod();
        switch (alarmPeriod) {
            case Once:
            case Repeat:
                AlarmManager.get().removeAlarm(chimeAlarm.getNo());
                break;
            case EveryDay:
                chimeAlarm.updateLastChimedTimeByCurrentTime();
        }
    }

    public class CASBinder extends Binder {
        public CheckAlarmService getService() {
            return CheckAlarmService.this;
        }
    }
    private CASBinder serviceBinder = new CASBinder();

    @Override
    public IBinder onBind(Intent intent) {
        return serviceBinder;
    }

    private void sendNotification() {
        NotificationManager nm = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

        PendingIntent intent = PendingIntent.getActivity(
                this, 0,
                new Intent(this, MainActivity.class),
                0);

        Notification notification =
                new Notification(R.drawable.medialarm_icon, "Alarm", System.currentTimeMillis());

        Uri pickedAlarm = MainOption.get().getAlarmSound();
        RingtoneManager ringtoneManager = new RingtoneManager(this);
        Ringtone alarmRingtone = ringtoneManager.getRingtone(this, pickedAlarm);
        alarmRingtone.play();

        notification.flags |= Notification.DEFAULT_VIBRATE;
        notification.flags |= Notification.DEFAULT_SOUND;
        notification.flags |= Notification.DEFAULT_LIGHTS;
        notification.flags |= Notification.FLAG_AUTO_CANCEL;

        notification.setLatestEventInfo(this,
                "MediAlarm", MainOption.get().getAlarmMessage(), intent);

        nm.notify(9214, notification);
    }


    //
    //  CheckTimeThread: 백그라운드에서 실행되며 현재 울릴 알람이 있는지를 체크한다.
    //
    private class CheckTimeThread extends Thread {
        @Override
        public void run() {
            int currentCheckTime = 0;
            while (continueCheck) {
                if(CHECK_ALARM_INTERVAL <= currentCheckTime) {
                    //Log.d(tag, "Check all alarm is available..... ^^");
                    Calendar currentTime = Calendar.getInstance();

                    List<Alarm> alarms = AlarmManager.get().getAlarms();
                    for(Alarm alarm : alarms) {
                        //Log.d(tag, "Check alarm time: " + alarm + " " + Convert.toStr(alarm.getNextChimeTime()));
                        if(currentTime.after(alarm.getNextChimeTime())) {
                            //Log.d(tag, "alarming.... )_U)(_(+_+PO}P}_{_+POK:LLKHKHIUYYT^%#$%^&*()_)(&HGBG%^H&J");
                            chimeAlarmHandler.sendEmptyMessage(alarm.getNo());
                        }
                    }
                    currentCheckTime = 0;
                }

                SystemClock.sleep(CHECK_UNIT_TIME);
                currentCheckTime += CHECK_UNIT_TIME;
            }
        }
    }


    private static class ChimeAlarmHandler extends Handler {
        private final CheckAlarmService service;

        public ChimeAlarmHandler(CheckAlarmService svc) {
            this.service = svc;
        }

        @Override
        public void handleMessage(Message msg) {
            service.handleMessage(msg);
        }
    }

}
