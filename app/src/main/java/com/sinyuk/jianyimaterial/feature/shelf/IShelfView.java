package com.sinyuk.jianyimaterial.feature.shelf;

import android.support.annotation.NonNull;

import com.sinyuk.jianyimaterial.api.Index;
import com.sinyuk.jianyimaterial.entity.YihuoProfile;

import java.util.List;

/**
 * Created by Sinyuk on 16.4.2.
 */
public interface IShelfView {
    void showLoadingProgress();

    void dismissLoadingProgress();

    void refresh();

    void loadData(int pageIndex);

    void showList(Index newPage, boolean isRefresh);

    void showEmptyView();

    void reachLastPage();

    void onVolleyError(@NonNull String message);

    void onParseError(@NonNull String message);
}
