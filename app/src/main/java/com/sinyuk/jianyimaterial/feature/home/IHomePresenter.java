package com.sinyuk.jianyimaterial.feature.home;

import android.support.annotation.NonNull;

/**
 * Created by Sinyuk on 16.3.27.
 */
public interface IHomePresenter {

    void loadBanner();

    void loadListHeader();

    void toHeaderHistory();

    void loadData(@NonNull String schoolIndex, int pageIndex);

    void attemptToOfferView();
}
