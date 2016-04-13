package com.sinyuk.jianyimaterial.ui;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by Sinyuk on 16.2.6.
 */
public class HeaderItemSpaceDecoration extends RecyclerView.ItemDecoration {
    private int spanCount;
    private int mSpace;
    private boolean includeEdge;

    public HeaderItemSpaceDecoration(int spanCount, int resId, boolean includeEdge, Context context) {
        this.mSpace = context.getResources().getDimensionPixelOffset(resId);
        this.spanCount = spanCount;
        this.includeEdge = includeEdge;
    }

    public HeaderItemSpaceDecoration(int spanCount, int mSpace, boolean includeEdge) {
        this.spanCount = spanCount;
        this.mSpace = mSpace;
        this.includeEdge = includeEdge;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view); // item position
        int column = (position - 1) % spanCount; // item column

        if (includeEdge) {
            if (position == 0) { // header top edge
                outRect.top = outRect.right = outRect.left = mSpace;
                outRect.bottom =  mSpace; // item bottom

            } else {
                outRect.left = mSpace - column * mSpace / spanCount; // mSpace - column * ((1f / spanCount) * mSpace)
                outRect.right = (column + 1) * mSpace / spanCount; // (column + 1) * ((1f / spanCount) * mSpace)
                outRect.bottom = mSpace; // item bottom

            }
        } else {
    /*        outRect.top = mSpace; // item top
            if (position == 0) { // header top edge
                outRect.right = outRect.left = mSpace;
            } else {
                if (position <= 2)
                    outRect.top =  mSpace;

                outRect.left = column * mSpace / spanCount; // column * ((1f / spanCount) * mSpace)
                outRect.right = mSpace - (column + 1) * mSpace / spanCount; // mSpace - (column + 1) * ((1f /    spanCount) * mSpace)
            }*/
            if (position == 0) { // header top edge
                outRect.top = outRect.right = outRect.left = 0;
                outRect.bottom =  mSpace; // item bottom

            } else {
                outRect.left = mSpace - column * mSpace / spanCount; // mSpace - column * ((1f / spanCount) * mSpace)
                outRect.right = (column + 1) * mSpace / spanCount; // (column + 1) * ((1f / spanCount) * mSpace)
                outRect.bottom = mSpace; // item bottom

            }
        }
    }
}
