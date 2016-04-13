package com.sinyuk.jianyimaterial.feature.settings.account;

import com.sinyuk.jianyimaterial.model.UserModel;
import com.sinyuk.jianyimaterial.mvp.BasePresenter;

/**
 * Created by Sinyuk on 16.4.12.
 */
public class AccountPresenterImpl extends BasePresenter<AccountView> implements IAccountPresenter{

    @Override
    public void logout() {
        UserModel.getInstance(mView.getContext()).logout();
    }
}
