package com.sinyuk.jianyimaterial.model;

import android.content.Context;
import android.support.annotation.NonNull;

import com.android.volley.Request;
import com.google.gson.Gson;
import com.sinyuk.jianyimaterial.api.JNeeds;
import com.sinyuk.jianyimaterial.api.JianyiApi;
import com.sinyuk.jianyimaterial.application.Jianyi;
import com.sinyuk.jianyimaterial.utils.LogUtils;
import com.sinyuk.jianyimaterial.volley.JsonRequest;
import com.sinyuk.jianyimaterial.volley.VolleyErrorHelper;

/**
 * Created by Sinyuk on 16.4.19.
 */
public class NeedsModel {
    private static final String REQUEST_NEEDS = "request_needs";
    private static NeedsModel sInstance;
    private final Context mContext;
    private Gson mGson;

    private NeedsModel(Context context) {
        this.mContext = context;
        mGson = new Gson();
    }

    public static NeedsModel getInstance(Context context) {
        if (sInstance == null) {
            synchronized (NeedsModel.class) {
                if (sInstance == null) {
                    sInstance = new NeedsModel(context);
                }
            }
        }
        return sInstance;
    }

    public void load(String url, int pageIndex, NeedsLoadCallback callback) {
        url = JianyiApi.needs(pageIndex);
        boolean isRefresh = pageIndex == 1;
        JsonRequest jsonRequest = new JsonRequest
                (Request.Method.GET, url, null, response -> {
                    try {
                        LogUtils.simpleLog(NeedsModel.class, "response -> " + response.toString());
                        JNeeds data = mGson.fromJson(response.toString(), JNeeds.class);
                        if (data.getData().getCurrent() > data.getData().getTotalPages()) {
                            callback.onNeedsReachBottom();
                        } else {
                            callback.onNeedsLoadSucceed(data, isRefresh);
                        }
                    } catch (Exception e) {
                        callback.onNeedsParseError(e.getMessage());
                    }
                }, error -> callback.onNeedsVolleyError(VolleyErrorHelper.getMessage(error)));
        Jianyi.getInstance().addRequest(jsonRequest, REQUEST_NEEDS);
    }

    public interface NeedsLoadCallback {

        void onNeedsLoadSucceed(@NonNull JNeeds data, boolean isRefresh);

        void onNeedsVolleyError(@NonNull String message);

        void onNeedsParseError(@NonNull String message);

        void onNeedsReachBottom();
    }
}
