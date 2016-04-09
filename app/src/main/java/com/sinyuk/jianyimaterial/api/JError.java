package com.sinyuk.jianyimaterial.api;

/**
 * Created by Sinyuk on 16.2.15.
 */
public class JError {

    /**
     * status : 0
     * data : 手机号或密码错误
     * code : 3000
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

    @Override
    public String toString() {
        return "JError{" +
                "status=" + status +
                ", data='" + error_msg + '\'' +
                ", code=" + error_code +
                '}';
    }
}
