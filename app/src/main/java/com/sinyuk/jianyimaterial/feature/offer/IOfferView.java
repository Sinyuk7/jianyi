package com.sinyuk.jianyimaterial.feature.offer;

/**
 * Created by Sinyuk on 16.4.8.
 */
public interface IOfferView {
    // start upload
    void showUploadProgress();

    void hideUploadProgress();

    void onParseError(String message);

    void onVolleyError(String message);

    void onUploaded(String url);
}
