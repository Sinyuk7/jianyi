package com.sinyuk.jianyimaterial.model;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sinyuk.jianyimaterial.R;
import com.sinyuk.jianyimaterial.api.JLoginError;
import com.sinyuk.jianyimaterial.api.JUser;
import com.sinyuk.jianyimaterial.api.JianyiApi;
import com.sinyuk.jianyimaterial.application.Jianyi;
import com.sinyuk.jianyimaterial.events.UserStateUpdateEvent;
import com.sinyuk.jianyimaterial.events.XUserLoginEvent;
import com.sinyuk.jianyimaterial.events.XUserUpdateEvent;
import com.sinyuk.jianyimaterial.greendao.dao.DaoUtils;
import com.sinyuk.jianyimaterial.greendao.dao.UserService;
import com.sinyuk.jianyimaterial.utils.DialogUtils;
import com.sinyuk.jianyimaterial.utils.PreferencesUtils;
import com.sinyuk.jianyimaterial.utils.StringUtils;
import com.sinyuk.jianyimaterial.volley.FormDataRequest;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;
import java.util.Map;

/**
 * MVP重构
 * Created by Sinyuk on 16.3.8.
 */

@SuppressWarnings("unused")
public class UserModel {
    private volatile static UserModel instance = null;
    public static final String LOGIN_REQUEST = "user_login";
    public static final String UPDATE_REQUEST = "user_update";


    private final Context mContext;
    private final UserService userService;
    private User currentUser;
    private ProgressDialog progressDialog;


    public UserModel(Context context) {
        this.mContext = context;
        userService = DaoUtils.getUserService();
        currentUser = (User) userService.query(
                PreferencesUtils.getString(mContext, StringUtils.getResString(mContext, R.string.key_user_id)));
        EventBus.getDefault().register(this);
    }

    public static UserModel getInstance(Context context) {
        if (instance == null) {
            synchronized (UserModel.class) {
                if (instance == null) {
                    instance = new UserModel(context);
                }
            }
        }
        return instance;
    }

    /**
     * 获取当前的用户
     */
    public User getCurrentUser() {
        return currentUser;
    }

    /**
     * @param user update  user info
     */
    public void setCurrentUser(User user) {
        this.currentUser = user;
        PreferencesUtils.putString(mContext, StringUtils.getResString(mContext, R.string.key_user_id), user.getId());
        PreferencesUtils.putBoolean(mContext, StringUtils.getResString(mContext, R.string.key_login_state), true);
        PreferencesUtils.putString(mContext, StringUtils.getResString(mContext, R.string.key_psw), user.getId());

        // 发送
        EventBus.getDefault().post(new UserStateUpdateEvent(true, user.getId()));
    }

    /**
     * 异步的更新用户的信息
     */
    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onEvent(XUserUpdateEvent event) {
        this.currentUser = event.getUser();

    }


    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onUserLogin(XUserLoginEvent event) {
        final String phoneNumber = event.getPhoneNumber();
        final String password = event.getPassword();

        progressDialog = DialogUtils.simpleProgressDialog(mContext, null, "正在登录", new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                Jianyi.getInstance().cancelPendingRequest(LOGIN_REQUEST);
            }
        });

        progressDialog.show();

        FormDataRequest jsonRequest = new FormDataRequest(Request.Method.POST, JianyiApi.login(), new Response.Listener<String>() {
            @Override
            public void onResponse(String str) {
                final Gson gson = new Gson();
                JsonParser parser = new JsonParser();
                final JsonObject response = parser.parse(str).getAsJsonObject();
                JUser jsonData = gson.fromJson(response, JUser.class);
                // 转换成我的Model
                JUser.Data data = jsonData.getData();
                String trans = gson.toJson(data);
                User userData = gson.fromJson(trans, User.class);
                if (userData != null) {
                    loginSucceed();
                } else {
                    JLoginError error = gson.fromJson(response, JLoginError.class);
                    loginFailed(error);
                    Jianyi.getInstance().cancelPendingRequest(LOGIN_REQUEST);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loginFailed(error);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("tel", phoneNumber);
                params.put("password", password);
                return params;
            }
        };

        Jianyi.getInstance().addRequest(jsonRequest, LOGIN_REQUEST);
    }


    private void loginFailed(Object error) {
        if (null != progressDialog)
            progressDialog.cancel();

    }

    // 保存用户信息
    private void loginSucceed() {
    }

}
