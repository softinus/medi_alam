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
import android.widget.Toast;

import com.plusline.medialarm.R;
import com.plusline.medialarm.management.SelectedDrugCase;
import com.plusline.medialarm.type.DrugInfo;

import java.util.ArrayList;
import java.util.List;

public class DrugInfoActivity extends Activity {

    private static final int SELECTED_BACK_COLOR = 0xffd1d1d1;
    private static final int NORMAL_BACK_COLOR = 0xfff5f5f5;

    private View prevSelectedColor = null;
    private List<View> selectColorButtons = new ArrayList<>();

    private EditText unitEdit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drug_info);

        selectColorButtons.add(findViewById(R.id.drug_color_1));
        selectColorButtons.add(findViewById(R.id.drug_color_2));
        selectColorButtons.add(findViewById(R.id.drug_color_3));
        selectColorButtons.add(findViewById(R.id.drug_color_4));
        selectColorButtons.add(findViewById(R.id.drug_color_5));
        selectColorButtons.add(findViewById(R.id.drug_color_6));


        unitEdit = (EditText) findViewById(R.id.unit_edit);
        EditText mgEdit = (EditText) findViewById(R.id.mg_edit);
        EditText grEdit = (EditText) findViewById(R.id.gr_edit);

        DrugInfo currentDrugInfo = SelectedDrugCase.get().getCurrentDrugInfo();
        if(SelectedDrugCase.get().isModify()) {
            selectColorButtons.get(currentDrugInfo.getColorIndex()).setBackgroundColor(SELECTED_BACK_COLOR);
            unitEdit.setText(String.valueOf(currentDrugInfo.getUnit()));
            mgEdit.setText(String.valueOf(currentDrugInfo.getMg()));
            grEdit.setText(String.valueOf(currentDrugInfo.getGr()));
        } else {
            unitEdit.setText("1");
            mgEdit.setText("0");
            grEdit.setText("0");
        }

        unitEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String number = s.toString();
                int unit = number.isEmpty() ? 0 : Integer.parseInt(number);
                if(unit < 1 ||4 < unit) {
                    unitEdit.setText("1");
                    Toast.makeText(DrugInfoActivity.this, "입력범위: 1 ~ 4", Toast.LENGTH_SHORT).show();
                } else {
                    DrugInfo currentDrug = SelectedDrugCase.get().getCurrentDrugInfo();
                    currentDrug.setUnit(unit);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mgEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String number = s.toString();
                int mg = number.isEmpty() ? 0 : Integer.parseInt(number);
                DrugInfo currentDrug = SelectedDrugCase.get().getCurrentDrugInfo();
                currentDrug.setMg(mg);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        grEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String number = s.toString();
                int gr = number.isEmpty() ? 0 : Integer.parseInt(number);
                DrugInfo currentDrug = SelectedDrugCase.get().getCurrentDrugInfo();
                currentDrug.setGr(gr);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        setOnClickListeners();
    }

    private void setOnClickListeners() {
        findViewById(R.id.back_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        for(View colorButton : selectColorButtons) {
            colorButton.setOnClickListener(new OnClickDrugColorListener());
        }
    }


    private class OnClickDrugColorListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if(null != prevSelectedColor) {
                prevSelectedColor.setBackgroundColor(NORMAL_BACK_COLOR);
            }

            v.setBackgroundColor(SELECTED_BACK_COLOR);
            prevSelectedColor = v;

            DrugInfo currentDrug = SelectedDrugCase.get().getCurrentDrugInfo();
            if(null == currentDrug) {
                return;
            }

            int index = selectColorButtons.indexOf(v);
            currentDrug.setColorIndex(index);
        }
    }
}
