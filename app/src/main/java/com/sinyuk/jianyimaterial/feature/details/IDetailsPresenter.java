package com.sinyuk.jianyimaterial.feature.details;

import android.support.annotation.NonNull;

import com.sinyuk.jianyimaterial.entity.YihuoDetails;
import com.sinyuk.jianyimaterial.model.YihuoModel;

/**
 * Created by Sinyuk on 16.3.19.
 */
public interface IDetailsPresenter {

    void loadYihuoDetails(@NonNull String yihuoId);

    void getLikesState(@NonNull String yihuoId);

    void addToLikes(@NonNull YihuoDetails data);

    void removeFromLikes(@NonNull YihuoDetails data);
}
