package com.sinyuk.jianyimaterial.feature.offer;

import android.support.annotation.NonNull;

import java.util.HashMap;

/**
 * Created by Sinyuk on 16.4.8.
 */
public interface IOfferPresenter {

    void compressThenUpload(String uri);

    void post(@NonNull HashMap<String,String> urls, @NonNull String title, @NonNull String details, @NonNull String price, @NonNull String sort, String childSort);
}
