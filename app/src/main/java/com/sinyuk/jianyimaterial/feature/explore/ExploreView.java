package com.sinyuk.jianyimaterial.feature.explore;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.flipboard.bottomsheet.BottomSheetLayout;
import com.sinyuk.jianyimaterial.R;
import com.sinyuk.jianyimaterial.mvp.BaseActivity;
import com.sinyuk.jianyimaterial.ui.InsetViewTransformer;
import com.sinyuk.jianyimaterial.utils.ToastUtils;
import com.sinyuk.jianyimaterial.widgets.flowlayout.FlowLayout;
import com.sinyuk.jianyimaterial.widgets.flowlayout.TagAdapter;
import com.sinyuk.jianyimaterial.widgets.flowlayout.TagFlowLayout;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by Sinyuk on 16.3.27.
 */
public class ExploreView extends BaseActivity<ExplorePresenterImpl> implements IExploreView, BottomSheetLayout.OnSheetStateChangeListener {
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
    public static final String PARENT_SORT = "sort";
    public static final String EXPLORE_TITLE = "title";
    private final String[] mOrderArray = new String[]{"时间↓", "时间↑", "价格↓", "价格↑"};
    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.app_bar_layout)
    AppBarLayout mAppBarLayout;
    @Bind(R.id.list_fragment_container)
    FrameLayout mListFragmentContainer;
    @Bind(R.id.coordinator_layout)
    CoordinatorLayout mCoordinatorLayout;
    @Bind(R.id.bottom_sheet_layout)
    BottomSheetLayout mBottomSheetLayout;

    TagFlowLayout mSchoolTags;
    TagFlowLayout mOrderTags;
    TagFlowLayout mChildSortTags;

    TextView mChildSortTitle;
    @Bind(R.id.back_btn)
    ImageView mBackBtn;
    @Bind(R.id.filter_btn)
    ImageView mFilterBtn;

    private int mParentSortIndex;
    private String mTitle;
    private String[] mSchoolArray;
    private String[] mChildSortArray;

    private int mOldSchoolPosition = 0;
    private int mOldOrderPosition = 0;
    private int mOldChildSortPosition = 0;

    private int mNewSchoolPosition = 0;
    private int mNewOrderPosition = 0;
    private int mNewChildSortPosition = 0;
    private View mFlowLayout;

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
        mTitle = extras.getString(EXPLORE_TITLE);
        if (TextUtils.isEmpty(mTitle)) {
            mParentSortIndex = extras.getInt(PARENT_SORT);
            mTitle = getResources().getStringArray(R.array.category_menu_items)[mParentSortIndex];
            mChildSortArray = getResources().getStringArray(PARENT_SORT_LIST[mParentSortIndex]);
        }
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
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(mTitle);
    }

    private void initFragment() {

    }

    private void setupBottomSheet() {
        mFlowLayout = LayoutInflater.from(this).inflate(R.layout.explore_view_flow_layout, mBottomSheetLayout, false);
        mSchoolTags = (TagFlowLayout) mFlowLayout.findViewById(R.id.school_tags);
        mOrderTags = (TagFlowLayout) mFlowLayout.findViewById(R.id.order_tags);
        mChildSortTags = (TagFlowLayout) mFlowLayout.findViewById(R.id.child_sort_tags);
        mChildSortTitle = (TextView) mFlowLayout.findViewById(R.id.child_sort_title);

        mBottomSheetLayout.setUseHardwareLayerWhileAnimating(true);
        mBottomSheetLayout.setShouldDimContentView(true);
        mBottomSheetLayout.setPeekOnDismiss(false);

        mBottomSheetLayout.addOnSheetStateChangeListener(this);

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
        TagAdapter<String> orderTagAdapter = new TagAdapter<String>(mOrderArray) {

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


        if (null != mChildSortArray) {
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
            mChildSortTags.setOnTagClickListener((view, position, parent) -> {
                mOldChildSortPosition = mNewSchoolPosition;
                mNewChildSortPosition = position;
                return false;
            });
        } else {
            mChildSortTags.setVisibility(View.GONE);
            mChildSortTitle.setVisibility(View.GONE);
        }

        // selected listener
        mSchoolTags.setOnTagClickListener((view, position, parent) -> {
            mOldSchoolPosition = mNewSchoolPosition;
            mNewSchoolPosition = position;
            mBottomSheetLayout.dismissSheet();
            return false;
        });


        mOrderTags.setOnTagClickListener((view, position, parent) -> {
            mOldOrderPosition = mNewOrderPosition;
            mNewOrderPosition = position;
            mBottomSheetLayout.dismissSheet();
            return false;
        });
    }


    public void confirm() {

        if (mNewChildSortPosition == mOldChildSortPosition &&
                mNewOrderPosition == mOldOrderPosition &&
                mNewSchoolPosition == mOldSchoolPosition) { return; }

        if (null != mChildSortArray) {
            if (mSchoolArray[mNewSchoolPosition] != null &&
                    mOrderArray[mNewOrderPosition] != null &&
                    mChildSortArray[mNewChildSortPosition] != null) {
                mPresenter.selectInCategory(
                        mTitle,
                        mNewSchoolPosition,
                        mNewOrderPosition,
                        mChildSortArray[mNewChildSortPosition]);
            }
        } else {
            if (mSchoolArray[mNewSchoolPosition] != null &&
                    mOrderArray[mNewOrderPosition] != null) {
                mPresenter.selectInTitles(
                        mNewSchoolPosition,
                        mNewOrderPosition);
            }
        }
        ToastUtils.toastSlow(this, "confirm");
    }

    public void cancel() {
        // reset
        mNewChildSortPosition = mOldChildSortPosition;
        mNewSchoolPosition = mOldSchoolPosition;
        mNewOrderPosition = mOldOrderPosition;
        ToastUtils.toastSlow(this, "cancel");
    }

    @OnClick({R.id.back_btn, R.id.filter_btn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_btn:
                onBackPressed();
                break;
            case R.id.filter_btn:
                if (mBottomSheetLayout.isSheetShowing()) {
                    mBottomSheetLayout.dismissSheet();
                } else {
                    mBottomSheetLayout.showWithSheetView(mFlowLayout, new InsetViewTransformer());
                }
                break;
        }
    }

    @Override
    public void onSheetStateChanged(BottomSheetLayout.State state) {
        if (state == BottomSheetLayout.State.EXPANDED) {
            mBackBtn.setBackgroundResource(R.drawable.ic_close_white_24dp);
            mFilterBtn.setBackgroundResource(R.drawable.ic_check_primary_24dp);
        }
        if (state == BottomSheetLayout.State.HIDDEN) {
            mBackBtn.setBackgroundResource(R.drawable.ic_arrow_back_primary_24dp);
            mFilterBtn.setBackgroundResource(R.drawable.ic_sort_white_24dp);
        }
    }
}
