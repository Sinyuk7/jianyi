package com.sinyuk.jianyimaterial.events;

import com.sinyuk.jianyimaterial.entity.User;

/**
 * Created by Sinyuk on 16.2.26.
 */
public class UserInfoEvent extends BaseEvent {
    private final User user;

    public UserInfoEvent(User currentUser) {
        this.user = currentUser;
    }

    public User getUser() {
        return user;
    }

    @Override
    public String getType() {
        return "UserInfoEvent";
    }
}
