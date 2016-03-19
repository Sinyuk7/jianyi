package com.sinyuk.jianyimaterial.feature.login;

import android.support.annotation.NonNull;

/**
 * Created by Sinyuk on 16.3.17.
 */
public interface ILoginPresenter {
        void attemptLogin(@NonNull String userName, @NonNull String password);
}
