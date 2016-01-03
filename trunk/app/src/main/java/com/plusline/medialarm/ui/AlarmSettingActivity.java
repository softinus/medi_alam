package com.plusline.medialarm.ui;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.plusline.medialarm.R;
import com.plusline.medialarm.management.AlarmManager;
import com.plusline.medialarm.management.SelectedDrugCase;
import com.plusline.medialarm.type.Alarm;
import com.plusline.medialarm.type.AlarmPeriod;
import com.plusline.medialarm.type.DrugInfo;

public class AlarmSettingActivity extends Activity {
    private static final String tag = "AlarmSettingActivity";

    private SwitchCompat alarmSwitch;
    private View periodLayout;
    private View alarmTimeLayout;
    private TextView periodText;
    private TextView selectedTimeText;

    private AlarmPeriod[] displayPeriod = new AlarmPeriod[] {
            AlarmPeriod.Once, AlarmPeriod.EveryDay, AlarmPeriod.Repeat
    };

    private int drugNo;
    private int currentPeriod = -1;
    private Pair<Integer, Integer> alarmTime = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_setting);

        drugNo = getIntent().getIntExtra("drugNo", -1);
        if(-1 == drugNo) {
            Log.e(tag, "The drug number is not setting.");
        }
        alarmSwitch = (SwitchCompat) findViewById(R.id.alarm_switch);

        periodLayout = findViewById(R.id.period_layout);
        periodLayout.setVisibility(View.INVISIBLE);
        alarmTimeLayout = findViewById(R.id.alarm_time_layout);
        alarmTimeLayout.setVisibility(View.INVISIBLE);

        periodText = (TextView) findViewById(R.id.period_text);
        periodText.setText("클릭하여 주기를 설정하세요");
        selectedTimeText = (TextView) findViewById(R.id.selected_time_text);
        selectedTimeText.setText("클릭하여 알람 시간을 설정하세요");

        if(SelectedDrugCase.get().isModify()) {
            initWithExistedAlarm();
            ((TextView)findViewById(R.id.save_alarm_text)).setText("수정");
        }
        setOnClickListeners();
    }

    private void initWithExistedAlarm() {
        Alarm existedAlarm = getExistedAlarm();
        if(null == existedAlarm) {
            return;
        }

        alarmSwitch.setChecked(true);
        periodLayout.setVisibility(View.VISIBLE);
        initCurrentPeriod(existedAlarm.getPeriod());
        periodText.setText(AlarmPeriod.toString(existedAlarm.getPeriod()));
        alarmTimeLayout.setVisibility(View.VISIBLE);
        initDisplaySelectedTime(existedAlarm.getPeriod(), existedAlarm.getHourOfDay(), existedAlarm.getMinute());
    }

    private void initCurrentPeriod(AlarmPeriod period) {
        switch (period) {
            case Once:
                currentPeriod = 0;
            case EveryDay:
                currentPeriod = 1;
            case Repeat:
                currentPeriod = 2;
        }
    }

    private Alarm getExistedAlarm() {
        DrugInfo drugInfo = SelectedDrugCase.get().getCurrentDrugInfo();
        return AlarmManager.get().getAlarm(drugInfo.getAlarmNo());
    }

    private void setOnClickListeners() {
        findViewById(R.id.back_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        findViewById(R.id.save_alarm_text).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveAlarm();
            }
        });

        alarmSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    periodLayout.setVisibility(View.VISIBLE);
                } else {
                    periodLayout.setVisibility(View.INVISIBLE);
                    alarmTimeLayout.setVisibility(View.INVISIBLE);

                    currentPeriod = -1;
                    alarmTime = null;
                    periodText.setText("클릭하여 주기를 설정하세요");
                    selectedTimeText.setText("클릭하여 알람 시간을 설정하세요");
                }
            }
        });

        findViewById(R.id.set_period_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayAlarmPeriod();
                alarmTimeLayout.setVisibility(View.VISIBLE);
            }
        });

        findViewById(R.id.select_time_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog dialog = new TimePickerDialog(AlarmSettingActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        displaySelectedTime(hourOfDay, minute);
                    }
                }, 0, 0, false);
                dialog.show();
            }
        });
    }

    private void displayAlarmPeriod() {
        ++currentPeriod;
        if(currentPeriod == displayPeriod.length) {
            currentPeriod = 0;
        }
        periodText.setText(AlarmPeriod.toString(displayPeriod[currentPeriod]));
    }

    private void displaySelectedTime(int hourOfDay, int minute) {
        AlarmPeriod period = displayPeriod[currentPeriod];
        initDisplaySelectedTime(period, hourOfDay, minute);
    }

    private void initDisplaySelectedTime(AlarmPeriod period, int hourOfDay, int minute) {
        String displayString = "";
        switch (period) {
            case Once:
            case EveryDay:
                displayString = String.format("%s %d:%02d", (12 < hourOfDay) ? "오후" : "오전", hourOfDay % 12, minute);
                break;
            case Repeat:
                displayString = String.format("%d시간 %d분 후에 알림", hourOfDay%12, minute);
                break;
        }
        selectedTimeText.setText(displayString);
        alarmTime = Pair.create(hourOfDay, minute);
    }


    private void saveAlarm() {
        if(-1 == currentPeriod) {
            Toast.makeText(this, "먼저 알람 주기를 선택하세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        if(null == alarmTime) {
            Toast.makeText(this, "알람 시간을 지정해 주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        if(SelectedDrugCase.get().isModify()) {
            Alarm currentAlarm = SelectedDrugCase.get().getCurrentAlarm();
            currentAlarm.update(displayPeriod[currentPeriod], alarmTime.first, alarmTime.second);
            AlarmManager.get().addAlarm(currentAlarm);
            SelectedDrugCase.get().getCurrentDrugInfo().setAlarmNo(currentAlarm.getNo());
        } else {
            Alarm currentAlarm = Alarm.crete(drugNo, displayPeriod[currentPeriod], alarmTime.first, alarmTime.second);
            AlarmManager.get().addAlarm(currentAlarm);
            SelectedDrugCase.get().getCurrentDrugInfo().setAlarmNo(currentAlarm.getNo());
        }

        Toast.makeText(this, "저장되었습니다", Toast.LENGTH_SHORT).show();
        finish();
    }
}
