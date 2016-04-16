package com.sinyuk.jianyimaterial.feature.offer;

/**
 * Created by Sinyuk on 16.4.8.
 */
public interface IOfferView {
    // start upload
    void showUploadProgress();

    void hideUploadProgress();

    void onShotUploadParseError(String message);

    void onShotUploadVolleyError(String message);

    void onShotUploadCompressError(String message);

    void onShotUploadSucceed(String url);

    void onPostGoodsSucceed(String message);

    void onPostGoodsFailed(String message);

    void onPostGoodsVolleyError(String message);

    void onUPostGoodsParseError(String message);
}
