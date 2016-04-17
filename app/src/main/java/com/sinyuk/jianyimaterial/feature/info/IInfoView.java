package com.sinyuk.jianyimaterial.feature.info;

/**
 * Created by Sinyuk on 16.4.16.
 */
public interface IInfoView {
    void showProgressDialog();

    void showUserAvatar(String url);

    void showUserNickname(String nickname);

    void showUserSchool(String schoolIndex);

    void onQuerySucceed();

    void onQueryFailed(String message);

    void onUserNotLogged();

    void onShotUploadParseError(String message);

    void onShotUploadVolleyError(String message);

    void onShotUploadCompressError(String message);

    void onShotUploadSucceed(String url);

    void onUserUpdateSucceed(String message);

    void onUserUpdateFailed(String message);

    void onUserUpdateVolleyError(String message);

    void onUserUpdateParseError(String message);

}
