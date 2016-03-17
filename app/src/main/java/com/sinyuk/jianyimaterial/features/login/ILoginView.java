package com.sinyuk.jianyimaterial.features.login;

/**
 * Created by Sinyuk on 16.3.16.
 */
public interface ILoginView {
    void showProgress();
    void hideProgress();
    void onLoginSucceed();
    void onLoginFailed(String message);
    void onNetworkError(String message);
}
