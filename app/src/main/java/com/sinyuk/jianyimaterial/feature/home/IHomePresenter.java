package com.sinyuk.jianyimaterial.feature.home;

/**
 * Created by Sinyuk on 16.3.27.
 */
public interface IHomePresenter {

    void loadBanner();

    void loadListHeader();

    void toHeaderHistory();

    void loadData(int pageIndex);

    void toPostView();
}
