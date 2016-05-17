package com.sinyuk.jianyimaterial.model;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.sinyuk.jianyimaterial.api.Index;
import com.sinyuk.jianyimaterial.api.JianyiApi;
import com.sinyuk.jianyimaterial.api.Show;
import com.sinyuk.jianyimaterial.application.Jianyi;
import com.sinyuk.jianyimaterial.common.Constants;
import com.sinyuk.jianyimaterial.entity.YihuoDetails;
import com.sinyuk.jianyimaterial.entity.YihuoProfile;
import com.sinyuk.jianyimaterial.events.XRequestLoginEvent;
import com.sinyuk.jianyimaterial.greendao.dao.DaoUtils;
import com.sinyuk.jianyimaterial.greendao.dao.YihuoDetailsService;
import com.sinyuk.jianyimaterial.mvp.BaseModel;
import com.sinyuk.jianyimaterial.utils.HtmlUtils;
import com.sinyuk.jianyimaterial.utils.LogUtils;
import com.sinyuk.jianyimaterial.utils.PreferencesUtils;
import com.sinyuk.jianyimaterial.volley.FormDataRequest;
import com.sinyuk.jianyimaterial.volley.JsonRequest;
import com.sinyuk.jianyimaterial.volley.VolleyErrorHelper;

import org.greenrobot.eventbus.EventBus;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * Created by Sinyuk on 16.3.17.
 */
public class YihuoModel implements BaseModel {
    public static final String SHOW_REQUEST = "show";
    public static final String INDEX_REQUEST = "index";
    private static final String HEADLINE_REQUEST = "headline";
    private static final String HEADLINE_INDEX = "1";


    private static YihuoModel instance;
    private final Context mContext;
    private YihuoDetailsService yihuoDetailsService;
    private Gson mGson;

    private YihuoModel(Context context) {
        this.mContext = context;
        yihuoDetailsService = DaoUtils.getYihuoDetailsService();
        mGson = new Gson();
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

    public void loadLatestHeadline(RequestHeadlineCallback callback) {
        JsonRequest jsonRequest = new JsonRequest
                (Request.Method.GET, JianyiApi.yihuoDetails(HEADLINE_INDEX), null, response -> {
                    try {
                        Show show = mGson.fromJson(response.toString(), Show.class);
                        Show.Data jsonData = show.getData();
                        String trans = mGson.toJson(jsonData);
                        YihuoDetails data = mGson.fromJson(trans,
                                YihuoDetails.class);
                        if (data != null) { callback.onCompleted(data); }
                    } catch (JsonParseException e) {
                        callback.onParseError(e.getMessage());
                    }

                }, error -> callback.onVolleyError(VolleyErrorHelper.getMessage(error)));
        Jianyi.getInstance().addRequest(jsonRequest, HEADLINE_REQUEST);
    }


    public void getGoodsBySchool(String schoolIndex, int pageIndex, RequestYihuoProfileCallback callback) {
        getGoods(JianyiApi.goodsBySchool(schoolIndex, pageIndex), pageIndex, callback);
    }

    public void getProfileByUid(int pageIndex, String uid, RequestYihuoProfileCallback callback) {
        boolean isRefresh = pageIndex == 1;
        LogUtils.simpleLog(YihuoModel.class, JianyiApi.goodsByUser(uid, pageIndex));
        JsonRequest jsonRequest = new JsonRequest
                (Request.Method.GET, JianyiApi.goodsByUser(uid, pageIndex), null, response -> {
                    try {
                        Index index = mGson.fromJson(response.toString(), Index.class);
                  /*      String trans = mGson.toJson(index.getData().getItems());
                        List<YihuoProfile> data = mGson.fromJson(trans,
                                new TypeToken<List<YihuoProfile>>() {
                                }.getType());*/

                        if (index != null) { callback.onCompleted(index, isRefresh); }
                    } catch (JsonParseException e) {
                        callback.onParseError(e.getMessage());
                    }
                }, error -> callback.onVolleyError(VolleyErrorHelper.getMessage(error)));
        Jianyi.getInstance().addRequest(jsonRequest, INDEX_REQUEST);

    }


    public void getProfileByParams(int pageIndex, HashMap<String, String> params, RequestYihuoProfileCallback callback) {
        boolean isRefresh = pageIndex == 1;
        FormDataRequest jsonRequest = new FormDataRequest
                (Request.Method.POST, JianyiApi.filterYihuoProfile(pageIndex), response -> {
                    try {
                        Index index = mGson.fromJson(response, Index.class);
                     /*   String trans = mGson.toJson(index.getData().getItems());
                        List<YihuoProfile> data = mGson.fromJson(trans,
                                new TypeToken<List<YihuoProfile>>() {
                                }.getType());*/
                        // do clear
                        if (index != null) { callback.onCompleted(index, isRefresh); }
                    } catch (JsonParseException e) {
                        callback.onParseError(e.getMessage());
                    }
                }, error -> callback.onVolleyError(VolleyErrorHelper.getMessage(error))) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return params;
            }
        };
        Jianyi.getInstance().addRequest(jsonRequest, INDEX_REQUEST);
    }

    public void getGoods(String url, int pageIndex, RequestYihuoProfileCallback callback) {
        boolean isRefresh = pageIndex == 1;
        JsonRequest jsonRequest = new JsonRequest
                (Request.Method.GET, url, null, response -> {
                    try {
                        Index index = mGson.fromJson(response.toString(), Index.class);
                     /*   String trans = mGson.toJson(index.getData().getItems());
                        List<YihuoProfile> data = mGson.fromJson(trans,
                                new TypeToken<List<YihuoProfile>>() {
                                }.getType());*/

                        // do clear
                        if (index != null) { callback.onCompleted(index, isRefresh); }
                    } catch (JsonParseException e) {
                        callback.onParseError(e.getMessage());
                    }
                }, error -> callback.onVolleyError(VolleyErrorHelper.getMessage(error)));
        Jianyi.getInstance().addRequest(jsonRequest, INDEX_REQUEST);
    }

    public void getSearchResult(String param, int pageIndex, RequestYihuoProfileCallback callback) {
        boolean isRefresh = pageIndex == 1;
        FormDataRequest formDataRequest = new FormDataRequest
                (Request.Method.GET, JianyiApi.search(pageIndex, param), str -> {
                    // the response is already constructed as a JSONObject!
                    JsonParser parser = new JsonParser();
                    final JsonObject response = parser.parse(HtmlUtils.removeHtml(str)).getAsJsonObject();
                    try {
                        Index index = mGson.fromJson(response, Index.class);

                        if (index != null) { callback.onCompleted(index, isRefresh); }
                    } catch (JsonParseException e) {
                        callback.onParseError(e.getMessage());
                    }
                }, error -> callback.onVolleyError(VolleyErrorHelper.getMessage(error)));

        Jianyi.getInstance().addRequest(formDataRequest, INDEX_REQUEST);
    }

    public void getDetails(@NonNull String yihuoId, RequestYihuoDetailsCallback callback) {
        JsonRequest jsonRequest = new JsonRequest
                (Request.Method.GET, JianyiApi.yihuoDetails(yihuoId), null, response -> {
                    try {
                        Show show = mGson.fromJson(response.toString(), Show.class);
                        Show.Data jsonData = show.getData();
                        String trans = mGson.toJson(jsonData);
                        YihuoDetails data = mGson.fromJson(trans,
                                YihuoDetails.class);
                        if (data != null) { callback.onCompleted(data); }
                    } catch (JsonParseException e) {
                        callback.onParseError(e.getMessage());
                    }

                }, error -> callback.onVolleyError(VolleyErrorHelper.getMessage(error)));
        Jianyi.getInstance().addRequest(jsonRequest, SHOW_REQUEST);
    }

    /**
     * check whether this Yihuo has been added into Likes already
     *
     * @return
     */
    public Observable getLikeState(@NonNull String yihuoId) {
        return Observable.create((Observable.OnSubscribe<Boolean>) subscriber -> {
            subscriber.onNext(checkLikeState(yihuoId));
            subscriber.onCompleted();
        }).subscribeOn(Schedulers.io());
    }


    private Boolean checkLikeState(String yihuoId) {
        YihuoDetails data = (YihuoDetails) yihuoDetailsService.query(yihuoId);
        return null != data;
    }

    public void addToLikes(@NonNull YihuoDetails detailsData, LikesCallback callback) {
        String uId = PreferencesUtils.getString(mContext, Constants.Prefs_Uid);
        if (TextUtils.isEmpty(uId)) {
            EventBus.getDefault().post(new XRequestLoginEvent());
        } else {
            Date addedDate = new Date(System.currentTimeMillis()); //获取当前时间
            detailsData.setDate(addedDate);
            yihuoDetailsService.saveOrUpdate(detailsData);
            callback.onAddToLikes();
        }
    }

    public void removeFromLikes(@NonNull YihuoDetails detailsData, LikesCallback callback) {
        yihuoDetailsService.deleteByKey(detailsData.getId());
        callback.onRemoveFromLikes();
    }


    public interface LikesCallback {
        void onAddToLikes();

        void onRemoveFromLikes();
    }

    public interface RequestHeadlineCallback {

        void onVolleyError(String message);

        void onCompleted(YihuoDetails data);

        void onParseError(String message);
    }

    public interface RequestYihuoDetailsCallback {

        void onVolleyError(String message);

        void onCompleted(YihuoDetails data);

        void onParseError(String message);
    }

    public interface RequestYihuoProfileCallback {

        void onVolleyError(String message);

        void onCompleted(Index data, boolean isRefresh);

        void onParseError(String message);
    }
}
