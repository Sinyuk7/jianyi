package com.sinyuk.jianyimaterial.feature.details;

import android.support.annotation.NonNull;

import com.sinyuk.jianyimaterial.entity.YihuoDetails;

import java.util.List;

/**
 * Created by Sinyuk on 16.3.19.
 */
public interface IDetailsView {
    void setupLikeButton(boolean isAdded);

    void showDescription(String description);

    void showShots(List<YihuoDetails.Pics> shotUrls);

    void showViewCount(@NonNull String count);

    void showComments();

    void hintNoComment();

    void sendComment(String comment);

    void hintAddToLikes();

    void hintRemoveFromLikes();

    void hintRequestLogin();
}
