package com.sinyuk.jianyimaterial.feature.offer;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flipboard.bottomsheet.BottomSheetLayout;
import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxbinding.widget.RxTextView;
import com.sinyuk.jianyimaterial.R;
import com.sinyuk.jianyimaterial.adapters.ShotsGalleryAdapter;
import com.sinyuk.jianyimaterial.events.XShotDropEvent;
import com.sinyuk.jianyimaterial.feature.explore.ExploreView;
import com.sinyuk.jianyimaterial.mvp.BaseActivity;
import com.sinyuk.jianyimaterial.sweetalert.SweetAlertDialog;
import com.sinyuk.jianyimaterial.ui.InsetViewTransformer;
import com.sinyuk.jianyimaterial.utils.ImeUtils;
import com.sinyuk.jianyimaterial.utils.LogUtils;
import com.sinyuk.jianyimaterial.utils.StringUtils;
import com.sinyuk.jianyimaterial.utils.ToastUtils;
import com.sinyuk.jianyimaterial.widgets.flowlayout.FlowLayout;
import com.sinyuk.jianyimaterial.widgets.flowlayout.TagAdapter;
import com.sinyuk.jianyimaterial.widgets.flowlayout.TagFlowLayout;
import com.tbruyelle.rxpermissions.RxPermissions;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import rx.Observable;

/**
 * Created by Sinyuk on 16.4.8.
 */
public class OfferView extends BaseActivity<OfferPresenterImpl> implements IOfferView {
    private static final int REQUEST_PICK = 0x01;
    @Bind(R.id.check_btn)
    ImageView mCheckBtn;
    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.app_bar_layout)
    AppBarLayout mAppBarLayout;
    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @Bind(R.id.shot_count_tv)
    TextView mShotCountTv;
    @Bind(R.id.gallery_wrapper)
    LinearLayout mGalleryWrapper;
    @Bind(R.id.title_et)
    EditText mTitleEt;
    @Bind(R.id.title_input_area)
    TextInputLayout mTitleInputArea;
    @Bind(R.id.details_et)
    EditText mDetailsEt;
    @Bind(R.id.details_input_area)
    TextInputLayout mDetailsInputArea;
    @Bind(R.id.new_price_et)
    EditText mNewPriceEt;
    @Bind(R.id.new_price_input_area)
    TextInputLayout mNewPriceInputArea;
    @Bind(R.id.confirm_btn)
    Button mConfirmBtn;
    @Bind(R.id.bottom_sheet_layout)
    BottomSheetLayout mBottomSheetLayout;
    @Bind(R.id.coordinator_layout)
    CoordinatorLayout mCoordinatorLayout;

    private ShotsGalleryAdapter mAdapter;

    private List<Uri> uriList = new ArrayList<>(3);
    private HashMap<String, String> indexAndUrlMap = new HashMap<>();

    private SweetAlertDialog mDialog;
    private View mFlowLayout;


    private TagFlowLayout mChildSortTags;
    private TagFlowLayout mSortTags;
    private String mSort;
    private String mChildSort;

    @Override
    protected boolean isUseEventBus() {
        return true;
    }

    @Override
    protected void beforeInflate() {
    }

    @Override
    protected OfferPresenterImpl createPresenter() {
        return new OfferPresenterImpl();
    }

    @Override
    protected boolean isNavAsBack() {
        return true;
    }

    @Override
    protected void onFinishInflate() {
        uriList = new ArrayList<>();
        setupRecyclerView();
        setupBottomSheet();
        setupFlowLayout();
        setupObservers();
    }

    private void setupBottomSheet() {
        mFlowLayout = LayoutInflater.from(this).inflate(R.layout.offer_view_flow_layout, mBottomSheetLayout, false);
        mBottomSheetLayout.setUseHardwareLayerWhileAnimating(true);
        mBottomSheetLayout.setShouldDimContentView(true);
        mBottomSheetLayout.addOnSheetDismissedListener(bottomSheetLayout -> {

        });
    }

    private void setupFlowLayout() {
        final String[] sortArray = getResources().getStringArray(R.array.category_menu_items);
        mSortTags = (TagFlowLayout) mFlowLayout.findViewById(R.id.sort_tags);
        mSortTags.setMaxSelectCount(1); // disallowed multiSelected
        final TagAdapter<String> mSortTagAdapter = new TagAdapter<String>(sortArray) {
            @Override
            public View getView(FlowLayout parent, int position, String s) {
                TextView tv = (TextView) getLayoutInflater().inflate(R.layout.item_tag, mSortTags, false);
                tv.setText(s);
                return tv;
            }
        };
        mSortTags.setAdapter(mSortTagAdapter);
        mSortTags.setOnTagClickListener((view, position, parent) -> {
            if (position >= 0 && position < ExploreView.PARENT_SORT_LIST.length) {
                switchChildSortTag(position);
            }
            return false;
        });
    }

    private void switchChildSortTag(int position) {
        String[] childSortArray = getResources().getStringArray(ExploreView.PARENT_SORT_LIST[position]);
        mChildSortTags = (TagFlowLayout) mFlowLayout.findViewById(R.id.child_sort_tags);
        mChildSortTags.setMaxSelectCount(1); // disallowed multiSelected
        final TagAdapter<String> mChildSortTagAdapter = new TagAdapter<String>(childSortArray) {
            @Override
            public View getView(FlowLayout parent, int position, String s) {
                TextView tv = (TextView) getLayoutInflater().inflate(R.layout.item_tag, mChildSortTags, false);
                tv.setText(s);
                return tv;
            }
        };
        mChildSortTags.setAdapter(mChildSortTagAdapter);
    }

    private void setupObservers() {
        mCompositeSubscription.add(RxTextView.editorActions(mNewPriceEt)
                .map(actionId -> actionId == EditorInfo.IME_ACTION_DONE)
                .subscribe(done -> {if (done) { onClickConfirm(); }}));

        final Observable<CharSequence> titleObservable = RxTextView.textChanges(mTitleEt).skip(1);
        final Observable<CharSequence> detailsObservable = RxTextView.textChanges(mDetailsEt).skip(1);
        final Observable<CharSequence> priceObservable = RxTextView.textChanges(mNewPriceEt).skip(1);

        mCompositeSubscription.add(Observable.combineLatest(titleObservable, detailsObservable, priceObservable,
                (title, details, price) -> {
                    if (TextUtils.isEmpty(title)) {
                        mTitleEt.setError(getString(R.string.offer_hint_null_title));
                        return false;
                    }

                    if (TextUtils.isEmpty(details)) {
                        mDetailsEt.setError(getString(R.string.offer_hint_null_details));
                        return false;
                    }

                    if (TextUtils.isEmpty(price)) {
                        mNewPriceEt.setError(getString(R.string.offer_hint_null_price));
                        return false;
                    }
                    return true;
                }).subscribe(this::toggleConfirmButton));

        mConfirmBtn.setEnabled(false);
        mConfirmBtn.setClickable(false);

    }

    private void setupRecyclerView() {
        mAdapter = new ShotsGalleryAdapter(this);

        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);

        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mAdapter);
        // attach to
        mAdapter.setData(uriList);

        final View mAddButton = View.inflate(this, R.layout.offer_view_shot_add_button, null);
        mAdapter.setFooterView(mAddButton);

        mCompositeSubscription.add(
                RxView.clicks(mAddButton).compose(RxPermissions.getInstance(this)
                        .ensure(Manifest.permission.READ_EXTERNAL_STORAGE))
                        .subscribe(granted -> {
                            if (granted) {pickPhoto();} else {hintPermissionDenied();}
                        }));

    }

    private void toggleConfirmButton(boolean enable) {
        mConfirmBtn.setEnabled(enable);
        mConfirmBtn.setClickable(enable);
        if (enable) {
            mConfirmBtn.setBackground(getResources().getDrawable(R.drawable.rounded_rect_fill_accent));
            mCheckBtn.setVisibility(View.VISIBLE);
        } else {
            mCheckBtn.setVisibility(View.GONE);
            mConfirmBtn.setBackground(getResources().getDrawable(R.drawable.rounded_rect_fill_grey));
        }
    }

    /**
     * 点击确认按钮
     */
    @OnClick(R.id.confirm_btn)
    public void onClickConfirm() {
        if (uriList.isEmpty()) {
            ToastUtils.toastSlow(this, getString(R.string.offer_hint_null_shot));
        } else {
            ImeUtils.hideIme(mCoordinatorLayout);
            expandBottomSheet();
        }
    }

    @OnClick(R.id.check_btn)
    public void onAttemptPost() {
        mCompositeSubscription.add(
                Observable.combineLatest(getSortTag(), getChildSortTag(), (sort, childSort) -> {
                    if (!TextUtils.isEmpty(sort) && !TextUtils.isEmpty(childSort)) {
                        mSort = sort;
                        mChildSort = childSort;
                        return true;
                    }
                    return false;
                }).onErrorReturn(throwable -> false)
                        .subscribe(isTagSelected -> {
                            if (isTagSelected) {
                                mPresenter.post(indexAndUrlMap,
                                        mTitleEt.getText().toString(),
                                        mDetailsEt.getText().toString(),
                                        mNewPriceEt.getText().toString(),
                                        mSort,
                                        mChildSort);
                            } else {
                                // expand bottom sheet let'em select
                                expandBottomSheet();
                            }
                        }));

    }

    private Observable<String> getSortTag() {
        return Observable
                .from(mSortTags.getSelectedList())
                .take(1)
                .map(index -> getResources().getStringArray(R.array.category_menu_items)[index]);
    }

    private Observable<String> getChildSortTag() {
        return Observable.defer(() -> Observable
                .from(mSortTags.getSelectedList())
                .take(1)
                .map(sortIndex -> getResources().getStringArray(ExploreView.PARENT_SORT_LIST[sortIndex]))
                .flatMap(childSortArray -> Observable
                        .from(mChildSortTags.getSelectedList())
                        .take(1)
                        .map(childSortIndex -> childSortArray[childSortIndex])));
    }

    public void expandBottomSheet() {
        if (mBottomSheetLayout.getState() != BottomSheetLayout.State.EXPANDED) {
            if (mBottomSheetLayout.getSheetView() == null) {
                mBottomSheetLayout.showWithSheetView(mFlowLayout, new InsetViewTransformer());
            } else {
                mBottomSheetLayout.expandSheet();
            }
        }
    }

    public void dismissBottomSheet() {
        if (mBottomSheetLayout.getState() == BottomSheetLayout.State.EXPANDED) {
            mBottomSheetLayout.dismissSheet();
        }
    }


    private void hintPermissionDenied() {
        ToastUtils.toastSlow(this, getString(R.string.common_hint_permission_denied));
    }

    private void pickPhoto() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(Intent.createChooser(intent, getString(R.string.offer_hint_pick_from)), REQUEST_PICK);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_PICK) {
                final Uri selectedUri = data.getData();
                if (selectedUri != null) {
                    if (uriList.size() == 3) { return; }
                    uriList.add(selectedUri);
                    mAdapter.setData(uriList);
                    updateIndicator(uriList.size());
                    mPresenter.compressThenUpload(selectedUri.toString());
                }
            }
        }
    }

    private void updateIndicator(int size) {
        // 更新计数器
        if (size == 3) {
            mShotCountTv.setTextColor(getResources().getColor(R.color.themeRed));
        } else {
            mShotCountTv.setTextColor(getResources().getColor(R.color.grey_600));
        }
        mShotCountTv.setText(size + "/3");

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onListItemDelete(XShotDropEvent event) {
        uriList.remove(event.getPosition());
        updateIndicator(uriList.size());
        mAdapter.setData(uriList);
        indexAndUrlMap.remove(String.valueOf(event.getPosition()));
    }

    @Override
    protected int getContentViewID() {
        return R.layout.offer_view;
    }

    @Override
    public void showUploadProgress() {
        mDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        mDialog.getProgressHelper().setBarColor(getResources().getColor(R.color.colorAccent));
        mDialog.setTitleText(StringUtils.getRes(this, R.string.offer_hint_post_in_process));
        mDialog.setCancelable(false);

        dismissBottomSheet();
        ImeUtils.hideIme(mCoordinatorLayout);
        myHandler.postDelayed(() -> mDialog.show(), 250);

    }

    @Override
    public void hideUploadProgress() {
        mDialog.dismissWithAnimation();
    }

    @Override
    public void onShotUploadParseError(String message) {
        LogUtils.simpleLog(OfferView.class, "onShotUploadParseError");
        ToastUtils.toastSlow(this, message);
    }

    @Override
    public void onShotUploadVolleyError(String message) {
        LogUtils.simpleLog(OfferView.class, "onShotUploadVolleyError");
        ToastUtils.toastSlow(this, message);
    }


    @Override
    public void onShotUploadCompressError(String message) {
        LogUtils.simpleLog(OfferView.class, "onShotUploadCompressError");
        ToastUtils.toastSlow(this, message);
    }

    @Override
    public void onShotUploadSucceed(String url) {
        indexAndUrlMap.put(String.valueOf(uriList.size()), url);
        LogUtils.simpleLog(OfferView.class, "index " + String.valueOf(uriList.size()));
        LogUtils.simpleLog(OfferView.class, "url " + url);
    }

    @Override
    public void onPostGoodsSucceed(String message) {
        LogUtils.simpleLog(OfferView.class, "onPostGoodsSucceed");
        mDialog.setCancelable(false);
        mDialog.setOnDismissListener(dialog -> finish());
        mDialog.setTitleText(StringUtils.getRes(this, R.string.offer_hint_post_succeed))
                .setConfirmText(StringUtils.getRes(this, R.string.action_confirm))
                .setConfirmClickListener(sweetAlertDialog -> finish())
                .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
    }

    @Override
    public void onPostGoodsFailed(String message) {
        LogUtils.simpleLog(OfferView.class, "onPostGoodsFailed");
        mDialog.setCancelable(false);
        mDialog.setTitleText(StringUtils.check(this, message, R.string.offer_hint_post_failed))
                .setConfirmText(StringUtils.getRes(this, R.string.action_confirm))
                .setConfirmClickListener(sweetAlertDialog -> finish())
                .changeAlertType(SweetAlertDialog.WARNING_TYPE);
    }

    @Override
    public void onPostGoodsVolleyError(String message) {
        LogUtils.simpleLog(OfferView.class, "onPostGoodsVolleyError");
        mDialog.setCancelable(false);
        mDialog.setTitleText(StringUtils.check(this, message, R.string.offer_hint_network_error))
                .setConfirmText(StringUtils.getRes(this, R.string.action_confirm))
                .setConfirmClickListener(sweetAlertDialog -> finish())
                .changeAlertType(SweetAlertDialog.ERROR_TYPE);
    }

    @Override
    public void onUPostGoodsParseError(String message) {
        LogUtils.simpleLog(OfferView.class, "onUPostGoodsParseError");
        mDialog.setCancelable(false);
        mDialog.setTitleText(StringUtils.check(this, message, R.string.offer_hint_post_failed))
                .setConfirmText(StringUtils.getRes(this, R.string.action_confirm))
                .setConfirmClickListener(sweetAlertDialog -> finish())
                .changeAlertType(SweetAlertDialog.WARNING_TYPE);
    }

    @Override
    public void onBackPressed() {
        if (mBottomSheetLayout.isSheetShowing()) {
            mBottomSheetLayout.dismissSheet();
        } else {
            super.onBackPressed();
        }
    }
}
