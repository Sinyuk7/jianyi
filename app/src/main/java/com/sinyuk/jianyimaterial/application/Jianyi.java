package com.sinyuk.jianyimaterial.application;

import android.app.Application;
import android.os.Environment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.sinyuk.jianyimaterial.R;
import com.sinyuk.jianyimaterial.activities.HomeActivity;
import com.sinyuk.jianyimaterial.greendao.dao.DaoCore;
import com.sinyuk.jianyimaterial.utils.LogUtils;

import java.io.File;
import java.io.IOException;

import cat.ereza.customactivityoncrash.CustomActivityOnCrash;

/**
 * Created by Sinyuk on 15.12.28.
 */
public class Jianyi extends Application {
    private RequestQueue mRequestQueue;
    private static Jianyi mInstance;
    public static final String TAG = Jianyi.class.getName();
    public static String mAbsAppPath;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        DaoCore.init(this);
        CustomActivityOnCrash.install(this);
        CustomActivityOnCrash.setRestartActivityClass(HomeActivity.class);
        CustomActivityOnCrash.setDefaultErrorActivityDrawable(R.drawable.turtle_lost404);
    }



    public static synchronized Jianyi getInstance() {
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        return mRequestQueue;
    }

    public <T> void add(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public <T> void addRequest(Request<T> request, String tag) {
        request.setTag(tag);
        getRequestQueue().add(request);
    }

    public void cancelPendingRequest(Object tag) {
        getRequestQueue().cancelAll(tag);
    }

    public void cancel() {
        mRequestQueue.cancelAll(TAG);
    }
}
