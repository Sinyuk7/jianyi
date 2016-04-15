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
import com.sinyuk.jianyimaterial.mvp.BaseActivity;
import com.sinyuk.jianyimaterial.sweetalert.SweetAlertDialog;
import com.sinyuk.jianyimaterial.utils.LogUtils;
import com.sinyuk.jianyimaterial.utils.StringUtils;
import com.sinyuk.jianyimaterial.utils.ToastUtils;
import com.tbruyelle.rxpermissions.RxPermissions;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import rx.Observable;

/**
 * Created by Sinyuk on 16.4.8.
 */
public class OfferView extends BaseActivity<OfferPresenterImpl> implements IOfferView {
    private static final int REQUEST_PICK = 0x01;
    @Bind(R.id.check_ctn)
    ImageView mCheckCtn;
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

    private SweetAlertDialog mDialog;

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
        setupObservers();
        setupRecyclerView();
    }

    private void setupObservers() {
        mCompositeSubscription.add(RxTextView.editorActions(mNewPriceEt)
                .map(actionId -> actionId == EditorInfo.IME_ACTION_DONE)
                .subscribe(done -> {if (done) { onClickConfirm(); }}));

        Observable<Integer> shotObservable = Observable.from(uriList).count();
        Observable<CharSequence> titleObservable = RxTextView.textChanges(mTitleEt).skip(1);
        Observable<CharSequence> detailsObservable = RxTextView.textChanges(mDetailsEt).skip(1);
        Observable<CharSequence> priceObservable = RxTextView.textChanges(mNewPriceEt).skip(1);

        mCompositeSubscription.add(Observable.combineLatest(shotObservable, titleObservable, detailsObservable, priceObservable,
                (count, title, details, price) -> {
                    if (count <= 0) {
                        ToastUtils.toastSlow(this, getString(R.string.offer_hint_null_shot));
                        return false;
                    }

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
        } else {
            mConfirmBtn.setBackground(getResources().getDrawable(R.drawable.rounded_rect_fill_grey));
        }
    }

    /**
     * 点击确认按钮
     */
    private void onClickConfirm() {

    }

    private void hintPermissionDenied() {
        ToastUtils.toastSlow(this, getString(R.string.offer_hint_permission_denied));
    }

    private void pickPhoto() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(Intent.createChooser(intent, getString(R.string.offer_hint_pick_from)), REQUEST_PICK);
    }


/*    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_PICK) {
                final Uri selectedUri = data.getData();
                if (uriList.size() >= 3) { return; }
                //
                // 因为没有header 所以notifyMyItemInserted 和notifyItemInserted 是一样的
//                mAdapter.addData(selectedUri);
                uriList.add(selectedUri);
                mAdapter.notifyMyItemInserted(uriList.size());
                LogUtils.simpleLog(OfferView.class, "uriList.size() " + uriList.size());
                LogUtils.simpleLog(OfferView.class, "getDataItemCount() " + mAdapter.getDataItemCount());
                LogUtils.simpleLog(OfferView.class, "getItemCount() " + mAdapter.getItemCount());
                updateIndicator(uriList.size());
                mPresenter.compressThenUpload(selectedUri.getPath());
            }
        }

    }*/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_PICK) {
                final Uri selectedUri = data.getData();
                if (selectedUri != null) {
                    if (uriList.size() == 3) { return; }
                    uriList.add(selectedUri);
                    LogUtils.simpleLog(OfferView.class, "getItemCount At list add" + mAdapter.getItemCount());
//                    mAdapter.notifyMyItemInserted(uriList.size());
                    mAdapter.setData(uriList);
                    LogUtils.simpleLog(OfferView.class, "uriList.size " + uriList.size());
                    LogUtils.simpleLog(OfferView.class, "getDataItemCount " + mAdapter.getDataItemCount());
                    LogUtils.simpleLog(OfferView.class, "getItemCount " + mAdapter.getItemCount());
                    updateIndicator(uriList.size());
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
    }

    @Override
    protected int getContentViewID() {
        return R.layout.offer_view;
    }

    @Override
    public void showUploadProgress() {
        mDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        mDialog.getProgressHelper().setBarColor(getResources().getColor(R.color.colorAccent));
        mDialog.setTitleText(StringUtils.getRes(this, R.string.offer_hint_uploading));
        mDialog.setCancelable(false);
        mDialog.show();
    }

    @Override
    public void hideUploadProgress() {
        mDialog.dismissWithAnimation();
    }

    @Override
    public void onParseError(String message) {
    }

    @Override
    public void onVolleyError(String message) {

    }

    @Override
    public void onUploaded(String url) {

    }
}
