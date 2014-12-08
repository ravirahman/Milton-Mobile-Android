package edu.milton.miltonmobileandroid.saa;

import java.io.Serializable;

/**
 * Created by ravi_000 on 11/15/2014.
 */
public class EventFeed implements Serializable{
    public final String name;
    public final String description;
    public final int id;
    public EventFeed(int id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;

    }
}
