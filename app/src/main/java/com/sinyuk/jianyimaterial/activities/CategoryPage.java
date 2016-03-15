package com.sinyuk.jianyimaterial.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.Menu;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.sinyuk.jianyimaterial.R;
import com.sinyuk.jianyimaterial.base.BaseActivity;
import com.sinyuk.jianyimaterial.events.SelectionsUpdateEvent;
import com.sinyuk.jianyimaterial.fragments.SortListFragment;
import com.sinyuk.jianyimaterial.utils.AnimUtils;
import com.sinyuk.jianyimaterial.widgets.flowlayout.FlowLayout;
import com.sinyuk.jianyimaterial.widgets.flowlayout.TagAdapter;
import com.sinyuk.jianyimaterial.widgets.flowlayout.TagFlowLayout;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import org.greenrobot.eventbus.EventBus;

import butterknife.Bind;
import butterknife.OnClick;
import cimi.com.easeinterpolator.EaseExponentialInInterpolator;
import cimi.com.easeinterpolator.EaseExponentialOutInterpolator;

public class CategoryPage extends BaseActivity {

    @Bind(R.id.list_fragment_container)
    FrameLayout listFragmentContainer;
    @Bind(R.id.school_tag_layout)
    TagFlowLayout schoolTagLayout;
    @Bind(R.id.order_tag_layout)
    TagFlowLayout orderTagLayout;
    @Bind(R.id.sort_tag_layout)
    TagFlowLayout childSortTagLayout;
    @Bind(R.id.sliding_layout)
    SlidingUpPanelLayout slidingLayout;


    public static final int[] parentSortResIds = new int[]{
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
    @Bind(R.id.back_iv)
    ImageView backIv;
    @Bind(R.id.toolbar_title_tv)
    TextView toolbarTitleTv;
    @Bind(R.id.sort_iv)
    ImageView sortIv;

    private final String[] sortOrder = new String[]{
            "发布时间↓",
            "发布时间↑",};

    private int parentSortIndex;


    private boolean hasTagsUpdated = false;

    private int schoolPos = 0;
    private int orderPos = 0;
    private int childSortPos = -1; // 全选
    private String[] schoolSelections;
    private String[] childSortSelections;


    @Override
    protected void beforeSetContentView(Bundle savedInstanceState) {
        parentSortIndex = getIntent().getIntExtra("parent_sort_index", 0);

        //
        schoolSelections = getResources().getStringArray(R.array.schools_sort);

        childSortSelections = getResources().getStringArray(parentSortResIds[parentSortIndex]);
    }

    @Override
    protected int getContentViewID() {
        return R.layout.activity_category_page;
    }

    @Override
    protected boolean isNavAsBack() {
        return false;
    }

    @Override
    protected boolean isUsingEventBus() {
        return true;
    }

    @Override
    protected void initViews() {
        initListFragment();
        setupSlidingLayout();
        setupTagFlowLayout();
        setupFakeToolbar();
    }


    @Override
    protected void initData() {

    }

    private void setupFakeToolbar() {
        String title = getResources().getStringArray(R.array.category_menu_items)[parentSortIndex];
        if (title != null)
            toolbarTitleTv.setText(title);
    }


    private void setupSlidingLayout() {
        slidingLayout.setPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {

            }

            @Override
            public void onPanelCollapsed(View panel) {
                sortIv.animate().rotation(0).setDuration(AnimUtils.ANIMATION_TIME_SHORT)
                        .setInterpolator(new EaseExponentialOutInterpolator()).start();

                if (hasTagsUpdated) {

                    if (schoolPos + 1 > getResources().getStringArray(R.array.schools_sort).length || schoolPos < 0)
                        schoolPos = 0;

                    final String order = orderPos == 0 ? "time_desc" : "time_asc";
                    String childSortStr = null;

//                    if (childSortPos >= 0 && childSortPos < childSortSelections.length)
//                        childSortStr = schoolSelections[childSortPos];


                    EventBus.getDefault().post(new SelectionsUpdateEvent(schoolPos, order, null));


                    hasTagsUpdated = false;
                }
            }

            @Override
            public void onPanelExpanded(View panel) {
                sortIv.animate().rotation(180).setDuration(AnimUtils.ANIMATION_TIME_SHORT)
                        .setInterpolator(new EaseExponentialInInterpolator()).start();

            }

            @Override
            public void onPanelAnchored(View panel) {

            }

            @Override
            public void onPanelHidden(View panel) {

            }
        });
    }

    private void setupTagFlowLayout() {

        schoolTagLayout.setMaxSelectCount(1); // disallowed multiSelected

        TagAdapter<String> schoolTagAdapter = new TagAdapter<String>(schoolSelections) {
            @Override
            public View getView(FlowLayout parent, int position, String s) {

                TextView tv = (TextView) getLayoutInflater().inflate(R.layout.item_tag,
                        schoolTagLayout, false);
                tv.setText(s);
                return tv;
            }
        };
        schoolTagLayout.setAdapter(schoolTagAdapter);
        schoolTagAdapter.setSelectedList(0); // default school selected is ZJCM xiasha


        // sortOrder
        orderTagLayout.setMaxSelectCount(1);
        TagAdapter<String> orderTagAdapter = new TagAdapter<String>(sortOrder) {

            @Override
            public View getView(FlowLayout parent, int position, String s) {

                TextView tv = (TextView) getLayoutInflater().inflate(R.layout.item_tag,
                        schoolTagLayout, false);
                tv.setText(s);
                return tv;
            }
        };
        orderTagLayout.setAdapter(orderTagAdapter);
        orderTagAdapter.setSelectedList(0); // default time_desc


        // child Sort
        childSortTagLayout.setMaxSelectCount(1); // multiSelected
        TagAdapter<String> childSortTagAdapter = new TagAdapter<String>(childSortSelections) {
            @Override
            public View getView(FlowLayout parent, int position, String s) {

                TextView tv = (TextView) getLayoutInflater().inflate(R.layout.item_tag,
                        schoolTagLayout, false);
                tv.setText(s);
                return tv;
            }
        };
        childSortTagLayout.setAdapter(childSortTagAdapter);


        // selected listener
        schoolTagLayout.setOnTagClickListener(new TagFlowLayout.OnTagClickListener() {

            @Override
            public boolean onTagClick(View view, int position, FlowLayout parent) {
                if (position != schoolPos) {
                    schoolPos = position;
                    hasTagsUpdated = true;
                }
                return false;
            }
        });


        orderTagLayout.setOnTagClickListener(new TagFlowLayout.OnTagClickListener() {

            @Override
            public boolean onTagClick(View view, int position, FlowLayout parent) {
                if (position != orderPos) {
                    orderPos = position;
                    hasTagsUpdated = true;
                }
                return false;
            }
        });

        childSortTagLayout.setOnTagClickListener(new TagFlowLayout.OnTagClickListener() {

            @Override
            public boolean onTagClick(View view, int position, FlowLayout parent) {
                if (position != childSortPos) {
                    childSortPos = position;
                    hasTagsUpdated = true;
                }
                return false;
            }
        });

    }



    private void initListFragment() {
        final FragmentManager fm = getSupportFragmentManager();
        // 初始化的时候传递大类的分类过去
        fm.beginTransaction().add(R.id.list_fragment_container, SortListFragment.newInstance(parentSortIndex)).commit();
    }


    @OnClick({R.id.back_iv, R.id.sort_iv})
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.back_iv:
                onBackPressed();
                break;
            case R.id.sort_iv:
                if (slidingLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED) {
                    slidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);

                } else if (slidingLayout.getPanelState() == SlidingUpPanelLayout.PanelState.COLLAPSED) {
                    slidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);

                }

                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_bar_search, menu);
        return true;
    }
}
