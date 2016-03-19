package com.sinyuk.jianyimaterial.fragments;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sinyuk.jianyimaterial.R;
import com.sinyuk.jianyimaterial.api.JResponse;
import com.sinyuk.jianyimaterial.api.JUploadResponse;
import com.sinyuk.jianyimaterial.api.JianyiApi;
import com.sinyuk.jianyimaterial.application.Jianyi;
import com.sinyuk.jianyimaterial.base.UploadFragment;
import com.sinyuk.jianyimaterial.events.LocationSelectEvent;
import com.sinyuk.jianyimaterial.fragments.dialogs.LocationSelectDialog;
import com.sinyuk.jianyimaterial.glide.BlurTransformation;
import com.sinyuk.jianyimaterial.glide.ColorFilterTransformation;
import com.sinyuk.jianyimaterial.glide.CropCircleTransformation;
import com.sinyuk.jianyimaterial.greendao.dao.DaoUtils;
import com.sinyuk.jianyimaterial.greendao.dao.UserService;
import com.sinyuk.jianyimaterial.entity.User;
import com.sinyuk.jianyimaterial.utils.DialogUtils;
import com.sinyuk.jianyimaterial.utils.PreferencesUtils;
import com.sinyuk.jianyimaterial.utils.StringUtils;
import com.sinyuk.jianyimaterial.utils.ToastUtils;
import com.sinyuk.jianyimaterial.volley.FormDataRequest;
import com.sinyuk.jianyimaterial.volley.VolleyErrorHelper;
import com.sinyuk.jianyimaterial.widgets.MyCircleImageView;
import com.yalantis.ucrop.UCrop;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by Sinyuk on 16.2.16.
 */
public class UserInfoFragment extends UploadFragment {
    private static UserInfoFragment instance;
    @Bind(R.id.backdrop_iv)
    ImageView backdropIv;
    @Bind(R.id.app_bar_layout)
    AppBarLayout appBarLayout;
    @Bind(R.id.avatar)
    MyCircleImageView avatar;
    @Bind(R.id.user_name_et)
    EditText userNameEt;
    @Bind(R.id.real_name_et)
    EditText realNameEt;
    @Bind(R.id.location_et)
    EditText locationEt;
    @Bind(R.id.tel_et)
    EditText telEt;
    @Bind(R.id.password_et)
    EditText passwordEt;
    @Bind(R.id.with_wechat_et)
    EditText accountEt;
    @Bind(R.id.nested_scroll_view)
    NestedScrollView nestedScrollView;
    @Bind(R.id.nested_coordinator_layout)
    CoordinatorLayout mCoordinatorLayout;
    @Bind(R.id.fab)
    Button fab;
    private User currentUser;
    private UserService userService;

    private boolean hasEdited = false;


    private String currentAvatar;
    private String uploadAvatar;

    private ProgressDialog progressDialog;

    private int schoolIndex = 1;

    public static UserInfoFragment getInstance() {
        if (null == instance)
            instance = new UserInfoFragment();

        return instance;
    }

    @Override
    protected int getContentViewId() {
        return R.layout.fragment_user_info;
    }


    @Override
    protected boolean isUsingEventBus() {
        return true;
    }

    @Override
    protected void initViewsAndEvent() {

        String uId = PreferencesUtils.getString(mContext, StringUtils.getRes(mContext, R.string.key_user_id));
        userService = DaoUtils.getUserService();
        currentUser = (User) userService.query(uId);
        setDefault();

    }


    private void setDefault() {
        if (currentUser == null) return;

        currentAvatar = currentUser.getHeading();

        DrawableRequestBuilder<String> requestBuilder = Glide.with(this).fromString().diskCacheStrategy(DiskCacheStrategy.RESULT);

        requestBuilder.load(currentAvatar).bitmapTransform(new CropCircleTransformation(mContext)).crossFade()
                .thumbnail(0.2f).error(R.drawable.ic_avatar_placeholder).priority(Priority.IMMEDIATE).into(avatar);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            requestBuilder.load(currentAvatar).bitmapTransform(new BlurTransformation(mContext))
                    .crossFade().priority(Priority.HIGH).error(R.drawable.backdrop_2).thumbnail(0.5f).into(backdropIv);
        } else {
            requestBuilder.load(currentAvatar).bitmapTransform(new ColorFilterTransformation(mContext, mContext.getResources().getColor(R.color.colorPrimary_50pct)))
                    .crossFade().priority(Priority.HIGH).error(R.drawable.backdrop_2).thumbnail(0.5f).into(backdropIv);
        }


        if (!TextUtils.isEmpty(currentUser.getName()))
            userNameEt.setText(currentUser.getName());


        if (!TextUtils.isEmpty(currentUser.getRealname()))
            realNameEt.setText(currentUser.getRealname());

        if (currentUser.getSchool() != null) {
            schoolIndex = Integer.parseInt(currentUser.getSchool());
        }

        setupLocation(schoolIndex - 1);


        if (!TextUtils.isEmpty(currentUser.getTel()))
            telEt.setText(currentUser.getTel());
    }


    @OnClick({R.id.avatar, R.id.user_name_et, R.id.real_name_et, R.id.location_et, R.id.tel_et, R.id.password_et, R.id.with_wechat_et})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.avatar:
                pickPhoto();
                break;
            case R.id.user_name_et:
                setUserName();
                break;
            case R.id.real_name_et:
                setRealName();
                break;
            case R.id.location_et:
                onSelectLocation();
                break;
            case R.id.tel_et:
                setTel();
                break;
            case R.id.password_et:
                // TODO: 设置密码
                setPassword();
                break;
            case R.id.with_wechat_et:
                // TODO: 关联微信号
                break;
        }
    }

    private void setUserName() {
        userNameEt.setFocusable(true);
        userNameEt.setFocusableInTouchMode(true);
        userNameEt.setCursorVisible(true);
        userNameEt.requestFocus();
        hasEdited = true;
    }

    private void setPassword() {
        hasEdited = true;
    }

    private void setupLocation(int index) {
        final String[] schools = getResources().getStringArray(R.array.schools_sort);

        if (index >= 0 && index < schools.length) {
            locationEt.setText(schools[index]);
            schoolIndex = index + 1;
        }
    }

    private void setRealName() {
        realNameEt.setFocusable(true);
        realNameEt.setFocusableInTouchMode(true);
        realNameEt.setCursorVisible(true);
        realNameEt.requestFocus();
        hasEdited = true;
    }

    private void setTel() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.MyAlertDialogTheme);
        builder.setMessage("账号一旦创建就无法更改,你可以在发布时备注你的新号码")
                .setCancelable(true)
                .setPositiveButton("确定", null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    @Override
    public String getCacheFileName() {
        return "avatar";
    }


    @Override
    protected void startUCrop(Uri fromMediaStore, Uri saveInCache) {
        UCrop uCrop = UCrop.of(fromMediaStore, saveInCache);
        uCrop.withAspectRatio(1, 1);
        UCrop.Options options = new UCrop.Options();
        options.setCompressionFormat(Bitmap.CompressFormat.JPEG);
        options.setImageToCropBoundsAnimDuration(150);
        options.setMaxScaleMultiplier(5);
        options.setOvalDimmedLayer(true);
        uCrop.withOptions(options);
        uCrop.start(mContext, this);
    }

    @Override
    protected void handleCropResult(Uri result) {

        DrawableRequestBuilder<Uri> requestBuilder = Glide.with(this).fromMediaStore().crossFade();

        requestBuilder.load(result).error(R.drawable.ic_avatar_placeholder).into(avatar);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            requestBuilder.load(result).bitmapTransform(new BlurTransformation(mContext))
                    .error(R.drawable.backdrop_2).into(backdropIv);
        } else {
            requestBuilder.load(result).bitmapTransform(new ColorFilterTransformation(mContext, mContext.getResources().getColor(R.color.colorPrimary_50pct)))
                    .error(R.drawable.backdrop_2).into(backdropIv);
        }

    }


    @Override
    protected int getMaxBufferSize() {
        return 640 * 640;
    }

    @Override
    protected void uploadFailed(VolleyError error) {
        ToastUtils.toastSlow(mContext, VolleyErrorHelper.getMessage(error, mContext));
    }

    // 修改了头像并成功上传

    @Override
    protected void uploadSucceed(NetworkResponse response) {
        try {
            final Gson gson = new Gson();
            String jsonString = new String(response.data,
                    HttpHeaderParser.parseCharset(response.headers));
            JUploadResponse uploadResponse = gson.fromJson(jsonString, JUploadResponse.class);
            String url = uploadResponse.getData();
            if (null != url) {
                uploadAvatar = JianyiApi.JIANYI + url;
                hasEdited = true;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void onSelectLocation() {
        LocationSelectDialog dialog = new LocationSelectDialog();
        dialog.show(getChildFragmentManager(), LocationSelectDialog.TAG);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLocationSelected(LocationSelectEvent event) {
        setupLocation(event.getWhich());
        hasEdited = true;
    }

    @OnClick(R.id.fab)
    public void onClickFab() {
        attemptPostUserInfo();
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors , the errors are presented and no actual login attempt is made.
     */
    private void attemptPostUserInfo() {
        if (!hasEdited) {
            ToastUtils.toastFast(mContext, "未做任何修改");
            return;
        }

        fab.setEnabled(false);

        // Reset errors.
        userNameEt.setError(null);
        realNameEt.setError(null);

        // Store values at the time of the login attempt.
        String userName = userNameEt.getText().toString();
        String realName = realNameEt.getText().toString();
        String school = locationEt.getText().toString();
        boolean cancel = false;


        if (TextUtils.isEmpty(userName)) {
            // TODO: Check for a valid userName.
            userNameEt.setError("昵称不能为空");
            cancel = true;
        }

        if (TextUtils.isEmpty(school)) {
            // TODO: Check for a valid userName.
            locationEt.setError("学校不能为空");
            cancel = true;
        }

        if (cancel) {
            // Reset focus

        } else {
            currentUser.setName(userName);
            currentUser.setRealname(realName);
            currentUser.setSchool(String.valueOf(schoolIndex));
            currentUser.setHeading(uploadAvatar);
            userService.saveOrUpdate(currentUser);


            createProgressDialog();
            progressDialog.show();

            startPostTask();

        }
    }


    private void startPostTask() {
        final String password = PreferencesUtils.getString(mContext, StringUtils.getRes(mContext, R.string.key_psw));

        FormDataRequest jsonRequest = new FormDataRequest(Request.Method.POST, JianyiApi.updateUser(), new Response.Listener<String>() {
            @Override
            public void onResponse(String str) {
                final Gson gson = new Gson();
                JsonParser parser = new JsonParser();
                final JsonObject response = parser.parse(str).getAsJsonObject();
                JResponse jResponse = gson.fromJson(response, JResponse.class);

                if (jResponse != null && jResponse.getCode() == JResponse.CODE_SUCCEED) {
                    postSucceed();
                } else {
                    postFailed(null);
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
                params.put("tel", currentUser.getTel());
                params.put("password", password);
                params.put("name", currentUser.getName());
                params.put("realname", currentUser.getRealname());
                params.put("school", currentUser.getSchool());
                params.put("heading", currentUser.getHeading());

                return params;
            }
        };

        Jianyi.getInstance().addRequest(jsonRequest, User.UPDATE_REQUEST);
    }

    private void postFailed(VolleyError error) {
        hasEdited = false;
        if (error != null) {
            ToastUtils.toastSlow(mContext, VolleyErrorHelper.getMessage(error, mContext));
        } else {
            ToastUtils.toastSlow(mContext, "上传失败");
        }
        fab.setEnabled(true);
        if (null != progressDialog)
            progressDialog.dismiss();
    }

    private void postSucceed() {
        hasEdited = false;
        fab.setEnabled(true);
        if (null != progressDialog)
            progressDialog.dismiss();
        ToastUtils.toastFast(mContext, "修改成功");
    }


    private void createProgressDialog() {
        progressDialog = DialogUtils.simpleProgressDialog(mContext, null, "上传资料中", new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                hasEdited = false;
                fab.setEnabled(true);
                Jianyi.getInstance().cancelPendingRequest(User.UPDATE_REQUEST);
            }
        });
    }


}
