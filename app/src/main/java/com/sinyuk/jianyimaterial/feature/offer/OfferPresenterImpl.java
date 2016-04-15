package com.sinyuk.jianyimaterial.feature.offer;

import com.sinyuk.jianyimaterial.model.ShotModel;
import com.sinyuk.jianyimaterial.mvp.BasePresenter;

/**
 * Created by Sinyuk on 16.4.8.
 */
public class OfferPresenterImpl extends BasePresenter<OfferView> implements IOfferPresenter, ShotModel.ShotUploadCallback {
    @Override
    public int queryUserSchool() {
        return 0;
    }

    @Override
    public void updateUserSchool(int schoolIndex) {

    }

    @Override
    public void compressThenUpload(String str) {
        ShotModel.getInstance(mView).compressThenUpload(str, this);
    }

    @Override
    public void onUploadParseError(String message) {
        mView.onParseError(message);
    }

    @Override
    public void onUploadVolleyError(String message) {
        mView.onVolleyError(message);
    }

    @Override
    public void onUploadUploaded(String url) {
        mView.onUploadedSucceed(url);
    }

    @Override
    public void onUploadCompressFailed(String message) {
        mView.onCompressError(message);
    }
}
