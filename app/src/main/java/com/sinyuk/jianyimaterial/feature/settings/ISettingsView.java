package com.sinyuk.jianyimaterial.feature.settings;

import com.sinyuk.jianyimaterial.entity.User;

/**
 * Created by Sinyuk on 16.4.12.
 */
public interface ISettingsView {

    void onQuerySucceed(User currentUser);


    void onQueryFailed(String message);


    void onUserNotLogged();
}
