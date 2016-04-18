package com.sinyuk.jianyimaterial.feature.settings.account;

import com.sinyuk.jianyimaterial.entity.School;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Sinyuk on 16.4.12.
 */
public interface IAccountPresenter {
    void logout();

    void update(HashMap<String,String> params);

    void fetchSchoolList();
}
