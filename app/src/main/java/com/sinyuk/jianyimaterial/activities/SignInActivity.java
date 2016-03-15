package com.sinyuk.jianyimaterial.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TextInputLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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
import com.sinyuk.jianyimaterial.base.BaseActivity;
import com.sinyuk.jianyimaterial.greendao.dao.DaoUtils;
import com.sinyuk.jianyimaterial.greendao.dao.UserService;
import com.sinyuk.jianyimaterial.managers.SnackBarFactory;
import com.sinyuk.jianyimaterial.model.User;
import com.sinyuk.jianyimaterial.utils.DialogUtils;
import com.sinyuk.jianyimaterial.utils.PreferencesUtils;
import com.sinyuk.jianyimaterial.utils.StringUtils;
import com.sinyuk.jianyimaterial.volley.FormDataRequest;
import com.sinyuk.jianyimaterial.volley.VolleyErrorHelper;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by Sinyuk on 16.3.8.
 */
public class SignInActivity extends BaseActivity {
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.app_bar_layout)
    AppBarLayout appBarLayout;
    @Bind(R.id.user_name_et)
    EditText userNameEt;
    @Bind(R.id.user_name_input_area)
    TextInputLayout userNameInputArea;
    @Bind(R.id.password_et)
    EditText passwordEt;
    @Bind(R.id.password_input_area)
    TextInputLayout passwordInputArea;
    @Bind(R.id.forget_psw_tv)
    TextView forgetPswTv;
    @Bind(R.id.login_btn)
    Button loginButton;
    @Bind(R.id.access_wechat)
    TextView accessWechat;
    @Bind(R.id.nested_scroll_view)
    NestedScrollView nestedScrollView;
    @Bind(R.id.coordinator_layout)
    CoordinatorLayout coordinatorLayout;

    private ProgressDialog progressDialog;
    private User userData;

    @Override
    protected void beforeSetContentView(Bundle savedInstanceState) {

    }

    @Override
    protected int getContentViewID() {
        return R.layout.activity_sign_in;
    }

    @Override
    protected boolean isNavAsBack() {
        return true;
    }

    @Override
    protected boolean isUsingEventBus() {
        return true;
    }

    @Override
    protected void initViews() {
        toggleLoginButton(false);
        setupEditText();
    }

    @Override
    protected void initData() {

    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors , the errors are presented and no actual login attempt is made.
     */
    @OnClick(R.id.login_btn)
    public void attemptLogin() {
        // Reset errors.
        passwordEt.setError(null);
        userNameEt.setError(null);

        // Store values at the time of the login attempt.
        String userName = userNameEt.getText().toString();
        String password = passwordEt.getText().toString();

        boolean cancel = false;

        if (TextUtils.isEmpty(userName)) {
            // TODO: Check for a valid userName.
            userNameEt.setError("用户名不能为空");
            cancel = true;
        }

        if (TextUtils.isEmpty(password)) {
            // TODO: Check for a valid password, if the user entered one.
            passwordEt.setError("密码不能为空");
            cancel = true;
        }


        if (cancel) {
            // Reset focus

        } else {
            hideSoftInput();
            createProgressDialog();
            progressDialog.show();
            startUserLoginTask(userName, password);
        }
    }

    private void startUserLoginTask(final String userName, final String password) {
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
                userData = gson.fromJson(trans, User.class);
                if (userData != null) {
                    loginSucceed();
                } else {
                    JLoginError error = gson.fromJson(response, JLoginError.class);
                    loginFailed(error);
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
                params.put("tel", userName);
                params.put("password", password);
                return params;
            }
        };

        Jianyi.getInstance().addRequest(jsonRequest, User.LOGIN_REQUEST);
    }

    private void loginFailed(Object error) {
        Jianyi.getInstance().cancelPendingRequest(User.LOGIN_REQUEST);
        progressDialog.dismiss();

        if (error == null) {
            SnackBarFactory.loginFailed(mContext, coordinatorLayout, "登录失败").show();
        } else if (error instanceof JLoginError) {
            if (null != ((JLoginError) error).getError_msg()) {
                SnackBarFactory.loginFailed(mContext, coordinatorLayout, ((JLoginError) error).getError_msg()).show();
            } else {
                SnackBarFactory.loginFailed(mContext, coordinatorLayout, "登录失败").show();
            }

        } else if (error instanceof VolleyError) {
            SnackBarFactory.networkError(mContext, coordinatorLayout, VolleyErrorHelper.getMessage(error, mContext)).show();
        }

        // reset
    }

    // 这个是在子线程中进行的
    private void loginSucceed() {
        progressDialog.dismiss();
        UserService userService = DaoUtils.getUserService();
        userService.saveOrUpdate(userData);

//        EventBus.getDefault().post(new LoginEvent(true, userData.getId()));
        updatePrefs(userData.getId());
        // post event first  in case they clean up the prefs
        final String password = passwordEt.getText().toString();
        PreferencesUtils.putString(mContext, StringUtils.getResString(mContext, R.string.key_psw), password);

    }

    private void createProgressDialog() {
        progressDialog = DialogUtils.simpleProgressDialog(mContext, null, "正在登录", new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                SnackBarFactory.loginFailed(mContext, coordinatorLayout, "登录中断").show();
                Jianyi.getInstance().cancelPendingRequest(User.LOGIN_REQUEST);
            }
        });
    }

    @SuppressWarnings("ConstantConditions")
    private void hideSoftInput() {
        // TODO: 在登录的时候关闭软键盘
        final InputMethodManager inputMethodManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputMethodManager.isActive()) {

            inputMethodManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
        }
    }


    private void setupEditText() {
        /**
         * 只要有一个输入框获得焦点的时候 就把顶部的视图隐藏
         */
        passwordEt.addTextChangedListener(new PasswordWatcher());
        passwordEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                // TODO: 当按下 enter 键的时候 尝试登录
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    attemptLogin();
                }
                return false;
            }
        });

    }

    private void toggleLoginButton(boolean hasInput) {
        loginButton.setEnabled(hasInput);
        loginButton.setClickable(hasInput);
        if (hasInput) {
            loginButton.setBackground(mContext.getResources().getDrawable(R.drawable.rounded_rect_fill_accent));
        } else {
            loginButton.setBackground(mContext.getResources().getDrawable(R.drawable.rounded_rect_fill_grey));
        }
    }


    private class PasswordWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            toggleLoginButton(!TextUtils.isEmpty(s));
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    }

    // very important method !!!
    private void updatePrefs(String currentUser) {
        // TODO: 登录状态 登录时间 登录密码 prefs
        // log in
        final String lastUser = PreferencesUtils.getString(this, StringUtils.getResString(this, R.string.key_user_id));
        if (lastUser != null && !currentUser.equals(lastUser)) {
            PreferencesUtils.clearAll(this); // a new user has login  clean up the prefs
        }

        PreferencesUtils.putString(this, StringUtils.getResString(this, R.string.key_user_id), currentUser);
        PreferencesUtils.putBoolean(this, StringUtils.getResString(this, R.string.key_login_state), true);
        int loginTimes = PreferencesUtils.getInt(this, StringUtils.getResString(this, R.string.key_login_times), 0) + 1;
        PreferencesUtils.putInt(this, StringUtils.getResString(this, R.string.key_login_times), loginTimes);

    }

    /**
     * reset the password for user
     */
    @OnClick(R.id.forget_psw_tv)
    public void toResetPassword() {
        Intent intent = new Intent(SignInActivity.this, SignUpActivity.class);
        intent.putExtra(SignUpActivity.REQUEST_TYPE, SignUpActivity.REQUEST_FORGET_PSW);
        startActivity(intent);
        finish();
    }

    @OnClick(R.id.access_wechat)
    public void accessWithWechat(){
        Intent intent = new Intent(SignInActivity.this, SignUpActivity.class);
        intent.putExtra(SignUpActivity.REQUEST_TYPE, SignUpActivity.REQUEST_REGISTER);
        startActivity(intent);
        finish();
    }
}
