package com.sinyuk.jianyimaterial.utils;

import android.content.Context;
import android.text.TextUtils;

/**
 * Created by Sinyuk on 16.2.9.
 */
public class StringUtils {
    public static String getResString(Context context, int id) {
        if (id <= 0)
            return "";
        return context.getResources().getString(id);
    }

    /**
     *
     * @param context
     * @param atFirst 想用那个字符串
     * @param but 那个是空的 只好用这个字符串
     * @return
     */
    public static String getSweetString(Context context, String atFirst, int but) {
        return TextUtils.isEmpty(atFirst) ? getResString(context, but) : atFirst;
    }
}
