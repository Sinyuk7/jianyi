package com.sinyuk.jianyimaterial.feature.want;

import android.support.annotation.NonNull;

/**
 * Created by Sinyuk on 16.4.20.
 */
public interface IWantView {
    void showContactInfo(@NonNull String tel);

    void onQueryFailed(String message);

    void onUserNotLogged();

    void onPostNeedSucceed(String message);

    void onPostNeedFailed(String message);

    void onPostNeedVolleyError(String message);

    void onUPostNeedParseError(String message);
}
