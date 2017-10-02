package io.github.xiewinson.movingparticlesview.util;

import android.util.Log;

/**
 * Created by winson on 2017/10/1.
 */

public class LogUtil {
    public static final String TAG = "movingparticlesview";
    public static void d(Object obj) {
        if(obj!= null) {
            Log.d(TAG, obj.toString());
        }
    }
    public static void e(Object obj) {
        if(obj!= null) {
            Log.e(TAG, obj.toString());
        }
    }
}
