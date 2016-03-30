package com.sinyuk.jianyimaterial.volley;

import android.util.Base64;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.sinyuk.jianyimaterial.api.JianyiApi;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Sinyuk on 15.12.28.
 */
public class JsonRequest extends JsonObjectRequest {

    public JsonRequest(int method, String url, JSONObject jsonRequest,
                       Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        super(method, url, jsonRequest, listener, errorListener);
    }

    private Priority mPriority;

    public void setPriority(Priority priority) {
        mPriority = priority;
    }

    @Override
    public Priority getPriority() {
        return mPriority == null ? Priority.NORMAL : mPriority;
    }

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
}
