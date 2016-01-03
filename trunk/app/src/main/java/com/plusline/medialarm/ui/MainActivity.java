package com.plusline.medialarm.ui;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.plusline.medialarm.R;
import com.plusline.medialarm.management.DrugManager;
import com.plusline.medialarm.management.MainOption;
import com.plusline.medialarm.management.Option;
import com.plusline.medialarm.management.PassedAlarmManager;
import com.plusline.medialarm.management.Profile;
import com.plusline.medialarm.management.SelectedDrugCase;
import com.plusline.medialarm.service.BluetoothLeService;
import com.plusline.medialarm.service.CheckAlarmService;
import com.plusline.medialarm.service.OnAlarmListener;
import com.plusline.medialarm.service.ServiceMain;
import com.plusline.medialarm.type.Alarm;
import com.plusline.medialarm.type.DrugInfo;

import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends FragmentActivity implements
        OnAlarmListener,
        MediAlarmDevice.BaseView {
    private static final String tag = "MainActivity";
    private static final int MAX_DAY_OFFSET = 6;

    private DrawerLayout drawerLayout;
    private MainSlidingMenu mainSlidingMenu;
    private QuickMenuLayout quickMenuLayout;
    private ReportLayout reportLayout;
    private SelectDrugAlarmLayout selectDrugAlarmLayout;
    private ViewPager drugCasePager;
    private View connectButton;

    private ImageView profileImageView;
    private TextView currentDayText;
    private View addDragButton;
    private ImageView quickOptionButton;

    private Calendar today;
    private int dayOffset = 0;

    private CheckAlarmService checkAlarmService;
    private BluetoothLeService mBluetoothLeService;
    private QuestionDialog questionDialog;
    private View scanningLayout;

    private BluetoothAdapter mBluetoothAdapter;
    private static final int REQUEST_ENABLE_BT = 1;


    @Override
    public void showScanningView() {
        scanningLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideScanningView() {
        scanningLayout.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onConnected() {
        Toast.makeText(this, "연결 되었습니다", Toast.LENGTH_SHORT).show();
        connectButton.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onDisconnected() {
        Toast.makeText(this, "연결이 해제 되었습니다", Toast.LENGTH_SHORT).show();
        connectButton.setVisibility(View.VISIBLE);
    }

    @Override
    public void onFailedDeviceSearch() {
        Toast.makeText(this, "디바이스를 찾을 수 없습니다", Toast.LENGTH_SHORT).show();
        connectButton.setVisibility(View.VISIBLE);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "이 휴대폰은 메디알람을 지원하지 않는 기종입니다.", Toast.LENGTH_SHORT).show();
        }

        // BLE 디바이스 연동 서비스
        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        startService(gattServiceIntent);
        bindService(gattServiceIntent, bluetoothServiceConnection, BIND_AUTO_CREATE);

        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        if (mBluetoothAdapter != null)
        {
            if (!mBluetoothAdapter.isEnabled())
            {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        }

        //
        // 알람 체크 서비스
        Intent alarmService = new Intent(this, CheckAlarmService.class);
        startService(alarmService);
        bindService(alarmService, checkAlarmServiceConnection, BIND_AUTO_CREATE);


        setChangedOptionListeners();
        today = Calendar.getInstance();
        SelectedDrugCase.get().select(today.get(Calendar.DAY_OF_MONTH));

        drawerLayout = (DrawerLayout) findViewById(R.id.main_drawer_layout);
        mainSlidingMenu = new MainSlidingMenu(
                findViewById(R.id.sliding_menu),
                drawerLayout);
        mainSlidingMenu.setMainActivity(this);

        quickMenuLayout = new QuickMenuLayout(this, findViewById(R.id.quick_menu_layout));

        drugCasePager = (ViewPager) findViewById(R.id.drug_case_pager);
        drugCasePager.setAdapter(new DrugCasePagerAdapter(getSupportFragmentManager()));
        drugCasePager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                dayOffset = position;
                updateUI();
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        profileImageView = (ImageView) findViewById(R.id.profile_image);
        currentDayText = (TextView) findViewById(R.id.today_date_text);
        addDragButton = findViewById(R.id.add_drag_button);
        quickOptionButton = (ImageView)findViewById(R.id.quick_menu_button);
        questionDialog = new QuestionDialog(findViewById(R.id.question_dialog));
        scanningLayout = findViewById(R.id.connecting_layout);
        scanningLayout.setVisibility(View.INVISIBLE);
        scanningLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        connectButton = findViewById(R.id.connect_button);

        reportLayout = new ReportLayout(findViewById(R.id.report_layout));
        selectDrugAlarmLayout = new SelectDrugAlarmLayout(
                findViewById(R.id.select_drug_alarm_layout),
                today.get(Calendar.DAY_OF_MONTH));

        initUI();
        displayCurrentDayText();
        setOnClickListeners();
        changeUIByOptions();
    }


    private void checkPassedAlarm() {
        DrugInfo passedDrug = PassedAlarmManager.get().getDrug();
        if(null == passedDrug) {
            return;
        }
        questionDialog.show(passedDrug);
    }


    // Code to manage Service lifecycle.
    private final ServiceConnection bluetoothServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                Log.e(tag, "Unable to initialize Bluetooth");
                finish();
            }

            ServiceMain.get().setBluetoothLeService(mBluetoothLeService);
            MediAlarmDevice.get().setBaseView(MainActivity.this, MainActivity.this);
            MediAlarmDevice.get().init();
            MediAlarmDevice.get().register();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };


    private final ServiceConnection checkAlarmServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            checkAlarmService = ((CheckAlarmService.CASBinder)service).getService();
            if(null == checkAlarmService) {
                Log.e(tag, "Unable to initialize Alarm Service");
                finish();
            }
            checkAlarmService.addOnAlarmListener(MainActivity.this);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            checkAlarmService = null;
        }
    };


    @Override
    protected void onDestroy() {
        super.onDestroy();
        MainOption.get().clearListeners();
        Profile.get().clearListeners();

        if(MediAlarmDevice.get() != null)
            MediAlarmDevice.get().disconnect();

        unbindService(checkAlarmServiceConnection);
        unbindService(bluetoothServiceConnection);
        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        stopService(gattServiceIntent);
    }


    @Override
    protected void onResume() {
        super.onResume();
        if(null != checkAlarmService) {
            checkAlarmService.addOnAlarmListener(this);
        }
        checkPassedAlarm();

        MediAlarmDevice.get().setBaseView(this, this);
        MediAlarmDevice.get().register();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(null != checkAlarmService) {
            checkAlarmService.removeAlarmListener(this);
        }
        MediAlarmDevice.get().unregister();
    }

    //
    //
    //
    @Override
    public void onChime(Alarm alarm) {
        DrugInfo drugInfo = DrugManager.get().getDrug(alarm.getDrugNo());
        questionDialog.show(drugInfo);
    }

    private Toast toast;
    private long backKeyPressedTime = 0;

    @Override
    public void onBackPressed() {
        if(reportLayout.isShow()) {
            reportLayout.hide();
            return;
        }

        if(selectDrugAlarmLayout.isShow()) {
            selectDrugAlarmLayout.hide();
            return;
        }

        if((backKeyPressedTime + 2000) < System.currentTimeMillis()) {
            backKeyPressedTime = System.currentTimeMillis();
            showExitGuide();
        } else {
            toast.cancel();
            super.onBackPressed();
        }
    }

    private void showExitGuide() {
        toast = Toast.makeText(this, "종료하시려면 뒤로가기 버튼을 한번 더 누르세요.", Toast.LENGTH_SHORT);
        toast.show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
            Toast.makeText(getBaseContext(), "이 앱을 사용하기 위해서는 블루투스가 활성화 되어 있어야 합니다.", Toast.LENGTH_LONG).show();
        }
    }

    private void initUI() {
        Drawable profileDrawable = Profile.get().getRoundedProfileImage();
        if(null != profileDrawable) {
            profileImageView.setImageDrawable(profileDrawable);
        }
    }


    private void setOnClickListeners() {
        findViewById(R.id.show_menu_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(findViewById(R.id.sliding_menu));
            }
        });

        // 약추가
        findViewById(R.id.add_drag_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSelectCartridgePage();
            }
        });

        findViewById(R.id.prev_day_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(0 == dayOffset) {
                    return;
                }

                --dayOffset;
                drugCasePager.setCurrentItem(dayOffset);
                updateUI();

            }
        });

        findViewById(R.id.next_day_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dayOffset < MAX_DAY_OFFSET) {
                    ++dayOffset;
                    drugCasePager.setCurrentItem(dayOffset);
                    updateUI();
                }
            }
        });

        quickOptionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                quickMenuLayout.toggle();
            }
        });

        findViewById(R.id.connect_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MediAlarmDevice.get().scanLeDevice(true);
            }
        });

        findViewById(R.id.profile_image).setOnClickListener(new OnProfileClickListener());
        findViewById(R.id.setting_profile_text).setOnClickListener(new OnProfileClickListener());
    }



    public void setQuickOptionButton(int resourceId) {
        quickOptionButton.setImageResource(resourceId);
    }


    private void updateUI() {
        selectDrugCase();
        displayCurrentDayText();
    }


    private void selectDrugCase() {
        SelectedDrugCase.get().select(today.get(Calendar.DAY_OF_MONTH) + dayOffset);
    }


    public void showSelectCartridgePage() {
        Intent addDrug = new Intent(MainActivity.this, SelectCartridgeActivity.class);
        startActivity(addDrug);
    }


    // 약 보관함
//    public void showDrugStorage() {
//        Intent storage = new Intent(this, DrugStorageActivity.class);
//        startActivity(storage);
//    }


    private class OnProfileClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent profileIntent = new Intent(MainActivity.this, ProfileActivity.class);
            startActivity(profileIntent);
        }
    }


    private void displayCurrentDayText() {
        Calendar displayCalendar = Calendar.getInstance();
        displayCalendar.set(today.get(Calendar.YEAR), today.get(Calendar.MONTH), today.get(Calendar.DAY_OF_MONTH));
        displayCalendar.add(Calendar.DAY_OF_MONTH, dayOffset);
        currentDayText.setText(Convert.toStr(displayCalendar, "MMM dd, E", Locale.KOREA));
    }


    private void setChangedOptionListeners() {
        MainOption.get().addOnChangedOptionListener(new Option.OnChangedOptionListener() {
            @Override
            public void onChanged() {
                changeUIByOptions();
                mainSlidingMenu.updateOptionValues();
            }
        });

        Profile.get().addOnChangedOptionListener(new Option.OnChangedOptionListener() {
            @Override
            public void onChanged() {
                changeUIByProfile();
                mainSlidingMenu.updateProfile();
            }
        });
    }


    private void changeUIByOptions() {
        if(MainOption.get().isSimpleMode()) {
            addDragButton.setVisibility(View.INVISIBLE);
            quickOptionButton.setVisibility(View.INVISIBLE);
        } else {
            addDragButton.setVisibility(View.VISIBLE);
            quickOptionButton.setVisibility(View.VISIBLE);
        }
    }


    private void changeUIByProfile() {
        Drawable profileDrawable = Profile.get().getRoundedProfileImage();
        if(null != profileDrawable) {
            profileImageView.setImageDrawable(profileDrawable);
        }
    }


    public void showReport() {
        reportLayout.show();
    }

    public void showSelectAlarmPage() {
        selectDrugAlarmLayout.show();
    }

    //
    //  DrugCasePagerAdapter
    //
    private class DrugCasePagerAdapter extends FragmentPagerAdapter {

        public DrugCasePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Calendar positionCalendar = Calendar.getInstance();
            positionCalendar.set(today.get(Calendar.YEAR), today.get(Calendar.MONTH), today.get(Calendar.DAY_OF_MONTH));
            positionCalendar.add(Calendar.DAY_OF_MONTH, position);

            return new DrugCaseFragment(positionCalendar.get(Calendar.DAY_OF_MONTH));
        }

        @Override
        public int getCount() {
            return 7;
        }
    }

}
