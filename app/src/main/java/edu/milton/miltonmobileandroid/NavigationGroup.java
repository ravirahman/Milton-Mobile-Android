package edu.milton.miltonmobileandroid;

/**
 * Created by ravi on 12/14/14.
 */
public class NavigationGroup {
    public final String title;
    public final int id;

    public NavigationGroup(String title, int id) {
        this.title = title;
        this.id = id;
    }

    @Override
    public String toString() {
        return title;
    }
}
