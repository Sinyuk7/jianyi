package com.sinyuk.jianyimaterial.feature.register;

import android.support.annotation.NonNull;

/**
 * Created by Sinyuk on 16.3.19.
 */
public interface IRegisterPresenter {
    void requestAuthenticode(@NonNull String tel);
    void attemptRegister(@NonNull String tel, @NonNull String password);

}
