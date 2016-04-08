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
        ShotModel.getInstance(mView).compressThenUpload(str,this);
    }

    @Override
    public void onParseError(String message) {

    }

    @Override
    public void onVolleyError(String message) {

    }

    @Override
    public void onUploaded(String url) {

    }
}
