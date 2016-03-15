package com.sinyuk.jianyimaterial.api;

/**
 * Created by Sinyuk on 16.2.19.
 */
public class JPostResponse {

    /**
     * status : 0
     * data : 您未登录
     * code : 3000
     */

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
