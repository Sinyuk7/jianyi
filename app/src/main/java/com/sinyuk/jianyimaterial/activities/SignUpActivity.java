package com.sinyuk.jianyimaterial.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TextInputLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sinyuk.jianyimaterial.R;
import com.sinyuk.jianyimaterial.api.JResponse;
import com.sinyuk.jianyimaterial.api.JUser;
import com.sinyuk.jianyimaterial.api.JianyiApi;
import com.sinyuk.jianyimaterial.application.Jianyi;
import com.sinyuk.jianyimaterial.base.BaseActivity;
import com.sinyuk.jianyimaterial.greendao.dao.DaoUtils;
import com.sinyuk.jianyimaterial.greendao.dao.UserService;
import com.sinyuk.jianyimaterial.managers.SnackBarFactory;
import com.sinyuk.jianyimaterial.entity.User;
import com.sinyuk.jianyimaterial.ui.SimpleTextWatcher;
import com.sinyuk.jianyimaterial.utils.DialogUtils;
import com.sinyuk.jianyimaterial.utils.FormatUtils;
import com.sinyuk.jianyimaterial.utils.PreferencesUtils;
import com.sinyuk.jianyimaterial.utils.StringUtils;
import com.sinyuk.jianyimaterial.utils.ToastUtils;
import com.sinyuk.jianyimaterial.volley.FormDataRequest;
import com.sinyuk.jianyimaterial.volley.VolleyErrorHelper;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * 分为注册和忘记密码
 */
public class SignUpActivity extends BaseActivity {
    public static final int REQUEST_REGISTER = 0;
    public static final String REQUEST_TYPE = "request_type";
    public static final int REQUEST_FORGET_PSW = 1;

    private static final String VOLLEY_REQUEST = "register_request";


    private int requestType;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.app_bar_layout)
    AppBarLayout appBarLayout;
    @Bind(R.id.phone_number_et)
    EditText phoneNumberEt;
    @Bind(R.id.phone_number_text_layout)
    TextInputLayout phoneNumberTextLayout;
    @Bind(R.id.authenticode_et)
    EditText authenticodeEt;
    @Bind(R.id.get_authenticode_btn)
    Button getAuthenticodeBtn;
    @Bind(R.id.authenticode_text_layout)
    LinearLayout authenticodeTextLayout;
    @Bind(R.id.password_et)
    EditText passwordEt;
    @Bind(R.id.password_text_layout)
    TextInputLayout passwordTextLayout;
    @Bind(R.id.register_btn)
    Button registerBtn;
    @Bind(R.id.nested_scroll_view)
    NestedScrollView nestedScrollView;
    @Bind(R.id.coordinator_layout)
    CoordinatorLayout coordinatorLayout;

    private ProgressDialog progressDialog;
    private User userData;


    @Override
    protected void beforeSetContentView(Bundle savedInstanceState) {
        requestType = getIntent().getIntExtra(REQUEST_TYPE, 0);
    }

    @Override
    protected int getContentViewID() {
        return R.layout.activity_sign_up;
    }

    @Override
    protected boolean isNavAsBack() {
        return true;
    }

    @Override
    protected boolean isUsingEventBus() {
        return false;
    }

    @Override
    protected void initViews() {
        updateToolbar();
        setupEditText();
        toggleAuthenticodeBtn(false);
        toggleSignInButton(false);
        allowPasswordInput(false);
    }


    private void updateToolbar() {
        if (null != getSupportActionBar()) {
            if (requestType == REQUEST_REGISTER) {
                getSupportActionBar().setTitle("注册");
            } else {
                getSupportActionBar().setTitle("设置密码");
            }
        }
    }

    @Override
    protected void initData() {

    }

    @OnClick(R.id.register_btn)
    public void attemptRegister() {
        // Cancel focus
        passwordEt.setFocusableInTouchMode(false);
        passwordEt.setFocusable(false);

        phoneNumberEt.setFocusableInTouchMode(false);
        phoneNumberEt.setFocusable(false);

        // Reset errors.
        passwordEt.setError(null);
        phoneNumberEt.setError(null);

        // Store values at the time of the login attempt.
        String phoneNumber = phoneNumberEt.getText().toString();
        String password = passwordEt.getText().toString();

        boolean cancel = false;

        if (TextUtils.isEmpty(password)) {
            // TODO: Check for a valid password, if the user entered one.
            passwordEt.setError("密码不能为空");
            cancel = true;
        } else if (!FormatUtils.isPasswordValid(password)) {
            passwordEt.setError("密码必须大于6位");
            cancel = true;
        }

        if (cancel) {
            // Reset focus
            passwordEt.setFocusableInTouchMode(true);
            passwordEt.setFocusable(true);

            phoneNumberEt.setFocusableInTouchMode(true);
            phoneNumberEt.setFocusable(true);
        } else {

            hideSoftInput();
            createProgressDialog();
            progressDialog.show();
            startPostTask(phoneNumber, password);
        }
    }

    private void startPostTask(final String phoneNumber, final String password) {
        FormDataRequest jsonRequest = new FormDataRequest(Request.Method.POST, JianyiApi.register(), new Response.Listener<String>() {
            @Override
            public void onResponse(String str) {
                final Gson gson = new Gson();
                JsonParser parser = new JsonParser();
                final JsonObject response = parser.parse(str).getAsJsonObject();
                JUser jUser = gson.fromJson(response, JUser.class);

                // 转换成我的Model;
                if (jUser != null) {
                    String trans = gson.toJson(jUser.getData());
                    userData = gson.fromJson(trans, User.class);
                    if (null != userData)
                        registerSucceed(phoneNumber, password);
                } else {
                    JResponse jResponse = gson.fromJson(response, JResponse.class);
                    if (jResponse != null)
                        registerFailed(jResponse.getData());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                registerFailed(error);
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

        Jianyi.getInstance().addRequest(jsonRequest, VOLLEY_REQUEST);
    }

    private void registerFailed(VolleyError error) {
        if (null != progressDialog)
            progressDialog.dismiss();
        ToastUtils.toastSlow(mContext, VolleyErrorHelper.getMessage(error));
    }

    private void registerFailed(String message) {
        if (null != progressDialog)
            progressDialog.dismiss();
        if (null == message)
            message = "注册失败";
        SnackBarFactory.errorWithHelp(mContext, coordinatorLayout, message).show();
    }

    private void registerSucceed(String phoneNumber, String password) {
        PreferencesUtils.putBoolean(mContext, StringUtils.getRes(mContext, R.string.key_login_state), true);
        PreferencesUtils.putString(mContext, StringUtils.getRes(mContext, R.string.key_user_id), userData.getId());
        PreferencesUtils.putString(mContext, StringUtils.getRes(mContext, R.string.key_psw), password);
        UserService userService = DaoUtils.getUserService();
        userService.save(userData);


        if (null != progressDialog)
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    progressDialog.dismiss();
                    startActivity(new Intent(SignUpActivity.this, RegisterSettings.class));
                    finish();
                }
            }, 400);
    }

    private void createProgressDialog() {
        progressDialog = DialogUtils.simpleProgressDialog(mContext, null, "正在注册", new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                ToastUtils.toastSlow(mContext, "注册取消");
                Jianyi.getInstance().cancelPendingRequest(VOLLEY_REQUEST);
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
        phoneNumberEt.addTextChangedListener(new PhoneNumberWatcher());
        authenticodeEt.addTextChangedListener(new AuthenticodeWatcher());
        passwordEt.addTextChangedListener(new PasswordWatcher());

        passwordEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                // TODO: 当按下 enter 键的时候 尝试登录
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    attemptRegister();
                }
                return false;
            }
        });

    }


    // <--toggle UI state by the my logic -->
    private void toggleSignInButton(boolean enable) {
        registerBtn.setEnabled(enable);
        if (enable) {
            registerBtn.setBackground(getResources().getDrawable(R.drawable.rounded_rect_fill_accent));
        } else {
            registerBtn.setBackground(getResources().getDrawable(R.drawable.rounded_rect_fill_grey));
        }

    }


    private void toggleAuthenticodeBtn(boolean enable) {
        getAuthenticodeBtn.setEnabled(enable);
        if (enable) {
            getAuthenticodeBtn.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        } else {
            getAuthenticodeBtn.setBackgroundColor(getResources().getColor(R.color.grey_300));
        }
    }


    private void allowPasswordInput(boolean allowed) {
        passwordEt.setCursorVisible(allowed);
        passwordEt.setFocusableInTouchMode(allowed);
        passwordEt.setEnabled(allowed);

    }

    // <--toggle UI state by the my logic -->


    @OnClick(R.id.get_authenticode_btn)
    public void acquireAuthenticode() {
        new CountDownTimer(30000, 1000) {
            // 第一个参数是总的倒计时时间
            // 第二个参数是每隔多少时间(ms)调用一次onTick()方法
            public void onTick(long millisUntilFinished) {
                toggleAuthenticodeBtn(false);
                getAuthenticodeBtn.setText(millisUntilFinished / 1000 + "s");
            }

            public void onFinish() {
                getAuthenticodeBtn.setText("获取");
                toggleAuthenticodeBtn(true);
                checkAuthenticode();
            }


        }.start();
    }

    // TODO: 应该用broadcast来接受sms 然后post 一个event包含验证码
    // 这里直接判断为正确
    private void checkAuthenticode() {
        allowPasswordInput(true);
    }

    /**
     * Watching the user input on Phone number edit text view
     * <p>
     * To activate authenticode input when phone number is valid
     */
    private class PhoneNumberWatcher extends SimpleTextWatcher {
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            toggleAuthenticodeBtn(FormatUtils.isPhoneNumberValid(s.toString()));

        }
    }

    /**
     * Watching the user input on Authenticode edit text view
     * <p>
     * To activate password input when authenticode is valid
     */
    private class AuthenticodeWatcher extends SimpleTextWatcher {

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            allowPasswordInput(!TextUtils.isEmpty(s));
        }

    }

    /**
     * Watching the user input on Password edit text view
     * <p>
     * To activate confirm button when password is valid
     */
    private class PasswordWatcher extends SimpleTextWatcher {

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            toggleSignInButton(!TextUtils.isEmpty(s));
        }

    }
}
