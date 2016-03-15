package com.sinyuk.jianyimaterial.utils;

import android.util.Log;

/**
 * Created by Sinyuk on 16.2.5.
 */
public class LogUtils {

    public static void simpleLog(Class clazz, String what) {
        Log.w("MyLogcat", clazz.getSimpleName() + " -> " + what);
    }
}
