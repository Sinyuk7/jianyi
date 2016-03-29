package com.sinyuk.jianyimaterial.feature.homelist;

/**
 * Created by Sinyuk on 16.3.27.
 */
public interface IHomeListPresenter {

    void loadListHeader();

    void toHeaderHistory();

    void loadData(int pageIndex,boolean isRefresh);
}
