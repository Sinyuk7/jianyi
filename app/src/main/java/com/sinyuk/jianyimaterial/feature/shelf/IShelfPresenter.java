package com.sinyuk.jianyimaterial.feature.shelf;

import java.util.HashMap;

/**
 * Created by Sinyuk on 16.4.2.
 */
public interface IShelfPresenter {
    void loadData(int pageIndex , HashMap<String,String> params);

    void loadData(int pageIndex , String uid);
}
