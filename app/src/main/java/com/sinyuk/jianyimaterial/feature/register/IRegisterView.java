package com.sinyuk.jianyimaterial.feature.register;

/**
 * Created by Sinyuk on 16.3.19.
 */
public interface IRegisterView {
    void hintRegisterProcessing();

    void hintRegisterError(String message);

    void hintRegisterFailed(String message);

    void hintRegisterSucceed();

    void hintRegisterCompleted();
}
