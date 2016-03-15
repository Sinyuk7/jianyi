package com.sinyuk.jianyimaterial.events;

/**
 * Created by Sinyuk on 16.2.5.
 */
public class RefreshCallback extends BaseEvent{
    @Override
    public String getType() {
        return "RefreshCallback";
    }

    private boolean success;
    public RefreshCallback(boolean success){
        this.success = success;
    }

    public boolean isSuccess() {
        return success;
    }
}
