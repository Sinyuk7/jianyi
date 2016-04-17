package com.sinyuk.jianyimaterial.feature.settings.account;

import java.util.HashMap;

/**
 * Created by Sinyuk on 16.4.12.
 */
public interface IAccountPresenter {
    void logout();

    void update(HashMap<String,String> params);
}
