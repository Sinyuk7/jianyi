package com.sinyuk.jianyimaterial.feature.search;

import android.support.annotation.NonNull;

/**
 * Created by Sinyuk on 16.4.29.
 */
public interface ISearchPresenter {
    void loadData(@NonNull String param,@NonNull int pageIndex);
}
