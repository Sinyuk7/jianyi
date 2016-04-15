package com.sinyuk.jianyimaterial.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Base64;

import com.android.volley.AuthFailureError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;
import com.sinyuk.jianyimaterial.api.JUploadResponse;
import com.sinyuk.jianyimaterial.api.JianyiApi;
import com.sinyuk.jianyimaterial.application.Jianyi;
import com.sinyuk.jianyimaterial.utils.LogUtils;
import com.sinyuk.jianyimaterial.volley.MultipartRequest;
import com.sinyuk.jianyimaterial.volley.VolleyErrorHelper;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Sinyuk on 16.4.8.
 */
public class ShotModel {
    private static ShotModel sInstance;
    private final String mTwoHyphens = "--";
    private final String mLineEnd = "\r\n";
    private final String mBoundary = "apiclient-" + System.currentTimeMillis();
    private final String mMimeType = "multipart/form-data;mBoundary=" + mBoundary;
    private final Context mContext;
    private Gson mGson;


    private ShotModel(Context context) {
        this.mContext = context;
        mGson = new Gson();
    }

    public static ShotModel getInstance(Context context) {
        if (sInstance == null) {
            synchronized (ShotModel.class) {
                if (sInstance == null) {
                    sInstance = new ShotModel(context);
                }
            }
        }
        return sInstance;
    }


    /**
     * @param uriStr   bitmap uri
     * @param callback
     */
    public void compressThenUpload(String uriStr, ShotUploadCallback callback) {
        LogUtils.simpleLog(ShotModel.class, "compressThenUpload");
        Observable.just(uriStr)
                .map(Uri::parse)
                .doOnError(parseUri -> callback.onUploadCompressFailed(parseUri.getMessage()))
                .map(this::getFileDataFromDrawable)
                .doOnError(toByte -> callback.onUploadCompressFailed(toByte.getMessage()))
                .subscribeOn(Schedulers.io())
                .map(this::getMultipartBody)
                .subscribeOn(Schedulers.io())
                .doOnError(transform -> callback.onUploadParseError(transform.getMessage()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(multipartBody -> {upload(multipartBody, callback);});
    }

    public byte[] getMultipartBody(byte[] imageFile) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(bos);
        try {
            buildPart(dos, imageFile, "upload_by_sinyuk.jpg");
            dos.writeBytes(mTwoHyphens + mBoundary + mTwoHyphens + mLineEnd);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bos.toByteArray();
    }

    private void upload(byte[] multipartBody, ShotUploadCallback callback) {
        LogUtils.simpleLog(ShotModel.class, "upload");
        MultipartRequest multipartRequest = new MultipartRequest(JianyiApi.uploadImage(), null, mMimeType, multipartBody, response -> {
            try {
                String jsonString = new String(response.data,
                        HttpHeaderParser.parseCharset(response.headers));
                JUploadResponse uploadResponse = mGson.fromJson(jsonString, JUploadResponse.class);
                LogUtils.simpleLog(ShotModel.class, "uploadResponse" + uploadResponse.toString());
                String url = uploadResponse.getData();
                LogUtils.simpleLog(ShotModel.class, "getData" + uploadResponse.getData());
                if (null != url) {
                    callback.onUploadUploaded(url);
                }
            } catch (Exception e) {
                callback.onUploadParseError(e.getMessage());
                LogUtils.simpleLog(ShotModel.class, "onUploadParseError");
            }

        }, error -> callback.onUploadVolleyError(VolleyErrorHelper.getMessage(error))) {
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
        Jianyi.getInstance().add(multipartRequest);
    }

    /**
     * @param dataOutputStream
     * @param fileData
     * @param fileName
     * @throws IOException
     */
    private void buildPart(DataOutputStream dataOutputStream, byte[] fileData, String fileName) throws IOException {
        dataOutputStream.writeBytes(mTwoHyphens + mBoundary + mLineEnd);
        dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\"; filename=\""
                + fileName + "\"" + mLineEnd);
        dataOutputStream.writeBytes(mLineEnd);

        ByteArrayInputStream fileInputStream = new ByteArrayInputStream(fileData);
        int bytesAvailable = fileInputStream.available();

        int maxBufferSize = 800 * 600;
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
        dataOutputStream.writeBytes(mLineEnd);
    }

    /**
     * @param uri
     * @return
     */
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
                bitmap.compress(Bitmap.CompressFormat.JPEG, 70, byteArrayOutputStream);
            }
        } finally {
            if (bitmap != null) {
                bitmap.recycle();
            }
        }
        return byteArrayOutputStream.toByteArray();
    }

    public interface ShotUploadCallback {

        void onUploadParseError(String message);

        void onUploadVolleyError(String message);

        void onUploadUploaded(String url);

        void onUploadCompressFailed(String message);
    }

}
