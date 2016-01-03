package com.plusline.medialarm.ui;

import android.app.Activity;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.plusline.medialarm.R;
import com.plusline.medialarm.management.MainOption;

/**
 * 일반설정 액티비티
 */
public class PreferenceActivity extends Activity {

    private static final int SHOW_SELECT_ALARM_SOUND = 4990;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preference);

        updateOptionValues();
        setOnClickListeners();
    }

    private void setOnClickListeners() {
        findViewById(R.id.back_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // 알람 알림 문자
        findViewById(R.id.setting_alarm_text).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent messageIntent = new Intent(PreferenceActivity.this, AlarmMessageActivity.class);
                startActivity(messageIntent);
            }
        });

        // 알림음
        findViewById(R.id.setting_alarm_sound).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectAlarmSound();
            }
        });

        // 단순모드
        findViewById(R.id.setting_simple_mode).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isSimpleMode = MainOption.get().isSimpleMode();
                MainOption.get().setSimpleMode(!isSimpleMode);
                updateOptionValues();

            }
        });

        // 암호사용
        SwitchCompat usePasswordSwitch = (SwitchCompat) findViewById(R.id.password_switch);
        usePasswordSwitch.setChecked(MainOption.get().isUsePassword());
        usePasswordSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                MainOption.get().setUsePassword(isChecked);
                if (isChecked) {
                    Intent passwordIntent = new Intent(PreferenceActivity.this, PasswordActivity.class);
                    startActivity(passwordIntent);
                } else {
                    MainOption.get().setPassword("");
                }
            }
        });

        // 소개
        findViewById(R.id.setting_view_introduce).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent introduce = new Intent(PreferenceActivity.this, AboutActivity.class);
                startActivity(introduce);
            }
        });

    }


    private void updateOptionValues() {
        TextView simpleModeText = (TextView) findViewById(R.id.simple_mode_text);
        if(MainOption.get().isSimpleMode()) {
            simpleModeText.setText("on");
            simpleModeText.setTextColor(0xff7db343);
        } else {
            simpleModeText.setText("off");
            simpleModeText.setTextColor(0xffb1b1b1);
        }
    }


    private void selectAlarmSound() {
        Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "알림음");
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_NOTIFICATION);

        startActivityForResult(intent, SHOW_SELECT_ALARM_SOUND);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(RESULT_OK != resultCode) {
            return;
        }

        if(SHOW_SELECT_ALARM_SOUND == requestCode) {
            Uri pickedAlarm = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
            if(null == pickedAlarm) {
                Toast.makeText(this, "알림음이 선택되지 않았습니다", Toast.LENGTH_SHORT).show();
                return;
            }

            Log.d("Preference", "Selected alarm sound: " + pickedAlarm.toString());
            MainOption.get().setAlarmSound(pickedAlarm);


//            RingtoneManager ringtoneManager = new RingtoneManager(this);
//            Ringtone alarmRingtone = ringtoneManager.getRingtone(this, pickedAlarm);
//            alarmRingtone.play();
        }
    }
}
