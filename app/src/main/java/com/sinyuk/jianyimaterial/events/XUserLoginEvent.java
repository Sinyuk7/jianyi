package com.sinyuk.jianyimaterial.events;

/**
 * Created by Sinyuk on 16.3.8.
 */
public class XUserLoginEvent {
    private String phoneNumber;
    private String password;

    public XUserLoginEvent(String phoneNumber, String password) {
        this.phoneNumber = phoneNumber;
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

}
