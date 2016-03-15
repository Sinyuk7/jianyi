package com.sinyuk.jianyimaterial.fragments;

import android.support.v4.app.Fragment;

import com.sinyuk.jianyimaterial.R;
import com.sinyuk.jianyimaterial.base.BaseFragment;

/**
 * Created by Sinyuk on 16.2.16.
 */
public class BlankFragment extends BaseFragment{

    @Override
    protected int getContentViewId() {
        return R.layout.hint_post_something;
    }

    @Override
    protected boolean isUsingEventBus() {
        return false;
    }

    @Override
    protected void initViewsAndEvent() {

    }
}
