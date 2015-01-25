package edu.milton.miltonmobileandroid.campus.doorlock;

/**
 * Created by ravi on 1/25/15.
 */
public class DoorLock {

    public String name;
    public String location;
    public int id;
    public String mac;

    public DoorLock(String name, String location, int id, String mac) {
        this.name = name;
        this.location = location;
        this.id = id;
        this.mac = mac;
    }
}
