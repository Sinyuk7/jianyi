package com.sinyuk.jianyimaterial.feature.homelist;

import android.support.annotation.NonNull;

import com.sinyuk.jianyimaterial.entity.YihuoProfile;

import java.util.List;

/**
 * Created by Sinyuk on 16.3.27.
 */
public interface IHomeListView {

    void loadListHeader();

    void showListHeader(YihuoProfile data);

    void refresh();

    void loadData(int pageIndex);

    void showList(List<YihuoProfile> newPage,boolean isRefresh);

    void hintVolleyError(@NonNull String message);

    void hintParseError(@NonNull String message);
}
