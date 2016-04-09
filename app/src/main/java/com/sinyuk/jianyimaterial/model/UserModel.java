package com.sinyuk.jianyimaterial.model;

import android.content.Context;
import android.support.annotation.NonNull;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sinyuk.jianyimaterial.R;
import com.sinyuk.jianyimaterial.api.JLoginError;
import com.sinyuk.jianyimaterial.api.JResponse;
import com.sinyuk.jianyimaterial.api.JUser;
import com.sinyuk.jianyimaterial.api.JianyiApi;
import com.sinyuk.jianyimaterial.application.Jianyi;
import com.sinyuk.jianyimaterial.entity.User;
import com.sinyuk.jianyimaterial.greendao.dao.DaoUtils;
import com.sinyuk.jianyimaterial.greendao.dao.UserService;
import com.sinyuk.jianyimaterial.mvp.BaseModel;
import com.sinyuk.jianyimaterial.utils.PreferencesUtils;
import com.sinyuk.jianyimaterial.utils.StringUtils;
import com.sinyuk.jianyimaterial.volley.FormDataRequest;
import com.sinyuk.jianyimaterial.volley.VolleyErrorHelper;

import java.util.HashMap;
import java.util.Map;

import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;

/**
 * Created by Sinyuk on 16.3.16.
 */
public class UserModel implements BaseModel {
    public static final String LOGIN_REQUEST = "login";
    public static final String UPDATE_REQUEST = "update";
    public static final String REGISTER = "register";


    private static UserModel sInstance;
    private final Context mContext;
    private final UserService mUserService;
    private User mCurrentUser;
    private Gson mGson;

    private UserModel(Context context) {
        this.mContext = context;
        mUserService = DaoUtils.getUserService();
        mGson = new Gson();
    }


    public static UserModel getInstance(Context context) {
        if (sInstance == null) {
            synchronized (UserModel.class) {
                if (sInstance == null) {
                    sInstance = new UserModel(context);
                }
            }
        }
        return sInstance;
    }

    public boolean isLoggedIn() {
        return PreferencesUtils.getBoolean(mContext, StringUtils.getRes(mContext, R.string.key_login_state));
    }

    /**
     * this may block UI thread
     *
     * @return current user if has logged in
     */
    public User getCurrentUser() {
        String uId = PreferencesUtils.getString(mContext, StringUtils.getRes(mContext, R.string.key_user_id));
        mCurrentUser = (User) mUserService.query(uId);
        return mCurrentUser;

    }


    public void login(@NonNull String tel, @NonNull String password, LoginCallback callback) {
        FormDataRequest jsonRequest = new FormDataRequest(Request.Method.POST, JianyiApi.login(), (Response.Listener<String>) str -> {
            JsonParser parser = new JsonParser();
            final JsonObject response = parser.parse(str).getAsJsonObject();
            JUser jsonData = mGson.fromJson(response, JUser.class);
            JUser.Data data = jsonData.getData();
            String trans = mGson.toJson(data);
            User userData = mGson.fromJson(trans, User.class);
            if (userData != null) {
                // TODO: 这里应该保存数据 然后保存成功在回调onSucceed();
                registerSucceed(userData, password);
                callback.onSucceed();
            } else {
                JLoginError error = mGson.fromJson(response, JLoginError.class);
                callback.onFailed(error.getError_msg());
            }
        }, (Response.ErrorListener) error -> callback.onError(VolleyErrorHelper.getMessage(error))) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("tel", tel);
                params.put("password", password);
                return params;
            }
        };

        Jianyi.getInstance().addRequest(jsonRequest, LOGIN_REQUEST);
    }

    private void registerSucceed(User userData, String password) {
        mUserService.saveOrUpdate(userData);
        if (null != mCurrentUser) {
            if (!userData.getId().equals(mCurrentUser.getId())) {
                PreferencesUtils.clearAll(mContext); // a new user has login  clean up the prefs
            }
        }

        PreferencesUtils.putString(mContext, StringUtils.getRes(mContext, R.string.key_user_id), userData.getId());
        PreferencesUtils.putBoolean(mContext, StringUtils.getRes(mContext, R.string.key_login_state), true);
        int loginTimes = PreferencesUtils.getInt(mContext, StringUtils.getRes(mContext, R.string.key_login_times), 0) + 1;
        PreferencesUtils.putInt(mContext, StringUtils.getRes(mContext, R.string.key_login_times), loginTimes);
        // post event first  in case they clean up the prefs
        PreferencesUtils.putString(mContext, StringUtils.getRes(mContext, R.string.key_psw), password);
    }


    public void register(@NonNull String tel, @NonNull String password, RegisterCallback callback) {
        FormDataRequest jsonRequest = new FormDataRequest(Request.Method.POST, JianyiApi.register(), (Response.Listener<String>) str -> {
            try {
                JsonParser parser = new JsonParser();
                final JsonObject response = parser.parse(str).getAsJsonObject();
                JUser jUser = mGson.fromJson(response, JUser.class);
                // 转换成我的Model;
                if (jUser != null) {
                    String trans = mGson.toJson(jUser.getData());
                    final User userData = mGson.fromJson(trans, User.class);
                    if (null != userData) { callback.onSucceed(); }
                } else {
                    JResponse jResponse = mGson.fromJson(response, JResponse.class);
                    if (jResponse != null) { callback.onFailed(jResponse.getData()); }
                }
            } catch (Exception e) {
                callback.onParseError(e.getMessage());
            }
        }, (Response.ErrorListener) error -> callback.onVolleyError(VolleyErrorHelper.getMessage(error))) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("tel", tel);
                params.put("password", password);
                return params;
            }
        };
        Jianyi.getInstance().addRequest(jsonRequest, REGISTER);
    }

    /**
     * there is no callback cause I don't know what's going on
     * @param tel
     */
    void askForAuthenticode(@NonNull String tel) {

    }

    void checkAuthenticode(@NonNull String tel, @NonNull String authenticode) {

    }

    void load(@NonNull String tel, @NonNull String password) {

    }

    Observable saveOrUpdate(User user) {
        return Observable.create(
                new Observable.OnSubscribe<User>() {
                    @Override
                    public void call(Subscriber<? super User> sub) {
                        sub.onNext(saveUser());
                        sub.onCompleted();
                    }
                }
        ).subscribeOn(Schedulers.io());
    }

    private User saveUser() {
        // 保存用户 要么就数据库要么就prefs 别乱几把早的啦
        return null;
    }

    /**
     * load data from server
     */
    User load() {
        return null;
    }

    /**
     * pull data from database
     */
    User pull() {
        return null;
    }

    public interface LoginCallback {

        void onSucceed();

        void onError(String message);

        void onFailed(String message);
    }

    public interface RegisterCallback {
        void onSucceed();

        void onFailed(String message);

        void onVolleyError(String message);

        void onParseError(String message);
    }
}
