package com.sinyuk.jianyimaterial.events;

/**
 * Created by Sinyuk on 16.3.8.
 */
public class XLoginStateUpdateEvent {
   private boolean isLogged;

    public XLoginStateUpdateEvent(boolean isLogged) {
        this.isLogged =isLogged;
    }

    public boolean isLogged() {
        return isLogged;
    }
}
