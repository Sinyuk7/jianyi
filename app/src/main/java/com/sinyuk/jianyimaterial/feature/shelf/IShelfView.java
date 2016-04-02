package com.sinyuk.jianyimaterial.feature.shelf;

import android.support.annotation.NonNull;

import com.sinyuk.jianyimaterial.entity.YihuoProfile;

import java.util.List;

/**
 * Created by Sinyuk on 16.4.2.
 */
public interface IShelfView {

    void refresh();

    void loadData(int pageIndex);

    void onDataLoaded();

    void showList(List<YihuoProfile> newPage,boolean isRefresh);

    void onVolleyError(@NonNull String message);

    void onParseError(@NonNull String message);
}
