package com.sinyuk.jianyimaterial.activities;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sinyuk.jianyimaterial.R;
import com.sinyuk.jianyimaterial.api.JResponse;
import com.sinyuk.jianyimaterial.api.JUploadResponse;
import com.sinyuk.jianyimaterial.api.JianyiApi;
import com.sinyuk.jianyimaterial.application.Jianyi;
import com.sinyuk.jianyimaterial.base.BaseActivity;
import com.sinyuk.jianyimaterial.events.LocationSelectEvent;
import com.sinyuk.jianyimaterial.events.UserStateUpdateEvent;
import com.sinyuk.jianyimaterial.fragments.dialogs.LocationSelectDialog;
import com.sinyuk.jianyimaterial.greendao.dao.DaoUtils;
import com.sinyuk.jianyimaterial.greendao.dao.UserService;
import com.sinyuk.jianyimaterial.entity.User;
import com.sinyuk.jianyimaterial.utils.DialogUtils;
import com.sinyuk.jianyimaterial.utils.FileUtils;
import com.sinyuk.jianyimaterial.utils.LogUtils;
import com.sinyuk.jianyimaterial.utils.PreferencesUtils;
import com.sinyuk.jianyimaterial.utils.StringUtils;
import com.sinyuk.jianyimaterial.utils.ToastUtils;
import com.sinyuk.jianyimaterial.volley.FormDataRequest;
import com.sinyuk.jianyimaterial.volley.MultipartRequest;
import com.sinyuk.jianyimaterial.volley.VolleyErrorHelper;
import com.sinyuk.jianyimaterial.widgets.MyCircleImageView;
import com.yalantis.ucrop.UCrop;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;

public class RegisterSettings extends BaseActivity {


    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.avatar_boy)
    MyCircleImageView avatarBoy;
    @Bind(R.id.avatar_girl)
    MyCircleImageView avatarGirl;
    @Bind(R.id.view_switcher)
    ViewSwitcher viewSwitcher;
    @Bind(R.id.hint_set_avatar)
    TextView hintSetAvatar;
    @Bind(R.id.male_flag)
    ImageView maleFlag;
    @Bind(R.id.female_flag)
    ImageView femaleFlag;
    @Bind(R.id.user_alias_et)
    EditText userAliasEt;
    @Bind(R.id.confirm_btn)
    Button confirmBtn;
    @Bind(R.id.root_view)
    LinearLayout rootView;

    @Bind(R.id.gender_switcher)
    SwitchCompat genderSwitcher;
    @Bind(R.id.location_et)
    EditText locationEt;

    private static final String UPLOAD_PHOTO = "upload_photo";
    private final String twoHyphens = "--";
    private final String lineEnd = "\r\n";
    private final String boundary = "apiclient-" + System.currentTimeMillis();
    private final String mimeType = "multipart/form-data;boundary=" + boundary;
    private byte[] multipartBody;
    private static final int REQUEST_PICK_PICTURE = 0x02;
    private Uri mUnCroppedImgUri;


    private int selectedGender = 0;
    private ProgressDialog uploadProgress;
    private String uploadUrl;
    private String password;
    private UserService userService;
    private User currentUser;
    private int schoolIndex = 1;
    private Dialog progressDialog;

    @Override
    protected void beforeSetContentView(Bundle savedInstanceState) {
        String uId = PreferencesUtils.getString(mContext, StringUtils.getRes(mContext, R.string.key_user_id));
        password = PreferencesUtils.getString(mContext, StringUtils.getRes(mContext, R.string.key_psw));
        userService = DaoUtils.getUserService();
        currentUser = (User) userService.query(uId);

        LogUtils.simpleLog(RegisterSettings.class,PreferencesUtils.getString(mContext, StringUtils.getRes(mContext, R.string.key_user_id)));
        LogUtils.simpleLog(RegisterSettings.class,PreferencesUtils.getString(mContext, StringUtils.getRes(mContext, R.string.key_psw)));
    }


    @Override
    protected int getContentViewID() {
        return R.layout.activity_register_settings;
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
        setupViewSwitcher();
        setupGenderToggle();
    }

    @Override
    protected void initData() {

    }

    private void setupGenderToggle() {
        genderSwitcher.setChecked(false); // 女的是 true
        genderSwitcher.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    viewSwitcher.showNext();
                    uploadUrl = null;
                    avatarGirl.setImageDrawable(getResources().getDrawable(R.drawable.girl));

                } else {
                    uploadUrl = null;
                    viewSwitcher.showPrevious();
                    avatarBoy.setImageDrawable(getResources().getDrawable(R.drawable.boy));
                }
            }
        });
    }

    // TODO:切换头像
    private void setupViewSwitcher() {
        Animation slide_in_left = AnimationUtils.loadAnimation(this,
                android.R.anim.fade_in);
        Animation slide_out_right = AnimationUtils.loadAnimation(this,
                android.R.anim.fade_out);

        viewSwitcher.setInAnimation(slide_in_left);
        viewSwitcher.setOutAnimation(slide_out_right);
    }

    @OnClick({R.id.avatar_girl, R.id.avatar_boy, R.id.view_switcher, R.id.location_et})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.avatar_girl:
                selectedGender = 0;
                pickPhoto();
                break;
            case R.id.avatar_boy:
                selectedGender = 1;
                pickPhoto();
                break;
            case R.id.location_et:
                onSelectLocation();
                break;
        }
    }

    private void onSelectLocation() {
        LocationSelectDialog dialog = new LocationSelectDialog();
        dialog.show(getSupportFragmentManager(), LocationSelectDialog.TAG);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLocationSelected(LocationSelectEvent event) {
        setupLocation(event.getWhich());
    }

    private void setupLocation(int index) {
        final String[] schools = getResources().getStringArray(R.array.schools_sort);

        if (index >= 0 && index < schools.length) {
            locationEt.setText(schools[index]);
            schoolIndex = index + 1;
        }
    }


    protected void pickPhoto() {
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermission(Manifest.permission.READ_EXTERNAL_STORAGE,
                    getString(R.string.permission_read_storage_rationale),
                    REQUEST_STORAGE_READ_ACCESS_PERMISSION);
        } else {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            startActivityForResult(Intent.createChooser(intent, getString(R.string.hint_pick_pics)), REQUEST_PICK_PICTURE);
        }
    }

    /**
     * Requests given permission.
     * If the permission has been denied previously, a Dialog will prompt the user to grant the
     * permission, otherwise it is requested directly.
     */
    private void requestPermission(final String permission, String rationale, final int requestCode) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setMessage(rationale)
                    .setCancelable(false)
                    .setNegativeButton("取消", null).setPositiveButton("允许", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ActivityCompat.requestPermissions(RegisterSettings.this,
                            new String[]{permission}, requestCode);
                }
            }).setTitle(mContext.getResources().getString(R.string.permission_read_storage_rationale));

            AlertDialog alertDialog = builder.show();

        } else {
            ActivityCompat.requestPermissions(this, new String[]{permission}, requestCode);
        }
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_STORAGE_READ_ACCESS_PERMISSION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    pickPhoto();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != 0) {
            if (requestCode == REQUEST_PICK_PICTURE) {
                mUnCroppedImgUri = data.getData();
                if (mUnCroppedImgUri != null) {
                    handleUriFromMediaStore(data.getData());
                } else {
//                    Toast.makeText(mContext, R.string.toast_cannot_retrieve_selected_image, Toast.LENGTH_LONG).show();
                }
            } else if (requestCode == UCrop.REQUEST_CROP) {
                onCropSucceed(data);
            }
        }

        if (resultCode == UCrop.RESULT_ERROR) {
            onCropFailed(data);
        }
    }

    private void handleUriFromMediaStore(Uri uri) {
        FileUtils.delete(new File(mContext.getCacheDir(), "avatar" + SystemClock.currentThreadTimeMillis()));

        Uri saveLocation = Uri.fromFile(new File(mContext.getCacheDir(), "avatar" + SystemClock.currentThreadTimeMillis()));

        startUCrop(uri, saveLocation);
    }

    private void startUCrop(@NonNull Uri uri, Uri saveLocation) {
        UCrop uCrop = UCrop.of(uri, saveLocation);

        uCrop.withAspectRatio(1, 1);
        UCrop.Options options = new UCrop.Options();
        options.setCompressionFormat(Bitmap.CompressFormat.JPEG);
        options.setImageToCropBoundsAnimDuration(150);
        options.setMaxScaleMultiplier(5);
        options.setOvalDimmedLayer(true);
        uCrop.withOptions(options);

        uCrop.start(RegisterSettings.this);

    }

    private void onCropSucceed(@NonNull Intent result) {
        final Uri resultUri = UCrop.getOutput(result);
        if (resultUri != null) {
            if (selectedGender == 0) {
                Glide.with(this).load(resultUri)
                        .crossFade()
                        .priority(Priority.IMMEDIATE)
                        .into(avatarGirl);

            } else {
                Glide.with(this).load(resultUri)
                        .crossFade()
                        .priority(Priority.IMMEDIATE)
                        .into(avatarBoy);
            }
            uploadPhoto(resultUri);
        } else {
            ToastUtils.toastFast(this, "图片加载失败");
        }
    }

    private void onCropFailed(Intent result) {
        final Throwable cropError = UCrop.getError(result);
        if (cropError != null) {
            ToastUtils.toastSlow(this, cropError.getMessage());

        } else {
            ToastUtils.toastFast(this, "发生未知错误");
        }
    }

    protected void uploadPhoto(@NonNull Uri resultUri) {
        showUploadProgress();

        byte[] imageFile = getFileDataFromDrawable(resultUri);

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(bos);
        try {
            buildPart(dos, imageFile, "upload_by_sinyuk.jpg");
            // send multipart form data necesssary after file data
            dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
            // pass to multipart body
            multipartBody = bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            uploadFailed();
        }

        MultipartRequest multipartRequest = new MultipartRequest(JianyiApi.uploadImage(), null, mimeType, multipartBody, new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {
                uploadSucceed(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                uploadFailed(error);

            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return createBasicAuthHeader("15757161281", "aaa");
            }

            Map<String, String> createBasicAuthHeader(String username, String password) {
                Map<String, String> headerMap = new HashMap<>();
                String credentials = username + ":" + password;
                String encodedCredentials = Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
                headerMap.put("Authorization", "Basic " + encodedCredentials);

                return headerMap;
            }
        };

        multipartRequest.setTag(UPLOAD_PHOTO);
        Jianyi.getInstance().add(multipartRequest);

    }

    private void showUploadProgress() {
        uploadProgress = DialogUtils.simpleProgressDialog(mContext, null, "上传图片", new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                ToastUtils.toastSlow(RegisterSettings.this, "上传中断");
                // cancel the upload
                Jianyi.getInstance().cancelPendingRequest(UPLOAD_PHOTO);
            }
        });
        uploadProgress.show();
    }


    private void uploadFailed() {
        if (null != uploadProgress)
            uploadProgress.dismiss();
        ToastUtils.toastSlow(mContext, "上传失败");
    }

    private void uploadFailed(VolleyError error) {
        if (null != uploadProgress)
            uploadProgress.dismiss();
        ToastUtils.toastSlow(mContext, VolleyErrorHelper.getMessage(error));
    }

    ;

    private void uploadSucceed(NetworkResponse response) {
        if (null != uploadProgress)
            uploadProgress.dismiss();
        try {
            final Gson gson = new Gson();
            String jsonString = new String(response.data,
                    HttpHeaderParser.parseCharset(response.headers));
            JUploadResponse uploadResponse = gson.fromJson(jsonString, JUploadResponse.class);
            String url = uploadResponse.getData();
            if (null != url) {
                uploadUrl = JianyiApi.JIANYI + url;
                LogUtils.simpleLog(RegisterSettings.class, "返回的url" + uploadUrl);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    protected void buildPart(DataOutputStream dataOutputStream, byte[] fileData, String fileName) throws IOException {
        dataOutputStream.writeBytes(twoHyphens + boundary + lineEnd);
        dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\"; filename=\""
                + fileName + "\"" + lineEnd);
        dataOutputStream.writeBytes(lineEnd);

        ByteArrayInputStream fileInputStream = new ByteArrayInputStream(fileData);
        int bytesAvailable = fileInputStream.available();

        int maxBufferSize = 640 * 640;
        int bufferSize = Math.min(bytesAvailable, maxBufferSize);
        byte[] buffer = new byte[bufferSize];

        // read file and write it into form...
        int bytesRead = fileInputStream.read(buffer, 0, bufferSize);

        while (bytesRead > 0) {
            dataOutputStream.write(buffer, 0, bufferSize);
            bytesAvailable = fileInputStream.available();
            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            bytesRead = fileInputStream.read(buffer, 0, bufferSize);
        }

        dataOutputStream.writeBytes(lineEnd);
    }

    protected byte[] getFileDataFromDrawable(Uri uri) {
        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(mContext.getContentResolver(), uri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ByteArrayOutputStream byteArrayOutputStream = null;
        try {
            byteArrayOutputStream = new ByteArrayOutputStream();
            if (bitmap != null) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 75, byteArrayOutputStream);
            }
        } finally {
            if (bitmap != null) {
                bitmap.recycle();
            }
        }

        return byteArrayOutputStream.toByteArray();
    }


    @OnClick(R.id.confirm_btn)
    void attemptConfirm() {

        // Reset errors.
        userAliasEt.setError(null);
        locationEt.setError(null);
        // Store values at the time of the confirm attempt.
        String userName = userAliasEt.getText().toString();
        String schoolName = locationEt.getText().toString();

        boolean cancel = false;


        if (TextUtils.isEmpty(userName)) {
            // TODO: Check for a valid userAlias.
            userAliasEt.setError("请填写用户名");
            cancel = true;
        } else if (!isUserAliasValid(userName)) {
            userAliasEt.setError("中英文数字+下划线");
            cancel = true;
        }

        if (TextUtils.isEmpty(schoolName)) {
            // TODO: Check for a valid userAlias.
            locationEt.setError("请选择学校");
            cancel = true;
        }


        if (cancel) {
            // Reset focus
        } else {
            currentUser.setName(userName);
            currentUser.setSchool(String.valueOf(schoolIndex));
            currentUser.setHeading(uploadUrl);
            userService.saveOrUpdate(currentUser);

            LogUtils.simpleLog(RegisterSettings.class, "schoolIndex -> " + schoolIndex);
            LogUtils.simpleLog(RegisterSettings.class, "userName -> " + userName);
            LogUtils.simpleLog(RegisterSettings.class, "psw -> " + password);
            createProgressDialog();
            progressDialog.show();

            startPostTask();
        }
    }

    private boolean isUserAliasValid(String userAlias) {
        // TODO: 中文怎么略过判断
        return true;
    }


    private void startPostTask() {
        FormDataRequest jsonRequest = new FormDataRequest(Request.Method.POST, JianyiApi.updateUser(), new Response.Listener<String>() {
            @Override
            public void onResponse(String str) {
                final Gson gson = new Gson();
                JsonParser parser = new JsonParser();
                final JsonObject response = parser.parse(str).getAsJsonObject();
                JResponse jResponse = gson.fromJson(response, JResponse.class);

                if (jResponse != null && jResponse.getCode() == JResponse.CODE_SUCCEED) {
                    LogUtils.simpleLog(RegisterSettings.class, "response -> " + jResponse.getData());
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
                params.put("school", currentUser.getSchool());
                params.put("heading", currentUser.getHeading());

                return params;
            }
        };

        Jianyi.getInstance().addRequest(jsonRequest, User.UPDATE_REQUEST);
    }

    private void postFailed(VolleyError error) {
        if (error != null) {
            ToastUtils.toastSlow(mContext, VolleyErrorHelper.getMessage(error));
        } else {
            ToastUtils.toastSlow(mContext, "上传失败");
        }
        if (null != progressDialog)
            progressDialog.dismiss();
    }

    private void postSucceed() {
        if (null != progressDialog)
            progressDialog.dismiss();
        ToastUtils.toastFast(mContext, "上传成功");
        LogUtils.simpleLog(RegisterSettings.class,"post UserStateUpdateEvent");
        EventBus.getDefault().post(new UserStateUpdateEvent(true, PreferencesUtils.getString(mContext, StringUtils.getRes(mContext, R.string.key_user_id))));
        finish();
    }


    private void createProgressDialog() {
        progressDialog = DialogUtils.simpleProgressDialog(mContext, null, "上传资料中", new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                Jianyi.getInstance().cancelPendingRequest(User.UPDATE_REQUEST);
            }
        });
    }


}

