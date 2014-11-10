package edu.milton.miltonmobileandroid.util;

import android.content.Context;

public abstract class DPConverter {
    static Context context;
    //this is a committed source change
    public static int dpConvert(int sp) {
        float scale = context.getResources().getDisplayMetrics().density;

        int dpAsPixels = (int) (2*scale + 0.5f);
        return dpAsPixels;
    }
}
