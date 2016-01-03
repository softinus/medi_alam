package com.plusline.medialarm.ui;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.plusline.medialarm.R;
import com.plusline.medialarm.management.MainOption;

public class AlarmMessageActivity extends Activity {

    private EditText alarmMessageText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_message);

        alarmMessageText = (EditText) findViewById(R.id.alarm_message_edit);
        alarmMessageText.setText(MainOption.get().getAlarmMessage());

        setOnClickListeners();
    }

    private void setOnClickListeners() {
        findViewById(R.id.back_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onBackPressed() {
        String alarmMessage = alarmMessageText.getText().toString();
        if(!alarmMessage.isEmpty()) {
            MainOption.get().setAlarmMessage(alarmMessage);
        }

        super.onBackPressed();
    }
}
