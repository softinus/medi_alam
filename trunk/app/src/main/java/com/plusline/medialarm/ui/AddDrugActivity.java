package com.plusline.medialarm.ui;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.plusline.medialarm.R;
import com.plusline.medialarm.management.AlarmManager;
import com.plusline.medialarm.management.DrugManager;
import com.plusline.medialarm.management.Profile;
import com.plusline.medialarm.management.SelectedDrugCase;
import com.plusline.medialarm.service.BluetoothLeService;
import com.plusline.medialarm.service.MyService;
import com.plusline.medialarm.service.ServiceMain;
import com.plusline.medialarm.type.Alarm;
import com.plusline.medialarm.type.Cartridge;
import com.plusline.medialarm.type.DrugInfo;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddDrugActivity extends Activity implements MediAlarmDevice.BaseView {
    private static final String tag = "AddDrugActivity";

    private int currentDrugNo;

    private TextView userNameText;
    private TextView drugNameText;
    private Button deleteDrugButton;

    private View connectingLayout;

    //
    //
    //  Activity code
    //
    //
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_drug);

        connectingLayout = findViewById(R.id.connecting_layout);
        connectingLayout.setVisibility(View.INVISIBLE);
        connectingLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        MediAlarmDevice.get().readDummyAlarmData();  // 첫번째 호출은 무조건 null을 반환하므로 강제적으로 한번 호출해 준다

        String name = Profile.get().getFullName();
        TextView nameText = (TextView) findViewById(R.id.user_name_text);
        nameText.setText(name.isEmpty() ? "성명" : name);

        drugNameText = (TextView) findViewById(R.id.drug_name_text);

        findViewById(R.id.read_data_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MediAlarmDevice.get().readAlarmData();
            }
        });

        deleteDrugButton = (Button) findViewById(R.id.delete_drug_button);

        if(SelectedDrugCase.get().isModify()) {
            DrugInfo currentDrugInfo = SelectedDrugCase.get().getCurrentDrugInfo();
            if(null != currentDrugInfo) {
                drugNameText.setText(currentDrugInfo.getName());
            }
            ((TextView)findViewById(R.id.save_drug_button)).setText("수정");
            deleteDrugButton.setVisibility(View.VISIBLE);
        } else {
            currentDrugNo = DrugManager.get().getNextNo();
            SelectedDrugCase.get().setCurrentDrug(new DrugInfo(currentDrugNo));
            deleteDrugButton.setVisibility(View.GONE);
        }

        MediAlarmDevice.get().setBaseView(this, this);
        setOnClickListeners();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //mBluetoothLeService.disconnect();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MediAlarmDevice.get().register();
    }

    @Override
    protected void onPause() {
        super.onPause();
        MediAlarmDevice.get().unregister();
    }


    @Override
    public void showScanningView() {

    }

    @Override
    public void hideScanningView() {

    }

    @Override
    public void onConnected() {

    }

    @Override
    public void onDisconnected() {

    }

    @Override
    public void onFailedDeviceSearch() {

    }



    private void setOnClickListeners() {
        findViewById(R.id.back_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        findViewById(R.id.save_drug_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(SelectedDrugCase.get().isModify()) {
                    updateDrugInfo();
                } else {
                    saveDrugInfo();
                }
            }
        });

        // 약정보
        findViewById(R.id.add_drug_info_menu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent drugInfo = new Intent(AddDrugActivity.this, DrugInfoActivity.class);
                startActivity(drugInfo);
            }
        });

        // 알림
        findViewById(R.id.alarm_menu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent alarm = new Intent(AddDrugActivity.this, AlarmSettingActivity.class);
                alarm.putExtra("drugNo", currentDrugNo);
                startActivity(alarm);
            }
        });

        // 삭제
        deleteDrugButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DrugInfo currentDrugInfo = SelectedDrugCase.get().getCurrentDrugInfo();
                DrugManager.get().removeDrug(currentDrugInfo.getNo());
                Toast.makeText(AddDrugActivity.this, "삭제되었습니다", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        // 보호자 추가
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();

        SelectedDrugCase.get().setCurrentDrug(null);   // 저장하지 않고 나가기
    }


    private void saveDrugInfo() {
        DrugInfo currentDrug = SelectedDrugCase.get().getCurrentDrugInfo();
        if(null == currentDrug) {
            Log.e(tag, "Current drug is null.");
            return;
        }

        String drugName = drugNameText.getText().toString();
        if(drugName.isEmpty()) {
            Toast.makeText(this, "약물명을 입력하세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        if(-1 == currentDrug.getColorIndex()) {
            Toast.makeText(this, "약물 색을 선택하세요." , Toast.LENGTH_SHORT).show();
            return;
        }

        if(-1 == currentDrug.getAlarmNo()) {
            Toast.makeText(this, "알림을 설정해주세요", Toast.LENGTH_SHORT).show();
            return;
        }

        currentDrug.setUser(Profile.get().getFullName());
        currentDrug.setName(drugName);

        DrugManager.get().addDrugInfo(currentDrug);
        MediAlarmDevice.get().saveAlarmToDevice(currentDrug.getAlarmNo());
        Toast.makeText(AddDrugActivity.this, "저장되었습니다", Toast.LENGTH_SHORT).show();
        finish();
    }


    private void updateDrugInfo() {
        DrugInfo currentDrug = SelectedDrugCase.get().getCurrentDrugInfo();
        if(null == currentDrug) {
            Log.e(tag, "Current drug is null.");
            return;
        }

        String drugName = drugNameText.getText().toString();
        if(drugName.isEmpty()) {
            Toast.makeText(this, "약물명을 입력하세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        if(-1 == currentDrug.getColorIndex()) {
            Toast.makeText(this, "약정보를 설정해 주세요", Toast.LENGTH_SHORT).show();
            return;
        }

        if(-1 == currentDrug.getAlarmNo()) {
            Toast.makeText(this, "알림을 설정해주세요", Toast.LENGTH_SHORT).show();
            return;
        }

        currentDrug.setUser(Profile.get().getFullName());
        currentDrug.setName(drugName);

        DrugManager.get().updateDrugInfo(currentDrug);
        MediAlarmDevice.get().saveAlarmToDevice(currentDrug.getAlarmNo());
        Toast.makeText(AddDrugActivity.this, "저장되었습니다", Toast.LENGTH_SHORT).show();
        finish();
    }

}
