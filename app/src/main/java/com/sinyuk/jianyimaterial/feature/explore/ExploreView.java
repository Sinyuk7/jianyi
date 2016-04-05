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
import com.sinyuk.jianyimaterial.events.XSelectionUpdateEvent;
import com.sinyuk.jianyimaterial.mvp.BaseActivity;
import com.sinyuk.jianyimaterial.utils.StringUtils;
import com.sinyuk.jianyimaterial.widgets.flowlayout.FlowLayout;
import com.sinyuk.jianyimaterial.widgets.flowlayout.TagAdapter;
import com.sinyuk.jianyimaterial.widgets.flowlayout.TagFlowLayout;

import org.greenrobot.eventbus.EventBus;

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
    public static final String PARENT_SORT = "title";
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
    private int mParentSortIndex;
    private String mTitle;
    private String[] mSchoolArray;
    private String[] mChildSortArray;
    private BottomSheetBehavior<NestedScrollView> mBottomSheetBehavior;
    private int mOldSchoolPosition;
    private int mOldOrderPosition;
    private int mOldChildSortPosition;

    private int mNewSchoolPosition;
    private int mNewOrderPosition;
    private int mNewChildSortPosition;

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
        if (null == extras) { return; }
        mParentSortIndex = extras.getInt(PARENT_SORT, 0);
        mTitle = StringUtils.check(this, getResources().getStringArray(R.array.category_menu_items)[mParentSortIndex], R.string.activity_category_page);
        mChildSortArray = getResources().getStringArray(PARENT_SORT_LIST[mParentSortIndex]);
    }

    @Override
    protected ExplorePresenterImpl createPresenter() {
        return new ExplorePresenterImpl();
    }

    @Override
    protected boolean isNavAsBack() {
        return true;
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
        if (null != getSupportActionBar()) { getSupportActionBar().setTitle(mTitle); }
    }

    private void initFragment() {

    }

    private void setupBottomSheet() {
        mBottomSheetBehavior = BottomSheetBehavior.from(mBottomSheet);
        mBottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                // React to state change
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    if (mNewChildSortPosition == mOldChildSortPosition &&
                            mNewOrderPosition == mOldOrderPosition &&
                            mNewSchoolPosition == mOldSchoolPosition) { return; }
                    EventBus.getDefault().post(new XSelectionUpdateEvent(mNewSchoolPosition,
                            mNewOrderPosition, mNewChildSortPosition));
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                // React to dragging events
            }
        });
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
            mOldSchoolPosition = mNewSchoolPosition;
            mNewSchoolPosition = position;
            return false;
        });


        mOrderTags.setOnTagClickListener((view, position, parent) -> {
            mOldSchoolPosition = mNewSchoolPosition;
            mNewOrderPosition = position;
            return false;
        });

        mChildSortTags.setOnTagClickListener((view, position, parent) -> {
            mOldChildSortPosition = mNewSchoolPosition;
            mNewChildSortPosition = position;
            return false;
        });

    }
}
