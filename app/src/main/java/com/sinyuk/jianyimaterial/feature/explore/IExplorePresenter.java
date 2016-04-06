package com.sinyuk.jianyimaterial.feature.explore;

/**
 * Created by Sinyuk on 16.3.27.
 */
public interface IExplorePresenter {
    void selectInCategory(String parentSort, int schoolIndex, int orderIndex, String childSort);

    void selectInTitles(int schoolIndex, int orderIndex);
}
