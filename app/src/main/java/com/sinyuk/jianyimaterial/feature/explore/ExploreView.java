package com.sinyuk.jianyimaterial.feature.explore;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.sinyuk.jianyimaterial.R;
import com.sinyuk.jianyimaterial.mvp.BaseActivity;
import com.sinyuk.jianyimaterial.widgets.flowlayout.FlowLayout;
import com.sinyuk.jianyimaterial.widgets.flowlayout.TagAdapter;
import com.sinyuk.jianyimaterial.widgets.flowlayout.TagFlowLayout;

import butterknife.Bind;

/**
 * Created by Sinyuk on 16.3.27.
 */
public class ExploreView extends BaseActivity<ExplorePresenterImpl> implements IExploreView {
    public static final int[] PARENT_SORT_LIST = new int[]{
            R.array.Clothing,
            R.array.Office,
            R.array.Home,
            R.array.Makeup,
            R.array.Sports,
            R.array.Bicycles,
            R.array.Electronics,
            R.array.Books,
            R.array.Cards,
            R.array.Bags,
            R.array.Snacks,
    };
    private static final String PARENT_SORT = "title";
    private final String[] sOrderArray = new String[]{
            "时间↓",
            "时间↑",
            "价格↓",
            "价格↑"};

    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.app_bar_layout)
    AppBarLayout mAppBarLayout;
    @Bind(R.id.list_fragment_container)
    FrameLayout mListFragmentContainer;
    @Bind(R.id.school_tags)
    TagFlowLayout mSchoolTags;
    @Bind(R.id.order_tags)
    TagFlowLayout mOrderTags;
    @Bind(R.id.child_sort_tags)
    TagFlowLayout mChildSortTags;
    @Bind(R.id.bottom_sheet)
    NestedScrollView mBottomSheet;
    @Bind(R.id.coordinator_layout)
    CoordinatorLayout mCoordinatorLayout;
    private int mParentSortIndex = 0;
    private String mTitle;
    private String[] mSchoolArray;
    private String[] mChildSortArray;
    private BottomSheetBehavior<NestedScrollView> mBottomSheetBehavior;

    @Override
    protected boolean isUseEventBus() {
        return false;
    }

    @Override
    protected void beforeInflate() {
        if (null != getIntent()) { configParentSort(getIntent().getExtras()); }

        mSchoolArray = getResources().getStringArray(R.array.schools_sort);


    }

    private void configParentSort(Bundle extras) {
     /*   mParentSort = extras.getString(PARENT_SORT);
        if (TextUtils.isEmpty(mParentSort)) {
            mTitle = "易货分类";
            mParentSort = "最近上新";
        } else {
            mTitle = mParentSort;
        }*/
        mChildSortArray = getResources().getStringArray(PARENT_SORT_LIST[mParentSortIndex]);
    }

    @Override
    protected ExplorePresenterImpl createPresenter() {
        return new ExplorePresenterImpl();
    }

    @Override
    protected boolean isNavAsBack() {
        return false;
    }

    @Override
    protected void onFinishInflate() {
        setupToolbarTitle();
        initFragment();
        setupBottomSheet();
        setupFlowLayout();
    }

    @Override
    protected int getContentViewID() {
        return R.layout.explore_view;
    }


    private void setupToolbarTitle() {

    }

    private void initFragment() {
        mBottomSheetBehavior = BottomSheetBehavior.from(mBottomSheet);
        mBottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                // React to state change
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                // React to dragging events
            }
        });
    }

    private void setupBottomSheet() {

    }

    private void setupFlowLayout() {

        mSchoolTags.setMaxSelectCount(1); // disallowed multiSelected

        TagAdapter<String> schoolTagAdapter = new TagAdapter<String>(mSchoolArray) {
            @Override
            public View getView(FlowLayout parent, int position, String s) {

                TextView tv = (TextView) getLayoutInflater().inflate(R.layout.item_tag,
                        mSchoolTags, false);
                tv.setText(s);
                return tv;
            }
        };
        mSchoolTags.setAdapter(schoolTagAdapter);
        // TODO: suan le 2333
        schoolTagAdapter.setSelectedList(0); // default school selected is ZJCM xiasha


        // sortOrder
        mOrderTags.setMaxSelectCount(1);
        TagAdapter<String> orderTagAdapter = new TagAdapter<String>(sOrderArray) {

            @Override
            public View getView(FlowLayout parent, int position, String s) {

                TextView tv = (TextView) getLayoutInflater().inflate(R.layout.item_tag,
                        mOrderTags, false);
                tv.setText(s);
                return tv;
            }
        };
        mOrderTags.setAdapter(orderTagAdapter);
        orderTagAdapter.setSelectedList(0); // default time_desc


        // child Sort
        mChildSortTags.setMaxSelectCount(1); // multiSelected
        TagAdapter<String> childSortTagAdapter = new TagAdapter<String>(mChildSortArray) {
            @Override
            public View getView(FlowLayout parent, int position, String s) {

                TextView tv = (TextView) getLayoutInflater().inflate(R.layout.item_tag,
                        mChildSortTags, false);
                tv.setText(s);
                return tv;
            }
        };
        mChildSortTags.setAdapter(childSortTagAdapter);


        // selected listener
        mSchoolTags.setOnTagClickListener((view, position, parent) -> {
           mSchoolPostion = position;
            return false;
        });


        mOrderTags.setOnTagClickListener((view, position, parent) -> {
            mOrderPostion = position;
            return false;
        });

        mChildSortTags.setOnTagClickListener((view, position, parent) -> {
           mChildSortPosition = position;
            return false;
        });

    }

    @Override
    public void onBackPressed() {
        if (mBottomSheetBehavior.getState() != BottomSheetBehavior.STATE_HIDDEN) {
            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        } else {
            super.onBackPressed();
        }
    }
}
