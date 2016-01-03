package com.plusline.medialarm.service;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

/**
 * 블루투스를 사용하여 하드웨어 제어
 */
public class BluetoothService {
    private static final String TAG = "BluetoothService";

    public static final int REQUEST_ENABLE_BT = 3842;
    private BluetoothAdapter btAdapter;

    private Activity mActivity;
    private Handler mHandler;

    //
    // Constructors
    //
    public BluetoothService(Activity ac, Handler h) {
        mActivity = ac;
        mHandler = h;

        // BluetoothAdapter 얻기
        btAdapter = BluetoothAdapter.getDefaultAdapter();
    }


    public boolean getDeviceState() {
        Log.d(TAG, "Check the Bluetooth support");

        if(btAdapter == null) {
            Log.d(TAG, "Bluetooth is not available");
            return false;
        } else {
            Log.d(TAG, "Bluetooth is available");
            return true;
        }
    }

    public void enableBluetooth() {
        if(btAdapter.isEnabled()) {
            // 기기의 블루투스 상태가 On인 경우
            Log.d(TAG, "Bluetooth Enable Now");



            // Next Step
        } else {
            // 기기의 블루투스 상태가 Off인 경우
            Log.d(TAG, "Bluetooth Enable Request");

            Intent i = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            mActivity.startActivityForResult(i, REQUEST_ENABLE_BT);
        }
    }


    public void scanDevice() {
        Log.d(TAG, "Scan Device");

//        Intent serverIntent = new Intent(mActivity, DeviceListActivity.class);
//        mActivity.startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
    }

}
