package com.plusline.medialarm.ui;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;

/**
 * MediAlarm Controller
 */
public class Controller {

    private static final int REQUEST_ENABLE_BT = 3215;

    private final Context context;
    private BluetoothAdapter bluetoothAdapter;

    //
    //
    //
    public Controller(Context context) {
        this.context = context;


    }


    public void init() {
        final BluetoothManager bluetoothManager =
                (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();
    }

    public void enable(Activity activity) {
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            activity.startActivityForResult(intent, REQUEST_ENABLE_BT);
        }
    }


    public void scanMediAlarm() {

    }

}
