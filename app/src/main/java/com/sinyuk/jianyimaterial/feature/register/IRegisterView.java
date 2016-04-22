package com.sinyuk.jianyimaterial.feature.register;

import com.sinyuk.jianyimaterial.entity.School;
import com.sinyuk.jianyimaterial.entity.User;

import java.util.List;

/**
 * Created by Sinyuk on 16.3.19.
 */
public interface IRegisterView {
    void showProgressDialog();

    void showErrorDialog(String message);

    void showWarningDialog(String message);

    void showSucceedDialog(User user);

    void hintRegisterCompleted();

    void onLoadSchoolSucceed(List<School> schoolList);
}
