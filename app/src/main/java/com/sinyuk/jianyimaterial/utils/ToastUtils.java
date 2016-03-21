package com.sinyuk.jianyimaterial.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.Toast;

/**
 * Created by Sinyuk on 16.2.27.
 */
public class ToastUtils {
    public static void toastFast(@NonNull Context context,@NonNull String message) {
        Toast.makeText(context,message,Toast.LENGTH_SHORT).show();
    }

    public static void toastFast(@NonNull Context context,@NonNull int resId) {
        Toast.makeText(context,context.getString(resId),Toast.LENGTH_SHORT).show();
    }

    public static void toastSlow(@NonNull Context context,@NonNull String message) {
        Toast.makeText(context,message,Toast.LENGTH_LONG).show();
    }


    public static void toastSlow(@NonNull Context context,@NonNull int resId) {
        Toast.makeText(context,context.getString(resId),Toast.LENGTH_LONG).show();
    }
}
