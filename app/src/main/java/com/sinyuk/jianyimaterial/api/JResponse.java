package com.sinyuk.jianyimaterial.api;

/**
 * Created by Sinyuk on 16.2.26.
 */
public class JResponse {
    public static final int CODE_SUCCEED = 2001;
    public int status;
    public String data;
    public int code;

    public int getStatus() {
        return status;
    }

    public String getData() {
        return data;
    }

    public int getCode() {
        return code;
    }
}
