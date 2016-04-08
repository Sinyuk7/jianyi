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
import com.sinyuk.jianyimaterial.mvp.BaseActivity;
import com.sinyuk.jianyimaterial.sweetalert.SweetAlertDialog;
import com.sinyuk.jianyimaterial.utils.StringUtils;
import com.tbruyelle.rxpermissions.RxPermissions;

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
    private View mAddButton;
    private List<Uri> uriList;

    private SweetAlertDialog mDialog;

    @Override
    protected boolean isUseEventBus() {
        return false;
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
        setupObserver();
        setupRecyclerView();
    }

    private void setupObserver() {
        mCompositeSubscription.add(RxTextView.editorActions(passwordEt)
                .map(actionId -> actionId == EditorInfo.IME_ACTION_DONE)
                .subscribe(done -> {
                    if (done) { clickLoginBtn(); }
                }));

        Observable<CharSequence> passwordObservable = RxTextView.textChanges(passwordEt).skip(5);
        Observable<CharSequence> phoneNumObservable = RxTextView.textChanges(userNameEt).skip(10);
    }

    private void setupRecyclerView() {

        mAdapter = new ShotsGalleryAdapter(this);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView.setLayoutManager(linearLayoutManager);

        mRecyclerView.setHasFixedSize(true);

        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mRecyclerView.setAdapter(mAdapter);

//        mAdapter.setData(uriList);

        mAddButton = View.inflate(this, R.layout.offer_view_shot_add_button, null);

        mCompositeSubscription.add(
                RxView.clicks(mAddButton).compose(RxPermissions.getInstance(this)
                        .ensure(Manifest.permission.READ_EXTERNAL_STORAGE))
                        .subscribe(granted -> {
                            if (granted) {pickPhoto();} else {hintPermissionDenied();}
                        }));


        mAdapter.setFooterView(mAddButton);
    }

    private void hintPermissionDenied() {

    }

    private void pickPhoto() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(Intent.createChooser(intent, getString(R.string.offer_hint_pick_from)), REQUEST_PICK);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_PICK) {
                final Uri selectedUri = data.getData();
                if (selectedUri != null) {
                    if (uriList.size() >= 3) { return; }
                    //
                    uriList.add(selectedUri);
                    mAdapter.notifyMyItemInserted(uriList.size());
                    updateIndicator(uriList.size());
                    mPresenter.compressThenUpload(selectedUri.getPath());
                }
            }
        }

    }

    private void updateIndicator(int size) {
        // 更新计数器
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
