package com.sinyuk.jianyimaterial.events;

/**
 * Created by Sinyuk on 16.4.25.
 */
public class XOnShelfEvent {
    private final String id;

    public String getId() {
        return id;
    }

    public XOnShelfEvent(String id) {
        this.id = id;
    }
}
