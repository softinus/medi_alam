package com.plusline.medialarm.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.plusline.medialarm.R;
import com.plusline.medialarm.management.AlarmManager;
import com.plusline.medialarm.management.DrugCaseManager;
import com.plusline.medialarm.management.DrugManager;
import com.plusline.medialarm.management.MainOption;
import com.plusline.medialarm.management.Profile;
import com.plusline.medialarm.management.TakeLogManager;
import com.plusline.medialarm.type.DrugCase;
import com.plusline.medialarm.util.Path;

public class SplashActivity extends Activity {

    private View loadingLayout;
    private EditText passwordEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        loadingLayout = findViewById(R.id.loading_layout);
        passwordEdit = (EditText) findViewById(R.id.password_edit);
        passwordEdit.addTextChangedListener(passwordWatcher);
        passwordEdit.setVisibility(View.GONE);

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                SystemClock.sleep(2000);

                Path.init(getBaseContext());
                MainOption.get().init(getBaseContext());
                Profile.get().init(SplashActivity.this);

                AlarmManager.initialize(getBaseContext());
                DrugManager.initialize(getBaseContext());
                DrugCaseManager.initialize(getBaseContext());
                TakeLogManager.initialize(getBaseContext());

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                if(MainOption.get().checkPasswordOnStart()) {
                    loadingLayout.setVisibility(View.GONE);
                    passwordEdit.setVisibility(View.VISIBLE);
                } else {
                    Intent startMainIntent = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(startMainIntent);
                    finish();
                }
            }
        }.execute();
    }


    private TextWatcher passwordWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence str, int start, int before, int count) {
            if(4 == str.length()) {
                String password = MainOption.get().getPassword();
                if(password.equals(str.toString())) {
                    Intent startMainIntent = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(startMainIntent);
                    finish();
                } else {
                    Toast.makeText(SplashActivity.this, "암호가 틀립니다.", Toast.LENGTH_SHORT).show();
                    passwordEdit.setText("");
                }
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };
}
