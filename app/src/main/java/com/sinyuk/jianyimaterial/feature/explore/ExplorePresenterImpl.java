package com.sinyuk.jianyimaterial.feature.explore;

import com.sinyuk.jianyimaterial.mvp.BasePresenter;
import com.sinyuk.jianyimaterial.utils.LogUtils;

/**
 * Created by Sinyuk on 16.3.27.
 */
public class ExplorePresenterImpl extends BasePresenter implements IExplorePresenter {
    private String generateUrl(String school, String order, String childSort) {

        return null;
    }

    @Override
    public void selectInCategory(String parentSort, int schoolIndex, int orderIndex, String childSort) {
        LogUtils.simpleLog(ExplorePresenterImpl.class, "selectInCategory" +
                "\n" + parentSort + "\n" + schoolIndex + "\n" + orderIndex + "\n" + childSort);
    }

    @Override
    public void selectInTitles(int schoolIndex, int orderIndex) {
        LogUtils.simpleLog(ExplorePresenterImpl.class, "selectInTitles" + "\n" + schoolIndex + "\n" + orderIndex);
    }
}
