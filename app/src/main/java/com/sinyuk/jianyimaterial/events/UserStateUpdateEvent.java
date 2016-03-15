package com.sinyuk.jianyimaterial.events;


/**
 * Created by Sinyuk on 16.2.15.
 */
public class UserStateUpdateEvent extends BaseEvent {
    private boolean isLogin;
    private String userId;

    public UserStateUpdateEvent(boolean isLogin, String userId) {
        this.isLogin = isLogin;
        this.userId = userId;
    }

    @Override
    public String getType() {
        return "UserStateUpdateEvent";
    }

    public boolean isLogin() {
        return isLogin;
    }

    public String getUserId() {
        return userId;
    }
}
