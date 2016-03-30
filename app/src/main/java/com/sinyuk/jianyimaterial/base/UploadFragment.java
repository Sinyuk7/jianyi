package com.sinyuk.jianyimaterial.base;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.util.Base64;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.sinyuk.jianyimaterial.R;
import com.sinyuk.jianyimaterial.api.JianyiApi;
import com.sinyuk.jianyimaterial.application.Jianyi;

import com.sinyuk.jianyimaterial.managers.SnackBarFactory;
import com.sinyuk.jianyimaterial.utils.DialogUtils;
import com.sinyuk.jianyimaterial.utils.FileUtils;
import com.sinyuk.jianyimaterial.utils.ToastUtils;
import com.sinyuk.jianyimaterial.volley.MultipartRequest;
import com.yalantis.ucrop.UCrop;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Sinyuk on 16.2.26.
 */
public abstract class UploadFragment extends BaseFragment {

    private static final String UPLOAD_PHOTO = "upload_photo";
    private final String twoHyphens = "--";
    private final String lineEnd = "\r\n";
    private final String boundary = "apiclient-" + System.currentTimeMillis();
    private final String mimeType = "multipart/form-data;boundary=" + boundary;
    private byte[] multipartBody;
    private static final int REQUEST_PICK_PICTURE = 0x02;
    private Uri mUnCroppedImgUri;
    private ProgressDialog uploadProgress;


    @Override
    protected int getContentViewId() {
        return 0;
    }

    @Override
    protected boolean isUsingEventBus() {
        return false;
    }

    @Override
    protected void initViewsAndEvent() {
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
        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), permission)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setMessage(rationale)
                    .setCancelable(false)
                    .setNegativeButton("取消", null).setPositiveButton("允许", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{permission}, requestCode);
                }
            }).setTitle(mContext.getResources().getString(R.string.permission_read_storage_rationale));

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

    public abstract String getCacheFileName();


    protected void handleUriFromMediaStore(@NonNull Uri uri) {

        FileUtils.delete(new File(mContext.getCacheDir(), getCacheFileName() + SystemClock.currentThreadTimeMillis()));

        Uri saveLocation = Uri.fromFile(new File(mContext.getCacheDir(), getCacheFileName() + SystemClock.currentThreadTimeMillis()));

        startUCrop(uri,saveLocation);

    }

    /**
     * start uCrop activity
     *
     * @param fromMediaStore
     * @param saveInCache
     */
    protected abstract void startUCrop(Uri fromMediaStore, Uri saveInCache);




    protected void onCropFailed(Intent result) {
        final Throwable cropError = UCrop.getError(result);
        if (cropError != null ) {
            ToastUtils.toastSlow(mContext,cropError.getMessage());

        } else {
            ToastUtils.toastFast(mContext,"发生未知错误");
        }
    }

    protected void onCropSucceed(@NonNull Intent result) {

        final Uri resultUri = UCrop.getOutput(result);
        if (resultUri != null) {
            handleCropResult(resultUri);

            uploadPhoto(resultUri);
        } else {
            ToastUtils.toastFast(mContext,"图片加载失败");
        }
    }

    protected abstract void handleCropResult(Uri result);


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
            if (null != uploadProgress)
                uploadProgress.dismiss();
        }

        final Gson gson = new Gson();
        MultipartRequest multipartRequest = new MultipartRequest(JianyiApi.uploadImage(), null, mimeType, multipartBody, new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {

                uploadSucceed(response);
                if (null != uploadProgress)
                    uploadProgress.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                uploadFailed(error);
                if (null != uploadProgress)
                    uploadProgress.dismiss();
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

        multipartRequest.setTag(UPLOAD_PHOTO);
        Jianyi.getInstance().add(multipartRequest);

    }

    private void showUploadProgress() {
        uploadProgress = DialogUtils.simpleProgressDialog(mContext, null, "上传图片", new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                SnackBarFactory.loginFailed(mContext, getView(), "上传中断").show();
                // cancel the upload
                Jianyi.getInstance().cancelPendingRequest(UPLOAD_PHOTO);
            }
        });
        uploadProgress.show();
    }

    protected abstract void uploadFailed(VolleyError error);

    protected abstract void uploadSucceed(NetworkResponse response);

    protected abstract int getMaxBufferSize();

    protected void buildPart(DataOutputStream dataOutputStream, byte[] fileData, String fileName) throws IOException {
        dataOutputStream.writeBytes(twoHyphens + boundary + lineEnd);
        dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\"; filename=\""
                + fileName + "\"" + lineEnd);
        dataOutputStream.writeBytes(lineEnd);

        ByteArrayInputStream fileInputStream = new ByteArrayInputStream(fileData);
        int bytesAvailable = fileInputStream.available();

        int maxBufferSize = getMaxBufferSize();
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

}
