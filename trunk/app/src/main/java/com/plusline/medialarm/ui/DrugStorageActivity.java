package com.plusline.medialarm.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.plusline.medialarm.R;

public class DrugStorageActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drug_storage);

        setOnClickListeners();
    }

    private void setOnClickListeners() {
        findViewById(R.id.add_drag_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addDrug = new Intent(DrugStorageActivity.this, SelectCartridgeActivity.class);
                startActivity(addDrug);
            }
        });
    }
}
