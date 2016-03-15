package com.sinyuk.jianyimaterial.api;

/**
 * Created by Sinyuk on 16.2.19.
 */
public class JPostError {

    /**
     * status : 0
     * error_msg : 您未登录
     * error_code : 3000
     */

    public int status;
    public String error_msg;
    public int error_code;

    public int getStatus() {
        return status;
    }

    public String getError_msg() {
        return error_msg;
    }

    public int getError_code() {
        return error_code;
    }
}
