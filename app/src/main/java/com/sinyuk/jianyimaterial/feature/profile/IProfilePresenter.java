package com.sinyuk.jianyimaterial.feature.profile;

import android.support.annotation.NonNull;

/**
 * Created by Sinyuk on 16.4.10.
 */
public interface IProfilePresenter {
    void queryCurrentUser();

    void fetchSchoolList();

    void unShelf(@NonNull String goodsId, @NonNull String reason);

    void onShelf(@NonNull String goodsId);
}
