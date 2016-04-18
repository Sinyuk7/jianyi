package com.sinyuk.jianyimaterial.feature.settings.account;

import com.sinyuk.jianyimaterial.entity.School;

import java.util.List;

/**
 * Created by Sinyuk on 16.4.12.
 */
public interface IAccountView {
    void onUpdateSucceed(String message);

    void onUpdateFailed(String message);

    void onUpdateVolleyError(String message);

    void onUpdateParseError(String message);

    void onLoadSchoolSucceed(List<School> schoolList);

    void onLoadSchoolParseError(String message);

    void onLoadSchoolVolleyError(String message);
}
