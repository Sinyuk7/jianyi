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
import com.flipboard.bottomsheet.OnSheetDismissedListener;
import com.sinyuk.jianyimaterial.R;
import com.sinyuk.jianyimaterial.api.JianyiApi;
import com.sinyuk.jianyimaterial.events.XShelfChangeEvent;
import com.sinyuk.jianyimaterial.feature.shelf.ShelfView;
import com.sinyuk.jianyimaterial.mvp.BaseActivity;
import com.sinyuk.jianyimaterial.ui.InsetViewTransformer;
import com.sinyuk.jianyimaterial.utils.LogUtils;
import com.sinyuk.jianyimaterial.widgets.flowlayout.FlowLayout;
import com.sinyuk.jianyimaterial.widgets.flowlayout.TagAdapter;
import com.sinyuk.jianyimaterial.widgets.flowlayout.TagFlowLayout;

import org.greenrobot.eventbus.EventBus;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by Sinyuk on 16.3.27.
 */
public class ExploreView extends BaseActivity<ExplorePresenterImpl> implements OnSheetDismissedListener {
    public static final String TITLE = "title"; // title 和 category 必然要传递过来一个
    public static final String CATEGORY = "category"; //
    public static final String ENABLE_FILTER = "enable_filter";
    public static final String ENABLE_SCHOOL = "enable_school";
    public static final String ENABLE_ORDER = "enable_order";
    public static final String ENABLE_CHILD_SORT = "enable_child_sort";

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

    @Bind(R.id.filter_btn)
    ImageView mFilterBtn;

    private int mParentSortIndex;
    private String mTitle;
    private String[] mSchoolArray;
    private String[] mChildSortArray;

    private View mFlowLayout;
    private String mUrl;

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
        mTitle = extras.getString(TITLE);
        if (TextUtils.isEmpty(mTitle)) {
            mParentSortIndex = extras.getInt(CATEGORY);
            mTitle = getResources().getStringArray(R.array.category_menu_items)[mParentSortIndex];
        }
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
        if (getIntent().getExtras().getBoolean(ENABLE_FILTER, false)) {
            setupBottomSheet();
            setupFlowLayout();
        } else {
            mFilterBtn.setVisibility(View.GONE);
        }
    }

    @Override
    protected int getContentViewID() {
        return R.layout.explore_view;
    }


    private void setupToolbarTitle() {
        if (TextUtils.isEmpty(mTitle)) { return; }
        String toolbarTitle;
        switch (mTitle) {
            case "new":
                toolbarTitle = "今日上进";
                break;
            case "free":
                toolbarTitle = "免费专区";
                break;
            case "hot":
                toolbarTitle = "小编推荐";
                break;
            default:
                toolbarTitle = mTitle;
                break;
        }
        if (getSupportActionBar() != null) { getSupportActionBar().setTitle(toolbarTitle); }
    }

    private void initFragment() {
        mUrl = new JianyiApi.YihuoProfileBuilder(mTitle).addSort("all").getUrl();
        Bundle args = new Bundle();
        args.putString(ShelfView.URL_WHEN_INIT, mUrl);
        ShelfView mShelfView = ShelfView.newInstance(args);
        getSupportFragmentManager().beginTransaction().add(R.id.list_fragment_container, mShelfView).commit();
    }

    private void setupBottomSheet() {
        mFlowLayout = LayoutInflater.from(this).inflate(R.layout.explore_view_flow_layout, mBottomSheetLayout, false);
        mBottomSheetLayout.setUseHardwareLayerWhileAnimating(true);
        mBottomSheetLayout.setShouldDimContentView(true);
    }

    private void setupFlowLayout() {
        if (getIntent().getExtras().getBoolean(ENABLE_SCHOOL, false)) {
            mSchoolTags = (TagFlowLayout) mFlowLayout.findViewById(R.id.school_tags);
            mSchoolTags.setMaxSelectCount(1); // disallowed multiSelected
            TagAdapter<String> mSchoolTagAdapter = new TagAdapter<String>(mSchoolArray) {
                @Override
                public View getView(FlowLayout parent, int position, String s) {
                    TextView tv = (TextView) getLayoutInflater().inflate(R.layout.item_tag, mSchoolTags, false);
                    tv.setText(s);
                    return tv;
                }
            };
            mSchoolTags.setAdapter(mSchoolTagAdapter);
        } else {
            TextView schoolTitle = (TextView) mFlowLayout.findViewById(R.id.school_title);
            schoolTitle.setVisibility(View.GONE);
        }

        if (getIntent().getExtras().getBoolean(ENABLE_ORDER, false)) {
            mOrderTags = (TagFlowLayout) mFlowLayout.findViewById(R.id.order_tags);
            mOrderTags.setMaxSelectCount(1);
            TagAdapter<String> mOrderTagAdapter = new TagAdapter<String>(mOrderArray) {
                @Override
                public View getView(FlowLayout parent, int position, String s) {
                    TextView tv = (TextView) getLayoutInflater().inflate(R.layout.item_tag, mOrderTags, false);
                    tv.setText(s);
                    return tv;
                }
            };
            mOrderTags.setAdapter(mOrderTagAdapter);
        } else {
            TextView orderTitle = (TextView) mFlowLayout.findViewById(R.id.order_title);
            orderTitle.setVisibility(View.GONE);
        }

        if (getIntent().getExtras().getBoolean(ENABLE_CHILD_SORT, false)) {
            mChildSortTags = (TagFlowLayout) mFlowLayout.findViewById(R.id.child_sort_tags);
            mChildSortArray = getResources().getStringArray(PARENT_SORT_LIST[mParentSortIndex]);
            mChildSortTags.setMaxSelectCount(1); // multiSelected
            TagAdapter<String> mChildSortTagAdapter = new TagAdapter<String>(mChildSortArray) {
                @Override
                public View getView(FlowLayout parent, int position, String s) {
                    TextView tv = (TextView) getLayoutInflater().inflate(R.layout.item_tag, mChildSortTags, false);
                    tv.setText(s);
                    return tv;
                }
            };
            mChildSortTags.setAdapter(mChildSortTagAdapter);
        } else {
            TextView childSortTitle = (TextView) mFlowLayout.findViewById(R.id.child_sort_title);
            childSortTitle.setVisibility(View.GONE);
        }
    }


    public void confirm() {
        if (!getComposedUrl().equals(mUrl)) {
            mUrl = getComposedUrl();
            EventBus.getDefault().post(new XShelfChangeEvent(mUrl));
            LogUtils.simpleLog(ExploreView.class, "update Url " + mUrl);
        }

    }

    private String getComposedUrl() {
        JianyiApi.YihuoProfileBuilder mUrlBuilder = new JianyiApi.YihuoProfileBuilder(mTitle);

        if (getIntent().getExtras().getBoolean(ENABLE_CHILD_SORT, false)) {
            int mNewChildSortPosition = mChildSortTags.getSelectedList().isEmpty() ? -1 : (int) mChildSortTags.getSelectedList().toArray()[0];
            if (mNewChildSortPosition > -1) {
                mUrlBuilder.addSort(mChildSortArray[mNewChildSortPosition]);
            } else {
                mUrlBuilder.addSort("all");
            }
        }

        if (getIntent().getExtras().getBoolean(ENABLE_SCHOOL, false)) {
            int mNewSchoolPosition = mSchoolTags.getSelectedList().isEmpty() ? 0 : (int) mSchoolTags.getSelectedList().toArray()[0];
            mUrlBuilder.addSchool(mNewSchoolPosition + 1);
        }

        if (getIntent().getExtras().getBoolean(ENABLE_ORDER, false)) {
            int mNewOrderPosition = mOrderTags.getSelectedList().isEmpty() ? 0 : (int) mOrderTags.getSelectedList().toArray()[0];
            switch (mNewOrderPosition) {
                case 0:
                    mUrlBuilder.addTimeOrder(true);
                    break;
                case 1:
                    mUrlBuilder.addTimeOrder(false);
                    break;
                case 2:
                    mUrlBuilder.addPriceOrder(true);
                    break;
                case 3:
                    mUrlBuilder.addPriceOrder(false);
                    break;
            }
        }
        return mUrlBuilder.getUrl();
    }


    public void cancel() {
        // reset
    }

    @OnClick({R.id.filter_btn})
    public void onFilter(View view) {
        switch (view.getId()) {
            case R.id.filter_btn:
                if (mBottomSheetLayout.isSheetShowing()) {
                    confirm();
                    mBottomSheetLayout.dismissSheet();
                } else {
                    if (mBottomSheetLayout.getSheetView() == null) {
                        mBottomSheetLayout.showWithSheetView(mFlowLayout, new InsetViewTransformer());
                        mBottomSheetLayout.addOnSheetDismissedListener(this);
                    } else {
                        mBottomSheetLayout.expandSheet();
                    }
                    mToolbar.setNavigationIcon(R.drawable.ic_close_primary_24dp);
                    mFilterBtn.setImageResource(R.drawable.ic_check_primary_24dp);
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (mBottomSheetLayout.isSheetShowing()) {
            mBottomSheetLayout.dismissSheet();
            cancel();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onDismissed(BottomSheetLayout bottomSheetLayout) {
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_primary_24dp);
        mFilterBtn.setImageResource(R.drawable.ic_sort_white_24dp);
    }

}
