package com.sinyuk.jianyimaterial.feature.register;

import android.support.annotation.NonNull;

/**
 * Created by Sinyuk on 16.3.19.
 */
public interface IRegisterPresenter {
    void askForAuthenticode(@NonNull String tel);

    void checkForAuthenticode(@NonNull String tel, @NonNull String authenticode);

    void attemptRegister(@NonNull String tel, @NonNull String password);

    void fetchSchools();

}
