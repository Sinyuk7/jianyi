package com.sinyuk.jianyimaterial.fragments;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;
import com.sinyuk.jianyimaterial.R;
import com.sinyuk.jianyimaterial.adapters.ShotsGalleryAdapter;
import com.sinyuk.jianyimaterial.api.JUploadResponse;
import com.sinyuk.jianyimaterial.api.JianyiApi;
import com.sinyuk.jianyimaterial.application.Jianyi;
import com.sinyuk.jianyimaterial.base.BaseFragment;
import com.sinyuk.jianyimaterial.entity.User;
import com.sinyuk.jianyimaterial.events.CategorySelectEvent;
import com.sinyuk.jianyimaterial.events.XShotDropEvent;
import com.sinyuk.jianyimaterial.greendao.dao.DaoUtils;
import com.sinyuk.jianyimaterial.greendao.dao.UserService;
import com.sinyuk.jianyimaterial.managers.SnackBarFactory;
import com.sinyuk.jianyimaterial.utils.AnimUtils;
import com.sinyuk.jianyimaterial.utils.DialogUtils;
import com.sinyuk.jianyimaterial.utils.LogUtils;
import com.sinyuk.jianyimaterial.utils.PreferencesUtils;
import com.sinyuk.jianyimaterial.utils.StringUtils;
import com.sinyuk.jianyimaterial.utils.ToastUtils;
import com.sinyuk.jianyimaterial.volley.FormDataRequest;
import com.sinyuk.jianyimaterial.volley.MultipartRequest;
import com.sinyuk.jianyimaterial.volley.VolleyErrorHelper;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Sinyuk on 16.2.18.
 */
public class PostFeedFragment extends BaseFragment {

    private static final String POST_FEED = "post_feed";
    private static final int REQUEST_PICK_PICTURE = 0x01;
    private static PostFeedFragment instance;
    private final String twoHyphens = "--";
    private final String lineEnd = "\r\n";
    private final String boundary = "apiclient-" + System.currentTimeMillis();
    private final String mimeType = "multipart/form-data;boundary=" + boundary;
    @Bind(R.id.recycler_view)
    RecyclerView recyclerView;
    @Bind(R.id.shot_count_tv)
    TextView shotCountTv;
    @Bind(R.id.gallery_wrapper)
    LinearLayout galleryWrapper;
    @Bind(R.id.title_et)
    EditText titleEt;
    @Bind(R.id.title_input_area)
    TextInputLayout titleInputArea;
    @Bind(R.id.details_et)
    EditText detailsEt;
    @Bind(R.id.details_input_area)
    TextInputLayout detailsInputArea;
    @Bind(R.id.new_price_et)
    EditText newPriceEt;
    @Bind(R.id.new_price_input_area)
    TextInputLayout newPriceInputArea;
    @Bind(R.id.post_btn)
    Button postBtn;
    @Bind(R.id.nested_scroll_view)
    NestedScrollView nestedScrollView;
    private ShotsGalleryAdapter adapter;
    private View addButton;
    private ArrayList<Uri> uriList;
    private ProgressDialog progressDialog;
    private ArrayList<String> uploadUrls;
    private byte[] multipartBody;
    private ProgressDialog uploadProgress;

    public static PostFeedFragment getInstance() {
        if (instance == null) {
            instance = new PostFeedFragment();
        }
        return instance;
    }

    @Override
    protected int getContentViewId() {
        return R.layout.offer_view_content;
    }

    @Override
    protected boolean isUsingEventBus() {
        return true;
    }

    @Override
    protected void initViewsAndEvent() {

        uriList = new ArrayList<>();
        uploadUrls = new ArrayList<>();


        setHasOptionsMenu(true);
        setupRecyclerView();

    }

    private void setupRecyclerView() {

        adapter = new ShotsGalleryAdapter(mContext);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);

        recyclerView.setHasFixedSize(true);

        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.setAdapter(adapter);
        adapter.setData(uriList);

        addButton = View.inflate(mContext, R.layout.offer_view_shot_add_button, null);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickPhoto();
            }


        });


        adapter.setFooterView(addButton);

    }

    private void pickPhoto() {
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermission(Manifest.permission.READ_EXTERNAL_STORAGE,
                    getString(R.string.permission_read_storage_rationale),
                    REQUEST_STORAGE_READ_ACCESS_PERMISSION);
        } else {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            startActivityForResult(Intent.createChooser(intent, getString(R.string.offer_hint_pick_from)), REQUEST_PICK_PICTURE);
        }
    }

    /**
     * Requests given permission.
     * If the permission has been denied previously, a Dialog will prompt the user to grant the
     * permission, otherwise it is requested directly.
     */
    private void requestPermission(final String permission, String rationale, final int requestCode) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), permission)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.MyAlertDialogTheme);
            builder.setMessage(rationale)
                    .setCancelable(false)
                    .setNegativeButton("取消", null).setPositiveButton("允许", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{permission}, requestCode);
                }
            }).setTitle(getResources().getString(R.string.permission_read_storage_rationale));

            AlertDialog alertDialog = builder.show();

        } else {
            ActivityCompat.requestPermissions(getActivity(), new String[]{permission}, requestCode);
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
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == getActivity().RESULT_OK) {
            if (requestCode == REQUEST_PICK_PICTURE) {
                final Uri selectedUri = data.getData();
                if (selectedUri != null) {
                    if (uriList.size() == 3) { return; }
                    uriList.add(selectedUri);
                    adapter.notifyMyItemInserted(uriList.size());
                    updateCounterText(uriList.size());
                    startUpload(uriList.size() - 1);
                }
            }
        }

    }

    private void startUpload(int index) {

        showUploadProgress();

        byte[] imageFile = getFileDataFromDrawable(uriList.get(index));

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(bos);
        try {
            buildPart(dos, imageFile, "upload_by_sinyuk.jpg");
            // send multipart form data necesssary after file data
            dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
            // pass to multipart body
            multipartBody = bos.toByteArray();
        } catch (IOException e) {
            if (null != uploadProgress) { uploadProgress.dismiss(); }
            e.printStackTrace();
        }

        final Gson gson = new Gson();
        MultipartRequest multipartRequest = new MultipartRequest(JianyiApi.uploadImage(), null, mimeType, multipartBody, new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {
                try {
                    String jsonString = new String(response.data,
                            HttpHeaderParser.parseCharset(response.headers));
                    JUploadResponse uploadResponse = gson.fromJson(jsonString, JUploadResponse.class);
                    String url = uploadResponse.getData();
                    if (null != url) {
                        uploadUrls.add(url);
                    }
                    if (null != uploadProgress) { uploadProgress.dismiss(); }
                } catch (Exception e) {
                    if (null != uploadProgress) { uploadProgress.dismiss(); }
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (null != uploadProgress) { uploadProgress.dismiss(); }
                ToastUtils.toastSlow(mContext, VolleyErrorHelper.getMessage(error));
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return createBasicAuthHeader(JianyiApi.BASIC_AUTHOR_ACCOUNT, JianyiApi.BASIC_AUTHOR_PASSWORD);
            }

            Map<String, String> createBasicAuthHeader(String username, String password) {
                Map<String, String> headerMap = new HashMap<>();
                String credentials = username + ":" + password;
                String encodedCredentials = Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
                headerMap.put("Authorization", "Basic " + encodedCredentials);
                return headerMap;
            }
        };

        multipartRequest.setTag("upload" + index);
        Jianyi.getInstance().add(multipartRequest);

    }

    private void showUploadProgress() {
        uploadProgress = DialogUtils.simpleProgressDialog(mContext, null, "上传图片", new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                SnackBarFactory.loginFailed(mContext, getView(), "上传中断").show();
                // cancel the upload
                Jianyi.getInstance().cancelPendingRequest("upload" + (uriList.size() - 1));
            }
        });
        uploadProgress.show();
    }

    private void buildPart(DataOutputStream dataOutputStream, byte[] fileData, String fileName) throws IOException {
        dataOutputStream.writeBytes(twoHyphens + boundary + lineEnd);
        dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\"; filename=\""
                + fileName + "\"" + lineEnd);
        dataOutputStream.writeBytes(lineEnd);

        ByteArrayInputStream fileInputStream = new ByteArrayInputStream(fileData);
        int bytesAvailable = fileInputStream.available();

        int maxBufferSize = 1920 * 1080;
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

    private byte[] getFileDataFromDrawable(Uri uri) {
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
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream);
            }
        } finally {
            if (bitmap != null) {
                bitmap.recycle();
            }
        }

        return byteArrayOutputStream.toByteArray();
    }

    private void cancelUpload(int index) {
        Jianyi.getInstance().cancelPendingRequest("upload" + index);

    }


    private void updateCounterText(int index) {
        addButton.setEnabled(index != 3);
        if (index == 3) {
            shotCountTv.setTextColor(mContext.getResources().getColor(R.color.themeRed));
            AnimUtils.tada(shotCountTv);
        } else {
            shotCountTv.setTextColor(mContext.getResources().getColor(R.color.grey_600));
        }
        shotCountTv.setText(index + "/3");
    }


/*
    @OnClick(R.id.category_et)
    public void selectCategory() {
        CategorySelectDialog dialog = new CategorySelectDialog();
        dialog.show(getChildFragmentManager(), CategorySelectDialog.TAG);
    }
*/

    @OnClick(R.id.post_btn)
    public void onClick() {

        attemptPost();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_post) { attemptPost(); }
        return true;
    }

    private void attemptPost() {
        if (uriList.isEmpty()) {
            handleNoneShotError();
            return;
        }


        cancelFocus();

        // Reset errors.
        cancelErrorHint();
        // Store values at the time of the attempt.
        String title = titleEt.getText().toString();
        String details = detailsEt.getText().toString();

//        String category = categoryEt.getText().toString();
        String newPrice = newPriceEt.getText().toString();

        boolean cancel = false;


        if (TextUtils.isEmpty(title)) {
            // TODO: Check for a valid userName.
            titleEt.setError("标题不能为空");
            cancel = true;
        }

 /*       if (TextUtils.isEmpty(category)) {
            categoryEt.setError("标签不能为空");
            cancel = true;
        }*/

        if (TextUtils.isEmpty(newPrice)) {
            newPriceEt.setError("转让价不能为空");
            cancel = true;
        }

        if (cancel) {
            // Reset focus
            resetFocus();

        } else {

            hideSoftInput();
            createProgressDialog();
            progressDialog.show();
//            startPostTask(title, details, category, newPrice);

        }

    }

    private void startPostTask(final String title, final String description, final String category, final String newPrice) {
        if (uploadUrls.isEmpty()) {
            postFailed();
            return;
        }

        final UserService userService = DaoUtils.getUserService();
        String uId = PreferencesUtils.getString(mContext, StringUtils.getRes(mContext, R.string.key_user_id));
        final User user = (User) userService.query(uId);

        if (user == null) { postFailed(); }

        final String tel = user.getTel();

        final String password = PreferencesUtils.getString(mContext, StringUtils.getRes(mContext, R.string.key_psw));
        if (null == tel || null == password) { postFailed(); }

//
//        LogUtils.simpleLog(PostFeedFragment.class, tel + "\n" + password + "\n" + title + "\n" + description + "\n" + category + "\n" + newPrice);
//        LogUtils.simpleLog(PostFeedFragment.class, uploadUrls.get(0));
        FormDataRequest postRequest = new FormDataRequest
                (Request.Method.POST, JianyiApi.postFeed(), new Response.Listener<String>() {
                    @Override
                    public void onResponse(String str) {
                        postSucceed(str);
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
                params.put("name", title);
                params.put("title", category);
                params.put("detail", description);
                params.put("price", newPrice);
                params.put("sort", "all");
                params.put("pic[0]", uploadUrls.get(0));
                if (uploadUrls.size() >= 2) { params.put("pic[1]", uploadUrls.get(1)); }
                if (uploadUrls.size() >= 3) { params.put("pic[2]", uploadUrls.get(2)); }
                return params;
            }
        };

        Jianyi.getInstance().addRequest(postRequest, POST_FEED);
    }

    private void postFailed(VolleyError error) {
        ToastUtils.toastSlow(mContext, VolleyErrorHelper.getMessage(error));
        LogUtils.simpleLog(PostFeedFragment.class, error.getMessage());
        if (null != progressDialog) { progressDialog.dismiss(); }
    }

    private void postFailed() {
        ToastUtils.toastSlow(mContext, "发布失败");
        if (null != progressDialog) { progressDialog.dismiss(); }
    }

    private void postSucceed(String response) {
        LogUtils.simpleLog(UserInfoFragment.class, response);
        ToastUtils.toastSlow(mContext, "发布成功");
        if (null != progressDialog) { progressDialog.dismiss(); }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getActivity().finish();
            }
        }, 200);
    }

    private void createProgressDialog() {
        progressDialog = DialogUtils.simpleProgressDialog(mContext, null, "正在上传", new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                SnackBarFactory.loginFailed(mContext, getView(), "上传中断").show();
                resetFocus();
                // cancel the upload
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
        addButton.setEnabled(true);
        titleEt.setFocusableInTouchMode(true);
        titleEt.setFocusable(true);
        detailsEt.setFocusableInTouchMode(true);
        detailsEt.setFocusable(true);
//        categoryEt.setEnabled(true);
        newPriceEt.setFocusableInTouchMode(true);
        newPriceEt.setFocusable(true);
    }

    private void cancelErrorHint() {
        titleEt.setError(null);
        detailsEt.setError(null);
//        categoryEt.setError(null);
        newPriceEt.setError(null);
    }

    private void cancelFocus() {
        // Cancel focus
        addButton.setEnabled(false);
        titleEt.setFocusableInTouchMode(false);
        titleEt.setFocusable(false);
        detailsEt.setFocusableInTouchMode(false);
        detailsEt.setFocusable(false);
//        categoryEt.setEnabled(false);
        newPriceEt.setFocusableInTouchMode(false);
        newPriceEt.setFocusable(false);

    }

    private void handleNoneShotError() {
        SnackBarFactory.errorNoAction(mContext, getView(), "至少附带一张图片").show();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onListItemDelete(XShotDropEvent event) {
        cancelUpload(event.getPosition());
        updateCounterText(uriList.size());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCategorySelected(CategorySelectEvent event) {
        final String category = mContext.getResources().getStringArray(R.array.category_menu_items)[event.getWhich()];
        LogUtils.simpleLog(PostFeedFragment.class, "choosen category" + event.getWhich() + category);
//        if (!TextUtils.isEmpty(category))
//            categoryEt.setText(category);
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
}
