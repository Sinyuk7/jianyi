package com.sinyuk.jianyimaterial.feature.info;

/**
 * Created by Sinyuk on 16.4.16.
 */
public interface IInfoView {
    void showProgressDialog(String message);

    void showErrorDialog(String message);

    void showWarningDialog(String message);

    void showSucceedDialog(String message);

    void dismissProgressDialog();

    void onShotUploadParseError(String message);

    void onShotUploadVolleyError(String message);

    void onShotUploadCompressError(String message);

    void onShotUploadSucceed(String url);

    void onUserUpdateSucceed(String message);

    void onUserUpdateFailed(String message);

    void onUserUpdateVolleyError(String message);

    void onUserUpdateParseError(String message);

}
