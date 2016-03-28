package com.sinyuk.jianyimaterial.feature.list;

/**
 * Created by Sinyuk on 16.3.27.
 */
public interface IListPresenter {
    void loadMore(int pageIndex);

    default void refresh() {
        loadMore(/**pageIndex = 1**/1);
    }
}
