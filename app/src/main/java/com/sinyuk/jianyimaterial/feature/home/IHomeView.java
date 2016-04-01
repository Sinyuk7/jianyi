package com.sinyuk.jianyimaterial.feature.home;

import android.support.annotation.NonNull;

import com.sinyuk.jianyimaterial.entity.Banner;
import com.sinyuk.jianyimaterial.entity.YihuoDetails;
import com.sinyuk.jianyimaterial.entity.YihuoProfile;

import java.util.List;

/**
 * Created by Sinyuk on 16.3.27.
 */
public interface IHomeView {

    void showBanner(List<Banner> bannerList);

    void showListHeader(YihuoDetails data);

    void refresh();

    void loadData(int pageIndex);

    void onDataLoaded();

    void showList(List<YihuoProfile> newPage,boolean isRefresh);

    void onVolleyError(@NonNull String message);

    void onParseError(@NonNull String message);

    void toPostView();

    void toLoginView();
}
