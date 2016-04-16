package com.sinyuk.jianyimaterial.feature.offer;

import android.support.annotation.NonNull;

import com.sinyuk.jianyimaterial.model.ShotModel;
import com.sinyuk.jianyimaterial.model.UserModel;
import com.sinyuk.jianyimaterial.mvp.BasePresenter;

import java.util.HashMap;

/**
 * Created by Sinyuk on 16.4.8.
 */
public class OfferPresenterImpl extends BasePresenter<OfferView> implements IOfferPresenter, ShotModel.ShotUploadCallback, UserModel.PostGoodsCallback {

    @Override
    public void compressThenUpload(String str) {
        ShotModel.getInstance(mView).compressThenUpload(str, this);
    }

    @Override
    public void post(@NonNull HashMap<String,String> urls,@NonNull String title, @NonNull String details, @NonNull String price, @NonNull String sort, String childSort) {
        UserModel.getInstance(mView).postGoods(urls,title,details,price,sort,childSort,this);
        mView.showUploadProgress();
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

    @Override
    public void onPostGoodsSucceed(String message) {
        mView.onPostGoodsSucceed(message);
    }

    @Override
    public void onPostGoodsFailed(String message) {
        mView.onPostGoodsFailed(message);
    }

    @Override
    public void onPostGoodsVolleyError(String message) {
        mView.onPostGoodsVolleyError(message);
    }

    @Override
    public void onUPostGoodsParseError(String message) {
        mView.onUPostGoodsParseError(message);
    }
}
