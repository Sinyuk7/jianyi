package com.sinyuk.jianyimaterial.api;

/**
 * Created by Sinyuk on 16.2.23.
 */
public class JUploadResponse {

    /**
     * status : 1
     * data : /uploads/port/port_56cc78e8432db.png
     * code : 2001
     */

    public int status;
    public String data;
    public int code;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
