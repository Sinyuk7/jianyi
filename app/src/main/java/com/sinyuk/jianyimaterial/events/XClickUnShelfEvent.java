package com.sinyuk.jianyimaterial.events;

import android.view.View;

import com.sinyuk.jianyimaterial.widgets.CheckableImageView;

/**
 * Created by Sinyuk on 16.4.21.
 */
public class XClickUnShelfEvent {
    private View itemView;
    private int position;
    public XClickUnShelfEvent(View view, int position) {
        this.itemView = view;
        this.position = position;
    }

    public int getPosition() {
        return position;
    }

    public View getItemView() {
        return itemView;
    }
}
