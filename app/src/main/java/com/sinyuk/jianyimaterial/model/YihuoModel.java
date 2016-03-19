package com.sinyuk.jianyimaterial.model;

import android.content.Context;
import android.support.annotation.NonNull;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.sinyuk.jianyimaterial.api.JianyiApi;
import com.sinyuk.jianyimaterial.api.Show;
import com.sinyuk.jianyimaterial.application.Jianyi;
import com.sinyuk.jianyimaterial.entity.YihuoDetails;
import com.sinyuk.jianyimaterial.greendao.dao.DaoUtils;
import com.sinyuk.jianyimaterial.greendao.dao.YihuoDetailsService;
import com.sinyuk.jianyimaterial.mvp.BaseModel;
import com.sinyuk.jianyimaterial.volley.JsonRequest;

/**
 * Created by Sinyuk on 16.3.17.
 */
public class YihuoModel implements BaseModel {
    public static final String SHOW_REQUEST = "show";
    public static final String INDEX_REQUEST = "index";


    private static YihuoModel instance;
    private final Context mContext;
    private final YihuoDetailsService yihuoDetailsService;
    private Gson gson;

    public YihuoModel(Context context) {
        this.mContext = context;
        yihuoDetailsService = DaoUtils.getYihuoDetailsService();
        gson = new Gson();
    }


    public static YihuoModel getInstance(Context context) {
        if (instance == null) {
            synchronized (UserModel.class) {
                if (instance == null) {
                    instance = new YihuoModel(context);
                }
            }
        }
        return instance;
    }

    public void getBrief() {

    }

    public void getDetails(@NonNull String yihuoId, RequestYihuoDetailsCallback callback) {
        JsonRequest jsonRequest = new JsonRequest
                (Request.Method.GET, JianyiApi.yihuoDetails(yihuoId), null, response -> {
                    try {
                        Show show = gson.fromJson(response.toString(), Show.class);
                        Show.Data jsonData = show.getData();
                        String trans = gson.toJson(jsonData);
                        YihuoDetails data = gson.fromJson(trans,
                                YihuoDetails.class);
                        if (data != null)
                            callback.onCompleted(data);
                    } catch (JsonParseException e) {
                        callback.onParseError(e);
                    }

                }, callback::onNetworkError);
        Jianyi.getInstance().addRequest(jsonRequest, SHOW_REQUEST);
    }
    
    public void addToLikes(){
        
    }
    
    public void removeFromLikes(){
        
    }

    public interface RequestYihuoDetailsCallback {

        void onNetworkError(VolleyError error);

        void onCompleted(YihuoDetails data);

        void onParseError(JsonParseException error);
    }
}
