package com.plusline.medialarm.service;

/**
 *  메디알람에서 사용되는 서비스를 중앙 관리한다.
 */
public class ServiceMain {


    //
    //
    //
    private ServiceMain() {

    }

    private BluetoothLeService bluetoothLeService;

    public void setBluetoothLeService(BluetoothLeService bluetoothLeService) {
        this.bluetoothLeService = bluetoothLeService;
    }

    public BluetoothLeService getBluetoothLeService() {
        return bluetoothLeService;
    }













    ///////////////////////////////////////////////////////////////////////////////////////////////
    //

    private static ServiceMain main = new ServiceMain();
    public static ServiceMain get() {
        return main;
    }
}
