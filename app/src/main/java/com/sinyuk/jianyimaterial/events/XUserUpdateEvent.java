package com.sinyuk.jianyimaterial.events;

import com.sinyuk.jianyimaterial.model.User;

/**
 * Created by Sinyuk on 16.3.8.
 */
public class XUserUpdateEvent {

    private final  User user;

    public XUserUpdateEvent(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }
}
