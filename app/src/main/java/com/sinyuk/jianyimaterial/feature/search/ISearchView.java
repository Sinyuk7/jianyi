package com.sinyuk.jianyimaterial.feature.search;

import android.support.annotation.NonNull;

import com.sinyuk.jianyimaterial.api.Index;

/**
 * Created by Sinyuk on 16.4.29.
 */
public interface ISearchView {

    void showList(Index newPage, boolean isRefresh);

    void reachLastPage();

    void onVolleyError(@NonNull String message);

    void onParseError(@NonNull String message);
}
