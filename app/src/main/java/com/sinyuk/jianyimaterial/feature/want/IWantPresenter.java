package com.sinyuk.jianyimaterial.feature.want;

import android.support.annotation.NonNull;

/**
 * Created by Sinyuk on 16.4.20.
 */
public interface IWantPresenter {
    void queryCurrentUser();

    void post(@NonNull String detail, @NonNull String tel, @NonNull String price);
}
