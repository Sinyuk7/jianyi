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
import com.sinyuk.jianyimaterial.api.JError;
import com.sinyuk.jianyimaterial.api.JPostResponse;
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
import com.sinyuk.jianyimaterial.utils.LogUtils;
import com.sinyuk.jianyimaterial.utils.PreferencesUtils;
import com.sinyuk.jianyimaterial.volley.FormDataRequest;
import com.sinyuk.jianyimaterial.volley.VolleyErrorHelper;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.LinkedHashMap;
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
    public static final String POST_GOODS = "post_goods";
    public static final String POST_NEED = "post_need";
    public static final String UNSHELF = "unshelf";
    public static final String ONSHELF = "onShelf";

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
        return PreferencesUtils.getBoolean(mContext, Constants.Prefs_Login_State);
    }

    /**
     * this may block UI thread
     *
     * @return current user if has logged in
     */
    public void queryCurrentUser(QueryCurrentUserCallback callback) {
        String uId = PreferencesUtils.getString(mContext, Constants.Prefs_Uid);
        if (TextUtils.isEmpty(uId)) {
            callback.onUserNotLogged();
            return;
        }
        Observable.create(new Observable.OnSubscribe<User>() {
            @Override
            public void call(Subscriber<? super User> subscriber) {
                if (mUserService.query(uId) != null) {
                    subscriber.onNext((User) mUserService.query(uId));
                    subscriber.onCompleted();
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
            User userData = jsonData.getData();
            if (userData != null) {
                // TODO: 这里应该保存数据 然后保存成功在回调onSucceed();
                saveOrUpdate(userData, password);
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
    private void saveOrUpdate(User userData, String password) {
        mUserService.saveOrUpdate(userData);
        if (null != mCurrentUser) {
            if (!userData.getId().equals(mCurrentUser.getId())) {
                PreferencesUtils.clearAll(mContext); // a new user has login  clean up the prefs
            }
        }

        PreferencesUtils.putString(mContext, Constants.Prefs_Uid, userData.getId());
        PreferencesUtils.putBoolean(mContext, Constants.Prefs_Login_State, true);
        // post event first  in case they clean up the prefs
        PreferencesUtils.putString(mContext, Constants.Prefs_Psw, password);
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
                    .map(JUser::getData)
                    .doOnError(error -> callback.onRegisterParseError(error.getMessage()))
                    .subscribe(user -> {
                        if (user != null) {
                            saveOrUpdate(user, password);
                            callback.onRegisterSucceed(user);
                        } else {
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
            callback.onUserUpdateFailed("读取用户信息失败");
            return;
        }

        final String tel = mCurrentUser.getTel();
        final String password = PreferencesUtils.getString(mContext, Constants.Prefs_Psw);
        FormDataRequest jsonRequest = new FormDataRequest(Request.Method.POST, JianyiApi.updateUser(), (Response.Listener<String>) str -> {
            try {
                JsonParser parser = new JsonParser();
                final JsonObject response = parser.parse(str).getAsJsonObject();
                JUser jsonData = mGson.fromJson(response, JUser.class);
                User userData = jsonData.getData();
                if (userData != null) {
                    saveOrUpdate(userData, password);
                    callback.onUserUpdateSucceed("更新用户信息成功");
                } else {
                    callback.onUserUpdateParseError("更新用户信息失败");
                }
            } catch (Exception e) {
                callback.onUserUpdateParseError("更新用户信息失败");
            }
        }, (Response.ErrorListener) error -> callback.onUserUpdateVolleyError(VolleyErrorHelper.getMessage(error))) {
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

    public void postGoods(HashMap<String, String> urls, String title, String details, String price, String sort, String childSort, PostGoodsCallback callback) {
        final String tel = mCurrentUser.getTel();
        final String password = PreferencesUtils.getString(mContext, Constants.Prefs_Psw);

        if (TextUtils.isEmpty(tel) || TextUtils.isEmpty(password)) {
            callback.onPostGoodsFailed("读取用户信息失败");
            return;
        }
        FormDataRequest postRequest = new FormDataRequest
                (Request.Method.POST, JianyiApi.postFeed(), (Response.Listener<String>) callback::onPostGoodsSucceed,
                        (Response.ErrorListener) error -> callback.onPostGoodsVolleyError(VolleyErrorHelper.getMessage(error))) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                LinkedHashMap<String, String> params = new LinkedHashMap<>();
                params.put("tel", tel);
                params.put("password", password);
                params.put("name", title);
                params.put("title", sort);
                params.put("detail", details);
                params.put("price", price);
                params.put("sort", childSort);
                if (!TextUtils.isEmpty(urls.get("1"))) { params.put("pic[0]", urls.get("1")); }
                if (!TextUtils.isEmpty(urls.get("2"))) { params.put("pic[1]", urls.get("2")); }
                if (!TextUtils.isEmpty(urls.get("3"))) { params.put("pic[2]", urls.get("3")); }
                LogUtils.simpleLog(UserModel.class, params.toString());
                return params;
            }
        };

        Jianyi.getInstance().addRequest(postRequest, POST_GOODS);
    }

    /**
     * 发布一个需求
     *
     * @param detail 内容
     * @param tel    联系方式
     * @param price  价格
     */
    public void postNeed(String detail, String tel, String price, PostNeedCallback callback) {
        final String password = PreferencesUtils.getString(mContext, Constants.Prefs_Psw);

        if (TextUtils.isEmpty(password)) {
            callback.onPostNeedParseError("读取用户信息失败");
            return;
        }
        FormDataRequest formDataRequest = new FormDataRequest(Request.Method.POST, JianyiApi.postNeeds(), (Response.Listener<String>) str -> {
            try {
                JsonParser parser = new JsonParser();
                final JsonObject response = parser.parse(str).getAsJsonObject();
                JPostResponse result = mGson.fromJson(response, JPostResponse.class);
                if (result.getCode() == 2001) {
                    callback.onPostNeedSucceed();
                } else {
                    JError error = mGson.fromJson(response, JError.class);
                    callback.onPostNeedFailed(error.getError_msg());
                }
            } catch (Exception e) {
                e.printStackTrace();
                callback.onPostNeedParseError(e.getMessage());
            }
        }, (Response.ErrorListener) error -> callback.onPostNeedVolleyError(VolleyErrorHelper.getMessage(error))) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("tel", tel);
                params.put("password", password);
                params.put("detail", detail);
                params.put("price", price);
                return params;
            }
        };

        Jianyi.getInstance().addRequest(formDataRequest, POST_NEED);
    }

    public void unShelf(@NonNull String gid, @NonNull String reason, UnShelfCallback callback) {
        final String uid = PreferencesUtils.getString(mContext, Constants.Prefs_Uid);
        if (TextUtils.isEmpty(uid)) { callback.onUnShelfFailed("读取用户信息失败"); }
        FormDataRequest formDataRequest = new FormDataRequest(Request.Method.POST, JianyiApi.unShelf(), (Response.Listener<String>) str -> {
            try {
                JsonParser parser = new JsonParser();
                final JsonObject response = parser.parse(str).getAsJsonObject();
                JPostResponse result = mGson.fromJson(response, JPostResponse.class);
                if (result.getCode() == 2001) {
                    callback.onUnShelfSucceed();
                } else {
                    JError error = mGson.fromJson(response, JError.class);
                    callback.onUnShelfFailed(error.getError_msg());
                }
            } catch (Exception e) {
                e.printStackTrace();
                callback.onUnShelfParseError(e.getMessage());
            }
        }, (Response.ErrorListener) error -> callback.onUnShelfVolleyError(VolleyErrorHelper.getMessage(error))) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", uid);
                params.put("id", gid);
                params.put("reason", reason);
                return params;
            }
        };

        Jianyi.getInstance().addRequest(formDataRequest, UNSHELF);
    }

    public void onShelf(@NonNull String gid, OnShelfCallback callback) {
        final String uid = PreferencesUtils.getString(mContext, Constants.Prefs_Uid);
        if (TextUtils.isEmpty(uid)) { callback.onOnShelfFailed("读取用户信息失败"); }
        FormDataRequest formDataRequest = new FormDataRequest(Request.Method.POST, JianyiApi.onShelf(), (Response.Listener<String>) str -> {
            try {
                JsonParser parser = new JsonParser();
                final JsonObject response = parser.parse(str).getAsJsonObject();
                JPostResponse result = mGson.fromJson(response, JPostResponse.class);
                if (result.getCode() == 2001) {
                    callback.onOnShelfSucceed();
                } else {
                    JError error = mGson.fromJson(response, JError.class);
                    callback.onOnShelfParseError(error.getError_msg());
                }
            } catch (Exception e) {
                e.printStackTrace();
                callback.onOnShelfParseError(e.getMessage());
            }
        }, (Response.ErrorListener) error -> callback.onOnShelfVolleyError(VolleyErrorHelper.getMessage(error))) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", uid);
                params.put("id", gid);
                return params;
            }
        };

        Jianyi.getInstance().addRequest(formDataRequest, ONSHELF);
    }

    public interface LoginCallback {

        void onLoginSucceed();

        void onLoginError(String message);

        void onLoginFailed(String message);
    }

    public interface RegisterCallback {
        void onRegisterSucceed(User user);

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
        void onUserUpdateSucceed(String message);

        void onUserUpdateFailed(String message);

        void onUserUpdateVolleyError(String message);

        void onUserUpdateParseError(String message);
    }

    public interface PostGoodsCallback {

        void onPostGoodsSucceed(String message);

        void onPostGoodsFailed(String message);

        void onPostGoodsVolleyError(String message);

        void onUPostGoodsParseError(String message);
    }


    public interface PostNeedCallback {

        void onPostNeedSucceed();

        void onPostNeedFailed(String message);

        void onPostNeedVolleyError(String message);

        void onPostNeedParseError(String message);
    }

    public interface UnShelfCallback {

        void onUnShelfSucceed();

        void onUnShelfFailed(String message);

        void onUnShelfVolleyError(String message);

        void onUnShelfParseError(String message);
    }

    public interface OnShelfCallback {

        void onOnShelfSucceed();

        void onOnShelfFailed(String message);

        void onOnShelfVolleyError(String message);

        void onOnShelfParseError(String message);
    }

}
