package com.plusline.medialarm.ui;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.plusline.medialarm.R;
import com.plusline.medialarm.management.MainOption;

public class PasswordActivity extends Activity {

    private String password;
    private TextView guideText;
    private EditText passwordEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);

        guideText = (TextView) findViewById(R.id.guide_text);
        passwordEdit = (EditText) findViewById(R.id.password_edit);
        passwordEdit.addTextChangedListener(passwordWatcher);

        InputMethodManager mInputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mInputMethodManager.showSoftInput(passwordEdit, InputMethodManager.SHOW_IMPLICIT);

        init();
        setOnClickListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();

        InputMethodManager mInputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mInputMethodManager.showSoftInput(passwordEdit, InputMethodManager.SHOW_IMPLICIT);
    }

    private void setOnClickListeners() {
        findViewById(R.id.back_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void init() {
        password = "";
        guideText.setText("암호 입력");
        passwordEdit.setText("");
    }

    private TextWatcher passwordWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence str, int start, int before, int count) {
            if(4 == str.length()) {
                if(password.isEmpty()) {
                    guideText.setText("암호 확인");
                    password = str.toString();
                    passwordEdit.setText("");
                } else {
                    if(!password.equals(str.toString())) {
                        init();
                        Toast.makeText(PasswordActivity.this, "입력된 암호가 다릅니다.", Toast.LENGTH_SHORT).show();
                    } else {
                        MainOption.get().setPassword(password);
                        Toast.makeText(PasswordActivity.this, "암호가 설정 되었습니다.", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };
}
