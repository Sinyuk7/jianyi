package com.sinyuk.jianyimaterial.feature.settings.account;

/**
 * Created by Sinyuk on 16.4.12.
 */
public interface IAccountView {
    void onUpdateSucceed(String message);

    void onUpdateFailed(String message);

    void onUpdateVolleyError(String message);

    void onUpdateParseError(String message);
}
