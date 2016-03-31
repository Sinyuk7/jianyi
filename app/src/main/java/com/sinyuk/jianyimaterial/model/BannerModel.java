package com.sinyuk.jianyimaterial.model;

import android.content.Context;

import com.android.volley.Request;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import com.sinyuk.jianyimaterial.api.JBanner;
import com.sinyuk.jianyimaterial.api.JianyiApi;
import com.sinyuk.jianyimaterial.application.Jianyi;
import com.sinyuk.jianyimaterial.entity.Banner;
import com.sinyuk.jianyimaterial.volley.JsonRequest;
import com.sinyuk.jianyimaterial.volley.VolleyErrorHelper;

import java.util.List;

/**
 * Created by Sinyuk on 16.3.31.
 */
public class BannerModel {
    public static final String BANNER_REQUEST = "banner";

    private static BannerModel sInstance;
    private final Context mContext;
    private Gson mGson;

    private BannerModel(Context context) {
        this.mContext = context;
        mGson = new Gson();
    }

    public static BannerModel getInstance(Context context) {
        if (sInstance == null) {
            synchronized (BannerModel.class) {
                if (sInstance == null) {
                    sInstance = new BannerModel(context);
                }
            }
        }
        return sInstance;
    }

    public void getBannerByIndex(int index, RequestBannerCallback callback) {
        JsonRequest jsonRequest = new JsonRequest
                (Request.Method.GET, JianyiApi.bannerByIndex(index), null, response -> {
                    try {
                        JBanner jBanner = mGson.fromJson(response.toString(), JBanner.class);
                        JBanner.Data data = jBanner.getData();
                        String trans = mGson.toJson(data);
                        List<Banner> banners = mGson.fromJson(trans,
                                new TypeToken<List<Banner>>() {
                                }.getType());
                        if (banners != null) { callback.onCompleted(banners); }
                    } catch (JsonParseException e) {
                        callback.onParseError(e.getMessage());
                    }
                }, error -> callback.onVolleyError(VolleyErrorHelper.getMessage(error)));
        Jianyi.getInstance().addRequest(jsonRequest, BANNER_REQUEST);
    }

    public interface RequestBannerCallback {

        void onCompleted(List<Banner> banner);

        void onParseError(String message);

        void onVolleyError(String message);
    }
}
