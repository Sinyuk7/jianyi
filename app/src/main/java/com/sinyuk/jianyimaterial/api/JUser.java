package com.sinyuk.jianyimaterial.api;

import com.sinyuk.jianyimaterial.entity.User;

/**
 * Created by Sinyuk on 16.2.15.
 */
public class JUser {

    public int status;
    public User data;
    public int code;

    public int getStatus() {
        return status;
    }

    public User getData() {
        return data;
    }

    public int getCode() {
        return code;
    }
}
