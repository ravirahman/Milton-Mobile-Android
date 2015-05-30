package edu.milton.miltonmobileandroid.util;

import android.app.Application;
import android.content.Context;

/**
 * Created by Ravi on 5/30/15.
 */
public class MiltonMobileAndroid extends Application {
    private static MiltonMobileAndroid instance;

    public static MiltonMobileAndroid getInstance() {
        return instance;
    }

    public static Context getContext(){
        return instance;
        // or return instance.getApplicationContext();
    }

    @Override
    public void onCreate() {
        instance = this;
        super.onCreate();
    }
}
