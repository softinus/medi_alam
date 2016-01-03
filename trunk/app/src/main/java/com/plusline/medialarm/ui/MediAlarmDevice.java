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
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import com.plusline.medialarm.management.AlarmManager;
import com.plusline.medialarm.management.SelectedDrugCase;
import com.plusline.medialarm.service.BluetoothLeService;
import com.plusline.medialarm.service.ServiceMain;
import com.plusline.medialarm.type.Alarm;
import com.plusline.medialarm.type.Cartridge;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * MediAlarm 하드웨어 제어
 */
public class MediAlarmDevice {
    private static final String tag = "MediAlarmDevice";

    public interface BaseView {

        void showScanningView();
        void hideScanningView();

        void onConnected();
        void onDisconnected();
        void onFailedDeviceSearch();
    }

    //
    // BLE 관련
    private static final long SCAN_PERIOD = 10000;
    private static final String MORNING_UUID = "0000fff1-0000-1000-8000-00805f9b34fb";
    private static final String LUNCH_UUID = "0000fff2-0000-1000-8000-00805f9b34fb";
    private static final String EVENING_UUID = "0000fff3-0000-1000-8000-00805f9b34fb";
    private static final String SET_TIME = "0000fff4-0000-1000-8000-00805f9b34fb";
    private static final String SET_MODE = "0000fff6-0000-1000-8000-00805f9b34fb";


    private Activity activity;
    private BaseView baseView;
    private BluetoothLeService bluetoothLeService;
    private BluetoothAdapter mBluetoothAdapter = null;
    private BluetoothDevice mediAlarmDevice;

    private Handler mHandler;
    private Map<String, BluetoothGattCharacteristic> serviceCharacteristics = new HashMap<>();
    private Map<Cartridge.Type, String> alarmUuids = new HashMap<>();


    //
    //  constructor
    //
    private MediAlarmDevice() {
        mHandler = new Handler();
    }

    public void setBaseView(Activity activity, BaseView baseView) {
        this.activity = activity;
        this.baseView = baseView;
    }


    public void init() {
        alarmUuids.put(Cartridge.Type.Morning, MORNING_UUID);
        alarmUuids.put(Cartridge.Type.Lunch, LUNCH_UUID);
        alarmUuids.put(Cartridge.Type.Evening, EVENING_UUID);

        final BluetoothManager bluetoothManager =
                (BluetoothManager) activity.getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        if (mBluetoothAdapter == null) {
            Toast.makeText(
                    activity,
                    "블루투스를 지원하지 않는 기종입니다. 메디알람에 알람 설정을 할 수 없습니다.",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if(null != bluetoothLeService) {
            bluetoothLeService.disconnect();
        }

        bluetoothLeService = ServiceMain.get().getBluetoothLeService();
        if(null == bluetoothLeService) {
            Log.e(tag, "BLE Service is null");
        }
        scanLeDevice(true);     // scan & connect device
    }


    public void scanLeDevice(final boolean enable) {
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            checkDeviceIsValid();
                        }
                    });
                }
            }, SCAN_PERIOD);

            baseView.showScanningView();
            mBluetoothAdapter.startLeScan(mLeScanCallback);
        } else {
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }
    }


    public void disconnect() {
        bluetoothLeService.disconnect();
    }


    private void checkDeviceIsValid() {
        baseView.hideScanningView();
        if(null == mediAlarmDevice) {
            baseView.onFailedDeviceSearch();
        }
    }


    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {
                @Override
                public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (device.getName().contains("MEDIALRAM")) {
                                Log.d(tag, "Find MediAlarm device => " + device.getName());
                                Log.d(tag, "Device Address: " + device.getAddress());

                                mediAlarmDevice = device;
                                bluetoothLeService.connect(mediAlarmDevice.getAddress());
                                scanLeDevice(false);
                            }
                        }
                    });
                }
            };


    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                baseView.hideScanningView();
                baseView.onConnected();
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                baseView.hideScanningView();
                baseView.onDisconnected();
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                // Show all the supported services and characteristics on the user interface.
                displayGattServices(bluetoothLeService.getSupportedGattServices());
                setCurrentTime();
                setModeToEnable();
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                displayData(intent.getStringExtra(BluetoothLeService.EXTRA_DATA));
            }
        }
    };

    // Demonstrates how to iterate through the supported GATT Services/Characteristics.
    // In this sample, we populate the data structure that is bound to the ExpandableListView
    // on the UI.
    private void displayGattServices(List<BluetoothGattService> gattServices) {
        if (gattServices == null) {
            return;
        }

        for (BluetoothGattService gattService : gattServices) {
            List<BluetoothGattCharacteristic> gattCharacteristics = gattService.getCharacteristics();
            for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                serviceCharacteristics.put(gattCharacteristic.getUuid().toString(), gattCharacteristic);
                Log.d(tag, "Characteristic: " + gattCharacteristic.getUuid());
            }
        }
    }


    private void displayData(String data) {
        if (data != null) {
            //mDataField.setText(data);
            Log.d(tag, "Display data: " + data);
        }
    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }


    public void saveAlarmToDevice(int alarmNo) {
        Alarm alarm = AlarmManager.get().getAlarm(alarmNo);
        if(null == alarm) {
            return;
        }

        String uuid = alarmUuids.get(SelectedDrugCase.get().getSelectedCartridge().getType());
        BluetoothGattCharacteristic characteristic = serviceCharacteristics.get(uuid);
        if(null == characteristic) {
            Log.e(tag, "Characteristic is not exist");
            return;
        }

        byte[] alarmTime = new byte[] {
                (byte)alarm.getHourOfDay(),
                (byte)alarm.getMinute()
        };
        characteristic.setValue(alarmTime);
        Log.d(tag, "Write alarm data: " + byteToHex(alarmTime));
        bluetoothLeService.writeCharacteristic(characteristic);
    }



    private void setCurrentTime() {
        Calendar currentTime = Calendar.getInstance();
        BluetoothGattCharacteristic characteristic = serviceCharacteristics.get(SET_TIME);
        if(null == characteristic) {
            Log.e(tag, "Characteristic is not exist");
            return;
        }

        bluetoothLeService.readCharacteristic(characteristic);
        SystemClock.sleep(500);
        byte[] readTimeData = characteristic.getValue();
        if(null == readTimeData) {
            Toast.makeText(activity, "Read time data: null", Toast.LENGTH_SHORT).show();
        } else {
            Log.d(tag, "Current Time Data: " + byteToHex(readTimeData));
        }

        byte[] timeValue = new byte[] {
                (byte) currentTime.get(Calendar.HOUR_OF_DAY),
                (byte) currentTime.get(Calendar.MINUTE) };
        characteristic.setValue(timeValue);
        bluetoothLeService.writeCharacteristic(characteristic);
    }


    private void setModeToEnable() {
        final BluetoothGattCharacteristic characteristic = serviceCharacteristics.get(SET_MODE);
        if (null == characteristic) {
            Log.e(tag, "Characteristic is not exist");
            return;
        }

        bluetoothLeService.readCharacteristic(characteristic);
        final byte[] readModeData = characteristic.getValue();
        if (null == readModeData) {
            Log.e(tag, "모드 정보 읽기 실패.");
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    setModeToEnable();
                }
            }, 1000);
            return;
        } else {
            Log.d(tag, "MODE Data: " + byteToHex(readModeData));
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (readModeData[0] == 0x00) {
                    readModeData[0] = 0x01;
                    characteristic.setValue(readModeData);
                    bluetoothLeService.writeCharacteristic(characteristic);
                }
            }
        }, 1000);
    }


    private String byteToHex(byte[] data) {
        StringBuilder sb = new StringBuilder();
        for(final byte b: data)
            sb.append(String.format("%02x ", b&0xff));
        return sb.toString();
    }

    public void readDummyAlarmData() {
        String uuid = alarmUuids.get(SelectedDrugCase.get().getSelectedCartridge().getType());
        BluetoothGattCharacteristic characteristic = serviceCharacteristics.get(uuid);
        if(null == characteristic) {
            Log.e(tag, "Characteristic is not exist");
            return;
        }

        bluetoothLeService.readCharacteristic(characteristic);
        characteristic.getValue();
    }

    public void readAlarmData() {
        String uuid = alarmUuids.get(SelectedDrugCase.get().getSelectedCartridge().getType());
        BluetoothGattCharacteristic characteristic = serviceCharacteristics.get(uuid);
        if(null == characteristic) {
            Log.e(tag, "Characteristic is not exist");
            return;
        }

        bluetoothLeService.readCharacteristic(characteristic);
        byte[] readData = characteristic.getValue();
        if(null == readData) {
            Toast.makeText(activity, "Read Data: null", Toast.LENGTH_SHORT).show();
        } else {
            String dataString = byteToHex(readData);
            Toast.makeText(activity, "Read Data: " + dataString, Toast.LENGTH_SHORT).show();
        }
    }


    public void register() {
        if(null != activity) {
            activity.registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        }
    }

    public void unregister() {
        if(null != activity) {
            activity.unregisterReceiver(mGattUpdateReceiver);
        }
    }



    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    private static MediAlarmDevice device = new MediAlarmDevice();
    public static MediAlarmDevice get() {
        return device;
    }

}
