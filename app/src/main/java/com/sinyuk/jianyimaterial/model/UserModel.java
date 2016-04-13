package com.sinyuk.jianyimaterial.model;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sinyuk.jianyimaterial.R;
import com.sinyuk.jianyimaterial.api.JError;
import com.sinyuk.jianyimaterial.api.JResponse;
import com.sinyuk.jianyimaterial.api.JUser;
import com.sinyuk.jianyimaterial.api.JianyiApi;
import com.sinyuk.jianyimaterial.application.Jianyi;
import com.sinyuk.jianyimaterial.common.Constants;
import com.sinyuk.jianyimaterial.entity.User;
import com.sinyuk.jianyimaterial.events.XLoginEvent;
import com.sinyuk.jianyimaterial.events.XLogoutEvent;
import com.sinyuk.jianyimaterial.greendao.dao.DaoUtils;
import com.sinyuk.jianyimaterial.greendao.dao.UserService;
import com.sinyuk.jianyimaterial.mvp.BaseModel;
import com.sinyuk.jianyimaterial.utils.PreferencesUtils;
import com.sinyuk.jianyimaterial.utils.StringUtils;
import com.sinyuk.jianyimaterial.volley.FormDataRequest;
import com.sinyuk.jianyimaterial.volley.VolleyErrorHelper;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.Map;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
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
    public void queryCurrentUser(QueryCurrentUserCallback callback) {
        String uId = PreferencesUtils.getString(mContext, StringUtils.getRes(mContext, R.string.key_user_id));
        if (TextUtils.isEmpty(uId)) {
            callback.onUserNotLogged();
            return;
        }
        Observable.create(new Observable.OnSubscribe<User>() {
            @Override
            public void call(Subscriber<? super User> subscriber) {
                if (mUserService.query(uId) != null) {
                    subscriber.onNext((User) mUserService.query(uId));
                } else {
                    subscriber.onError(new Throwable("读取用户信息失败"));
                }

            }
        }).subscribeOn(Schedulers.computation())
                .doOnError(throwable -> callback.onQueryFailed(throwable.getMessage()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(user -> {
                    mCurrentUser = user;
                    callback.onQuerySucceed(user);
                });
    }

    /**
     * 登录
     *
     * @param tel      账号
     * @param password 密码
     * @param callback 回调
     */
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
                callback.onLoginSucceed();
            } else {
                JError error = mGson.fromJson(response, JError.class);
                callback.onLoginFailed(error.getError_msg());
            }
        }, (Response.ErrorListener) error -> callback.onLoginError(VolleyErrorHelper.getMessage(error))) {
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

    /**
     * TODO: 早晚直接用prefs的key 而不是这样做
     * 登录成功之后保存信息
     *
     * @param userData
     * @param password
     */
    private void registerSucceed(User userData, String password) {
        mUserService.saveOrUpdate(userData);
        if (null != mCurrentUser) {
            if (!userData.getId().equals(mCurrentUser.getId())) {
                PreferencesUtils.clearAll(mContext); // a new user has login  clean up the prefs
            }
        }

        PreferencesUtils.putString(mContext, StringUtils.getRes(mContext, R.string.key_user_id), userData.getId());
        PreferencesUtils.putBoolean(mContext, StringUtils.getRes(mContext, R.string.key_login_state), true);
        // post event first  in case they clean up the prefs
        PreferencesUtils.putString(mContext, StringUtils.getRes(mContext, R.string.key_psw), password);
        // 发送事件
        EventBus.getDefault().post(new XLoginEvent());

    }

    /**
     * 注册
     *
     * @param tel
     * @param password
     * @param callback
     */
    public void register(@NonNull String tel, @NonNull String password, RegisterCallback callback) {
        FormDataRequest jsonRequest = new FormDataRequest(Request.Method.POST, JianyiApi.register(), (Response.Listener<String>) str -> {
            Observable.just(str)
                    .map(responseStr -> new JsonParser().parse(responseStr).getAsJsonObject())
                    .map(jsonObject -> mGson.fromJson(jsonObject, JUser.class))
                    .map(jUser -> mGson.toJson(jUser.getData()))
                    .map(trans -> mGson.fromJson(trans, User.class))
                    .doOnError(error -> callback.onRegisterParseError(error.getMessage()))
                    .subscribe(user -> {
                        if (user != null) {callback.onRegisterSucceed();} else {
                            Observable.just(str)
                                    .map(responseStr -> new JsonParser().parse(responseStr).getAsJsonObject())
                                    .map(response -> mGson.fromJson(response, JError.class))
                                    .map(JError::getError_msg)
                                    .doOnError(error -> callback.onRegisterParseError(error.getMessage()))
                                    .subscribe(callback::onRegisterFailed);
                        }
                    });

        }, (Response.ErrorListener) error -> callback.onRegisterVolleyError(VolleyErrorHelper.getMessage(error))) {
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
     *
     * @param tel
     */
    public void askForAuthenticode(@NonNull String tel) {

    }

    /**
     * 验证验证码
     *
     * @param tel
     * @param authenticode
     * @param callback
     */
    public void checkAuthenticode(@NonNull String tel, @NonNull String authenticode, AuthenticateCallback callback) {
        if (authenticode.equals("123456")) {
            callback.onAuthenticateSucceed();
        } else {
            callback.onAuthenticateFailed("验证码错误");
        }
    }

    /**
     * 登出
     */
    public void logout() {
        mUserService.deleteAll();
        PreferencesUtils.clearAll(mContext); // a new user has login  clean up the prefs
        EventBus.getDefault().post(new XLogoutEvent());
    }

    public void update(Map<String, String> params, UserUpdateCallback callback) {
        if (TextUtils.isEmpty(mCurrentUser.getTel()) || TextUtils.isEmpty(PreferencesUtils.getString(mContext, Constants.Prefs_Psw))) {
            callback.onUpdateFailed("读取用户信息失败");
            return;
        }

        final String tel = mCurrentUser.getTel();
        final String password = PreferencesUtils.getString(mContext, Constants.Prefs_Psw);
        FormDataRequest jsonRequest = new FormDataRequest(Request.Method.POST, JianyiApi.updateUser(), (Response.Listener<String>) str -> {

            Observable.just(str)
                    .map(responseStr -> new JsonParser().parse(responseStr).getAsJsonObject())
                    .map(jsonObject -> mGson.fromJson(jsonObject, JResponse.class))
                    .map(jResponse -> {
                        if (jResponse != null && jResponse.getCode() == JResponse.CODE_SUCCEED) {
                            return jResponse.getData();
                        } else {
                            return "更新用户信息失败";
                        }
                    })
                    .doOnError(throwable -> callback.onUpdateParseError(throwable.getMessage()))
                    .subscribe(callback::onUpdateSucceed);
        }, (Response.ErrorListener) error -> callback.onUpdateVolleyError(VolleyErrorHelper.getMessage(error))) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> body = new HashMap<>();
                body.put("tel", tel);
                body.put("password", password);
                body.putAll(params);
                return body;
            }
        };

        Jianyi.getInstance().addRequest(jsonRequest, UPDATE_REQUEST);
    }

    public interface LoginCallback {

        void onLoginSucceed();

        void onLoginError(String message);

        void onLoginFailed(String message);
    }

    public interface RegisterCallback {
        void onRegisterSucceed();

        void onRegisterFailed(String message);

        void onRegisterVolleyError(String message);

        void onRegisterParseError(String message);
    }

    public interface AuthenticateCallback {
        void onAuthenticateSucceed();

        void onAuthenticateFailed(String message);

        void onAuthenticateVolleyError(String message);

        void onAuthenticateParseError(String message);
    }

    public interface QueryCurrentUserCallback {
        void onQuerySucceed(User currentUser);

        void onQueryFailed(String message);

        void onUserNotLogged();
    }

    public interface UserUpdateCallback {
        void onUpdateSucceed(String message);

        void onUpdateFailed(String message);

        void onUpdateVolleyError(String message);

        void onUpdateParseError(String message);
    }


}
