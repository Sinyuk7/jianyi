package com.sinyuk.jianyimaterial.feature.drawer;

import android.support.annotation.NonNull;

import com.sinyuk.jianyimaterial.entity.User;

/**
 * Created by Sinyuk on 16.3.30.
 */
public interface IDrawerPresenter {

    void queryCurrentUser();

    void onUserInfoClick();
}
