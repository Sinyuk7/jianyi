package com.sinyuk.jianyimaterial.feature.info;

import java.util.HashMap;

/**
 * Created by Sinyuk on 16.4.16.
 */
public interface IInfoPresenter {

    void compressThenUpload(String uri);

    void loadUserInfo();

    void updateUser(HashMap<String,String> params);
}
