package edu.milton.miltonmobileandroid.campus.doorlock;

import android.bluetooth.BluetoothDevice;

public class DoorLock {

    public final String name;
    public final String mac;
    public final BluetoothDevice device;


    public DoorLock(BluetoothDevice device) {
        name = device.getName();
        mac = device.getAddress().replace(":","");
        this.device = device;
    }
}
