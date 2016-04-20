package com.sinyuk.jianyimaterial.feature.info;

import android.text.TextUtils;

import com.sinyuk.jianyimaterial.entity.User;
import com.sinyuk.jianyimaterial.model.ShotModel;
import com.sinyuk.jianyimaterial.model.UserModel;
import com.sinyuk.jianyimaterial.mvp.BasePresenter;

import java.util.HashMap;

/**
 * Created by Sinyuk on 16.4.16.
 */
public class InfoPresenterImpl extends BasePresenter<InfoView> implements IInfoPresenter, ShotModel.ShotUploadCallback {
    @Override
    public void compressThenUpload(String uri) {
        ShotModel.getInstance(mView).compressThenUpload(uri,this);
    }

    @Override
    public void updateUser(HashMap<String, String> params) {

    }

    @Override
    public void onUploadParseError(String message) {
        mView.onShotUploadParseError(message);
    }

    @Override
    public void onUploadVolleyError(String message) {
        mView.onShotUploadVolleyError(message);
    }

    @Override
    public void onUploadUploaded(String url) {
        mView.onShotUploadSucceed(url);
    }

    @Override
    public void onUploadCompressFailed(String message) {
        mView.onShotUploadCompressError(message);
    }
}
