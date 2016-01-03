package com.plusline.medialarm.ui;

import android.app.Activity;
import android.database.Cursor;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.plusline.medialarm.R;
import com.plusline.medialarm.util.Debug;

import java.util.List;

public class AlarmSoundActivity extends Activity {

    private ListView alarmSoundListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_sound);

        alarmSoundListView = (ListView) findViewById(R.id.alarm_sound_list_view);
        alarmSoundListView.setAdapter(new AlarmSoundListAdapter());



        // 알람음 재생
//        Uri ringtoneUri = RingtoneManager.getActualDefaultRingtoneUri(getApplicationContext(), RingtoneManager.TYPE_ALARM);
//        Ringtone ringtone = RingtoneManager.getRingtone(getApplicationContext(), ringtoneUri);
//        ringtone.play();
    }



    private class AlarmSoundListAdapter extends BaseAdapter {

        public AlarmSoundListAdapter() {
            RingtoneManager rm = new RingtoneManager(AlarmSoundActivity.this);
            rm.setType(RingtoneManager.TYPE_ALARM);
            Cursor cursor = rm.getCursor();
            while (cursor.moveToNext()) {
                Debug.printCursor(cursor);
            }
        }

        @Override
        public int getCount() {
            return 0;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return null;
        }
    }



}
