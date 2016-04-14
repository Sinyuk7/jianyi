package com.sinyuk.jianyimaterial.model;

import android.content.Context;

import com.android.volley.Request;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import com.sinyuk.jianyimaterial.api.JSchool;
import com.sinyuk.jianyimaterial.api.JianyiApi;
import com.sinyuk.jianyimaterial.application.Jianyi;
import com.sinyuk.jianyimaterial.entity.School;
import com.sinyuk.jianyimaterial.greendao.dao.DaoUtils;
import com.sinyuk.jianyimaterial.greendao.dao.SchoolService;
import com.sinyuk.jianyimaterial.volley.JsonRequest;
import com.sinyuk.jianyimaterial.volley.VolleyErrorHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sinyuk on 16.4.14.
 */
public class SchoolModel {
    private static final String SCHOOL_REQUEST = "school";
    private static SchoolModel sInstance;
    private final Context mContext;
    private final SchoolService mSchoolService;
    private List<School> schoolList = new ArrayList<>();
    private Gson mGson;

    private SchoolModel(Context context) {
        this.mContext = context;
        mSchoolService = DaoUtils.getSchoolService();
        mGson = new Gson();
        schoolList = mSchoolService.queryAll();
    }


    public static SchoolModel getInstance(Context context) {
        if (sInstance == null) {
            synchronized (SchoolModel.class) {
                if (sInstance == null) {
                    sInstance = new SchoolModel(context);
                }
            }
        }
        return sInstance;
    }

    public void getSchools(LoadSchoolsCallback callback) {
        if (schoolList != null) {
            callback.onLoadSchoolSucceed(schoolList);
        } else {
            fetchSchools(callback);
        }
    }

    public void fetchSchools(LoadSchoolsCallback callback) {
        JsonRequest jsonRequest = new JsonRequest
                (Request.Method.GET, JianyiApi.schools(), null, response -> {
                    try {
                        JSchool jSchool = mGson.fromJson(response.toString(), JSchool.class);

                        List<JSchool.Data> items = jSchool.getData();

                        String trans = mGson.toJson(items);

                        List<School> data = mGson.fromJson(trans,
                                new TypeToken<List<School>>() {
                                }.getType());

                        // do clear
                        if (data != null) { callback.onLoadSchoolSucceed(data); }
                    } catch (JsonParseException e) {
                        callback.onLoadSchoolParseError(e.getMessage());
                    }
                }, error -> callback.onLoadSchoolVolleyError(VolleyErrorHelper.getMessage(error)));
        Jianyi.getInstance().addRequest(jsonRequest, SCHOOL_REQUEST);
    }

    public void updateUserSchool(){

    }

    public interface LoadSchoolsCallback {

        void onLoadSchoolSucceed(List<School> schoolList);

        void onLoadSchoolParseError(String message);

        void onLoadSchoolVolleyError(String message);
    }
}
