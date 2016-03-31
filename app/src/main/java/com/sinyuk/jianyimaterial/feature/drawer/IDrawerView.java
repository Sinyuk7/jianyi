package com.sinyuk.jianyimaterial.feature.drawer;

import com.sinyuk.jianyimaterial.entity.User;

/**
 * Created by Sinyuk on 16.3.30.
 */
public interface IDrawerView {
    void onLoginStateUpdate(boolean hasLogged);

    void showLoginHeader();

    void showLoggedHeader(User data);

    void showMessageBadge();

    void toPersonalView();

    void toLoginView();

}
