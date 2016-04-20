package com.sinyuk.jianyimaterial.feature.profile;

import android.support.annotation.NonNull;

import com.sinyuk.jianyimaterial.entity.School;

import java.util.List;

/**
 * Created by Sinyuk on 16.4.10.
 * 个人主页
 * 分成自己的和别人的
 */
public interface IProfileView {
    void showUsername(@NonNull String username);

    void showAvatar(@NonNull String url);

    void showBackdrop(@NonNull String url);

    void showLocation(@NonNull String schoolName);

    void showToolbarTitle(@NonNull String username);

    void onQueryFailed(String message);

    void onUserNotLogged();

    void onLoadSchoolSucceed(List<School> schoolList);

    void onLoadSchoolParseError(String message);

    void onLoadSchoolVolleyError(String message);
}
