package com.sinyuk.jianyimaterial.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.widget.NestedScrollView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
import com.sinyuk.jianyimaterial.api.JPostError;
import com.sinyuk.jianyimaterial.api.JPostResponse;
import com.sinyuk.jianyimaterial.api.JianyiApi;
import com.sinyuk.jianyimaterial.application.Jianyi;
import com.sinyuk.jianyimaterial.base.BaseFragment;
import com.sinyuk.jianyimaterial.managers.SnackBarFactory;
import com.sinyuk.jianyimaterial.greendao.dao.DaoUtils;
import com.sinyuk.jianyimaterial.greendao.dao.UserService;
import com.sinyuk.jianyimaterial.model.User;
import com.sinyuk.jianyimaterial.utils.DialogUtils;
import com.sinyuk.jianyimaterial.utils.FormatUtils;
import com.sinyuk.jianyimaterial.utils.LogUtils;
import com.sinyuk.jianyimaterial.utils.PreferencesUtils;
import com.sinyuk.jianyimaterial.utils.StringUtils;
import com.sinyuk.jianyimaterial.volley.FormDataRequest;
import com.sinyuk.jianyimaterial.volley.VolleyErrorHelper;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Sinyuk on 16.2.18.
 */
public class PostNeedFragment extends BaseFragment {
    private static final int MAX_INPUT_COUNT = 40;
    private static final String POST_NEED_REQUEST = "post_need";
    private static PostNeedFragment instance;
    @Bind(R.id.want_content_et)
    EditText wantContentEt;
    @Bind(R.id.word_count_tv)
    TextView wordCountTv;
    @Bind(R.id.contact_info_et)
    EditText contactInfoEt;
    @Bind(R.id.contact_info_input_area)
    TextInputLayout contactInfoInputArea;
    @Bind(R.id.want_price_et)
    EditText wantPriceEt;
    @Bind(R.id.want_price_input_area)
    TextInputLayout wantPriceInputArea;
    @Bind(R.id.post_btn)
    Button postBtn;
    @Bind(R.id.nested_scroll_view)
    NestedScrollView nestedScrollView;

    private String tel;
    private int schoolIndex;
    private String password;


    private ProgressDialog progressDialog;

    public static PostNeedFragment getInstance() {
        if (instance == null) {
            instance = new PostNeedFragment();
        }
        return instance;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        final String uId = PreferencesUtils.getString(mContext, StringUtils.getResString(mContext, R.string.key_user_id));
        if (uId != null) {
            UserService userService = DaoUtils.getUserService();
            User currentUser = (User) userService.query(uId);

            if (currentUser.getSchool() != null) {
                schoolIndex = Integer.parseInt(currentUser.getSchool());
                schoolIndex = schoolIndex - 1;// wocao he begin the array index with 1 can't believe that !
            } else {
                schoolIndex = -1;
            }

            tel = currentUser.getTel();

            password = PreferencesUtils.getString(mContext, StringUtils.getResString(mContext, R.string.key_psw));

        }
    }

    @Override
    protected int getContentViewId() {
        return R.layout.fragment_post_need;
    }

    @Override
    protected boolean isUsingEventBus() {
        return true;
    }

    @Override
    protected void initViewsAndEvent() {
        setHasOptionsMenu(true);
//        setupLocation(schoolIndex);
        setupTel(tel);
        setupEditTextView();
    }

    private void setupTel(String tel) {
        contactInfoEt.setText(StringUtils.getSweetString(mContext, tel, R.string.unknown_tel));
    }

//    private void setupLocation(int index) {
//        String location;
//        if (index >= 0 && index < mContext.getResources().getStringArray(R.array.schools_sort).length) {
//            location = mContext.getResources().getStringArray(R.array.schools_sort)[index];
//            if (!TextUtils.isEmpty(location))
//                locationEt.setText(location);
//        }
//    }

    private void setupEditTextView() {
        wantContentEt.addTextChangedListener(new TextWatcher() {

            private int editStart;

            private int editEnd;

            @Override
            public void afterTextChanged(Editable s) {
                editStart = wantContentEt.getSelectionStart();
                editEnd = wantContentEt.getSelectionEnd();

                // 先去掉监听器，否则会出现栈溢出

                wantContentEt.removeTextChangedListener(this);

                // 注意这里只能每次都对整个EditText的内容求长度，不能对删除的单个字符求长度
                // 因为是中英文混合，单个字符而言，calculateLength函数都会返回1
                while (calculateLength(s.toString()) > MAX_INPUT_COUNT) {
                    // 当输入字符个数超过限制的大小时，进行截断操作
                    s.delete(editStart - 1, editEnd);
                    editStart--;
                    editEnd--;
                }
                // mEditText.setText(s);
                // 将这行代码注释掉就不会出现后面所说的输入法在数字界面自动跳转回主界面的问题了，多谢@ainiyidiandian的提醒
                wantContentEt.setSelection(editStart);

                // 恢复监听器
                wantContentEt.addTextChangedListener(this);

                updateCounterText();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

        wantContentEt.setSelection(wantContentEt.length()); // 将光标移动最后一个字符后面
        updateCounterText();

    }

    private long calculateLength(CharSequence c) {
        double len = 0;
        for (int i = 0; i < c.length(); i++) {
            int tmp = (int) c.charAt(i);
            if (tmp > 0 && tmp < 127) {
                len += 0.5;
            } else {
                len++;
            }
        }
        return Math.round(len);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_post)
            attemptPost();
        return true;
    }

    private void attemptPost() {

        cancelFocus();

        // Reset errors.
        cancelErrorHint();
        // Store values at the time of the attempt.
        String content = wantContentEt.getText().toString();
//        String location = locationEt.getText().toString();
        String phoneNumber = contactInfoEt.getText().toString();
        String price = wantPriceEt.getText().toString();

        boolean cancel = false;


        if (TextUtils.isEmpty(content)) {
            // TODO: Check for a valid userName.
            wantContentEt.setError("内容不能为空");
            cancel = true;
        }

//        if (TextUtils.isEmpty(location)) {
//            locationEt.setError("地址不能为空");
//            cancel = true;
//        }

        if (TextUtils.isEmpty(phoneNumber)) {
            contactInfoEt.setError("号码不能为空");
            cancel = true;
        } else if (!FormatUtils.isPhoneNumberValid(phoneNumber)) {
            contactInfoEt.setError("号码不正确");
            cancel = true;
        }

        if (TextUtils.isEmpty(price)) {
            wantPriceEt.setError("价格不能为空");
            cancel = true;
        }

        if (cancel) {
            // Reset focus
            resetFocus();

        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            /**
             *  输入正确的话就开始登陆
             *  关闭软键盘
             *  取消输入框的焦点
             *  下拉顶部视图
             *  显示进度条
             *  异步任务
             */
            hideSoftInput();
            createProgressDialog();
            progressDialog.show();
            startPostTask(content, phoneNumber, price);
        }

    }

    private void startPostTask(final String content, final String phoneNumber, final String wantPrice) {
        FormDataRequest formDataRequest = new FormDataRequest(Request.Method.POST, JianyiApi.postNeeds(), new Response.Listener<String>() {
            @Override
            public void onResponse(String str) {
                final Gson gson = new Gson();
                JsonParser parser = new JsonParser();
                final JsonObject response = parser.parse(str).getAsJsonObject();
                JPostResponse result = gson.fromJson(response, JPostResponse.class);
                // 转换成我的Model
                int resultCode = result.getCode();

                if (resultCode == 2001) {
                    postSucceed();
                } else {
                    JPostError error = gson.fromJson(response, JPostError.class);
                    if (error != null)
                        postFailed(error);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                postFailed(error);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("tel", tel);
                params.put("password", password);
                params.put("detail", content);
                params.put("price", wantPrice);


                return params;
            }
        };

        Jianyi.getInstance().addRequest(formDataRequest, POST_NEED_REQUEST);

    }

    private void postSucceed() {
        progressDialog.dismiss();
        SnackBarFactory.succeedNoAction(mContext, getView(), "发布成功").show();
        getActivity().finish();
    }

    private void postFailed(Object error) {
        progressDialog.dismiss();
        // TODO: show hint

        if (error == null) {
            SnackBarFactory.loginFailed(mContext, getView(), "发送失败").show();
        } else if (error instanceof JPostError) {

            SnackBarFactory.loginFailed(mContext, getView(), "请重新登录认证").show();
            LogUtils.simpleLog(PostFeedFragment.class, "error log ->" + ((JPostError) error).getError_msg());

        } else if (error instanceof VolleyError) {
            SnackBarFactory.networkError(mContext, getView(), VolleyErrorHelper.getMessage(error, mContext)).show();
        }

        // reset
//        Jianyi.getInstance().cancelPendingRequest(POST_NEED_REQUEST);
        resetFocus();
    }

    private void createProgressDialog() {
        progressDialog = DialogUtils.simpleProgressDialog(mContext, null, "正在发布", new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                SnackBarFactory.loginFailed(mContext, getView(), "发布取消").show();
                resetFocus();
                // cancel the upload
//                Jianyi.getInstance().cancelPendingRequest(POST_NEED_REQUEST);
            }
        });
    }

    @SuppressWarnings("ConstantConditions")
    private void hideSoftInput() {
        // TODO: 在登录的时候关闭软键盘
        final InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputMethodManager.isActive()) {

            inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
        }
    }

    private void resetFocus() {
        postBtn.setEnabled(true);
        wantContentEt.setFocusableInTouchMode(true);
        wantContentEt.setFocusable(true);
//        locationEt.setEnabled(true);
        contactInfoEt.setFocusableInTouchMode(true);
        contactInfoEt.setFocusable(true);
        wantPriceEt.setFocusable(true);
        wantPriceEt.setFocusableInTouchMode(true);
    }

    private void cancelErrorHint() {
        wantContentEt.setError(null);
//        locationEt.setError(null);
        contactInfoEt.setError(null);
        wantPriceEt.setError(null);
    }

    private void cancelFocus() {
        postBtn.setEnabled(false);
        wantContentEt.setFocusableInTouchMode(false);
        wantContentEt.setFocusable(false);
//        locationEt.setEnabled(false);
        contactInfoEt.setFocusableInTouchMode(false);
        contactInfoEt.setFocusable(false);
        wantPriceEt.setFocusable(false);
        wantPriceEt.setFocusableInTouchMode(false);
    }

    private void updateCounterText() {
        wordCountTv.setText(String.valueOf((MAX_INPUT_COUNT - getInputCount())));
    }

    private long getInputCount() {
        return calculateLength(wantContentEt.getText().toString());
    }

    @OnClick(R.id.post_btn)
    public void onClick() {
        attemptPost();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: inflate a fragment view
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

//    @OnClick(R.id.location_et)
//    public void onSelectLocation() {
//        LocationSelectDialog dialog = new LocationSelectDialog();
//        dialog.show(getChildFragmentManager(), LocationSelectDialog.TAG);
//
//    }

//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void onLocationSelected(LocationSelectEvent event) {
//        setupLocation(event.getWhich());
//
//    }


}
