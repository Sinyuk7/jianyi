package com.sinyuk.jianyimaterial.utils;

import android.graphics.Rect;
import android.view.View;

/**
 * Created by Sinyuk on 15.12.24.
 */
public class ViewUtils {

    /**
     *  Determines if two views intersect in the window.
     * @param view1
     * @param view2
     * @return 两个View之间是否香蕉
     *
     */
    public static boolean viewsIntersect(View view1, View view2) {
        final int[] view1Loc = new int[2];
        view1.getLocationOnScreen(view1Loc);
        final Rect view1Rect = new Rect(view1Loc[0],
                view1Loc[1],
                view1Loc[0] + view1.getWidth(),
                view1Loc[1] + view1.getHeight());
        int[] view2Loc = new int[2];
        view2.getLocationOnScreen(view2Loc);
        final Rect view2Rect = new Rect(view2Loc[0],
                view2Loc[1],
                view2Loc[0] + view2.getWidth(),
                view2Loc[1] + view2.getHeight());
        return view1Rect.intersect(view2Rect);
    }
}
