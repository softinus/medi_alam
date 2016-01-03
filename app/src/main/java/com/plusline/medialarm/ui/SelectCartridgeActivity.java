package com.plusline.medialarm.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.plusline.medialarm.R;
import com.plusline.medialarm.management.SelectedDrugCase;
import com.plusline.medialarm.type.Cartridge;

public class SelectCartridgeActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_cartridge);

        String caseDate = Convert.toStr(SelectedDrugCase.get().getSelectedCase().getDayInfo(), "M/d");
        TextView drugCaseInfo = (TextView) findViewById(R.id.select_drug_case_info);
        drugCaseInfo.setText("선택된 약통: " + caseDate);

        setOnClickListeners();
    }


    private void setOnClickListeners() {
        findViewById(R.id.back_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        findViewById(R.id.morning_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectedDrugCase.get().select(Cartridge.Type.Morning);
                showSelectDrugActivity();
            }
        });

        findViewById(R.id.lunch_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectedDrugCase.get().select(Cartridge.Type.Lunch);
                showSelectDrugActivity();
            }
        });

        findViewById(R.id.evening_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectedDrugCase.get().select(Cartridge.Type.Evening);
                showSelectDrugActivity();
            }
        });
    }


    private void showSelectDrugActivity() {
        finish();
        Intent addDrug = new Intent(SelectCartridgeActivity.this, AddDrugActivity.class);
        startActivity(addDrug);
    }
}
