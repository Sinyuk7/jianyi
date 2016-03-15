package com.sinyuk.jianyimaterial.utils;

import android.content.Context;
import android.support.annotation.NonNull;

/**
 * Created by Sinyuk on 16.2.4.
 * 这是用来记录某些动作的次数 根据它们来做出相应的改变
 * 比如在用户第一次进入app的时候干嘛 之后就再也不干那个了
 * 就是这个意思
 */
public class SweetNotificationUtils {
    final Context mContext;

    public SweetNotificationUtils(Context mContext) {
        this.mContext = mContext;
    }

    /**
     * @param key      the tag in prefs
     * @param callback do something
     */
    public void once(@NonNull String key, NotifyCallback callback) {
        boolean isFirstTime = PreferencesUtils.getBoolean(mContext, key, true);
        if (isFirstTime) {
            callback.doSomething();
            PreferencesUtils.putBoolean(mContext, key, false);
        }
    }

    /**
     *
     * @param key
     * @param callback
     * @param stio 下次还要这样?
     */
    public void always(@NonNull String key, NotifyCallback callback, boolean stio) {
        boolean isAllowed = PreferencesUtils.getBoolean(mContext, key, true);
        if (isAllowed) {
            callback.doSomething();
            PreferencesUtils.putBoolean(mContext, key, stio);
        }
    }

    /**
     * @param key
     * @param callback
     * @param threshold 大于这个阈值触发这个动作
     */
    public void schedule(@NonNull String key, NotifyCallback callback, int threshold) {
        int times = PreferencesUtils.getInt(mContext, key, 0);
        if (times > threshold) {
            callback.doSomething();
        }
        PreferencesUtils.putInt(mContext, key, ++times);
    }

    private interface NotifyCallback {
        void doSomething();
    }

}
