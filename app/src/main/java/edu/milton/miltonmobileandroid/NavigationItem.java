package edu.milton.miltonmobileandroid;

/**
 * Created by ravi on 12/14/14.
 */
public class NavigationItem {
    public final String title;
    public final int id;
    public final Class aClass;

    public NavigationItem(String title, int id, Class aClass) {
        this.title = title;
        this.id = id;
        this.aClass = aClass;
    }
}
