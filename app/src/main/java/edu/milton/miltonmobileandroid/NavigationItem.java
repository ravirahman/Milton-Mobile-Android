package edu.milton.miltonmobileandroid;

import android.graphics.drawable.Drawable;

/**
 * Created by ravi on 12/14/14.
 */
public class NavigationItem {
    public final String title;
    public final int id;
    public final Class aClass;
    //public final Drawable icon;

    public NavigationItem(String title, int id, Class aClass) {
        this.title = title;
        this.id = id;
        this.aClass = aClass;
    }
}
