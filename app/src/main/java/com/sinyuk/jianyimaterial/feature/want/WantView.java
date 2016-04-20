package com.sinyuk.jianyimaterial.feature.want;

import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TextInputLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxbinding.widget.RxTextView;
import com.sinyuk.jianyimaterial.R;
import com.sinyuk.jianyimaterial.mvp.BaseActivity;
import com.sinyuk.jianyimaterial.utils.ImeUtils;
import com.sinyuk.jianyimaterial.utils.ToastUtils;

import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import rx.Observable;

/**
 * Created by Sinyuk on 16.4.20.
 */
public class WantView extends BaseActivity<WantPresenterImpl> implements IWantView {
    private static final int MAX_INPUT_COUNT = 40;
    @Bind(R.id.check_btn)
    ImageView mCheckBtn;
    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.app_bar_layout)
    AppBarLayout mAppBarLayout;
    @Bind(R.id.want_content_et)
    EditText mWantContentEt;
    @Bind(R.id.word_count_tv)
    TextView mWordCountTv;
    @Bind(R.id.contact_info_et)
    EditText mContactInfoEt;
    @Bind(R.id.contact_info_input_area)
    TextInputLayout mContactInfoInputArea;
    @Bind(R.id.want_price_et)
    EditText mWantPriceEt;
    @Bind(R.id.want_price_input_area)
    TextInputLayout mWantPriceInputArea;
    @Bind(R.id.post_btn)
    Button mPostBtn;
    @Bind(R.id.nested_scroll_view)
    NestedScrollView mNestedScrollView;
    @Bind(R.id.coordinator_layout)
    CoordinatorLayout mCoordinatorLayout;

    @Override
    protected boolean isUseEventBus() {
        return false;
    }

    @Override
    protected void beforeInflate() {

    }

    @Override
    protected WantPresenterImpl createPresenter() {
        return new WantPresenterImpl();
    }

    @Override
    protected boolean isNavAsBack() {
        return true;
    }

    @Override
    protected void onFinishInflate() {
        setupTextWatcher();
        setupObservers();
        toggleFunctionButton(false);
        mPresenter.queryCurrentUser();
    }

    private void setupObservers() {
        mCompositeSubscription.add(RxTextView.editorActions(mWantPriceEt)
                .map(actionId -> actionId == EditorInfo.IME_ACTION_DONE)
                .subscribe(done -> {
                    if (done) { attemptToPost(); }
                }));

        Observable<CharSequence> contentObservable = RxTextView.textChanges(mWantContentEt).skip(1);
        Observable<CharSequence> phoneNumObservable = RxTextView.textChanges(mContactInfoEt).skip(1);
        Observable<CharSequence> priceObservable = RxTextView.textChanges(mWantPriceEt).skip(1);

        mCompositeSubscription.add(Observable.combineLatest(contentObservable, phoneNumObservable, priceObservable, (content, phoneNum, price) -> {
            if (TextUtils.isEmpty(content)) {
                mWantContentEt.setError("你确定?");
                return false;
            }
            if (phoneNum.length() < 6) {
                mContactInfoEt.setError("你确定?");
                return false;
            }

            if (TextUtils.isEmpty(price)) {
                mWantPriceEt.setError("你确定?");
                return false;
            }
            return true;
        }).subscribe(WantView.this::toggleFunctionButton));

        mCompositeSubscription.add(RxView.clicks(mPostBtn).throttleFirst(4, TimeUnit.SECONDS).subscribe(aVoid -> attemptToPost()));

        mCompositeSubscription.add(RxView.clicks(mCheckBtn).throttleFirst(4, TimeUnit.SECONDS).subscribe(aVoid -> attemptToPost()));
    }

    private void setupTextWatcher() {
        mWantContentEt.addTextChangedListener(new TextWatcher() {
            private int editStart;
            private int editEnd;

            @Override
            public void afterTextChanged(Editable s) {
                editStart = mWantContentEt.getSelectionStart();
                editEnd = mWantContentEt.getSelectionEnd();

                // 先去掉监听器，否则会出现栈溢出
                mWantContentEt.removeTextChangedListener(this);

                // 注意这里只能每次都对整个EditText的内容求长度，不能对删除的单个字符求长度
                // 因为是中英文混合，单个字符而言，calculateLength函数都会返回1
                while (calculateLength(s.toString()) > MAX_INPUT_COUNT) {
                    // 当输入字符个数超过限制的大小时，进行截断操作
                    s.delete(editStart - 1, editEnd);
                    editStart--;
                    editEnd--;
                }
                // mEditText.setText(s);
                // 将这行代码注释掉就不会出现后面所说的输入法在数字界面自动跳转回主界面的问题了，多谢@ainiyidiandian的提醒
                mWantContentEt.setSelection(editStart);
                // 恢复监听器
                mWantContentEt.addTextChangedListener(this);
                updateCounterText();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

        mWantContentEt.setSelection(mWantContentEt.length()); // 将光标移动最后一个字符后面
        updateCounterText();
    }

    private void updateCounterText() {
        mWordCountTv.setText(String.valueOf((MAX_INPUT_COUNT - getInputCount())));
    }

    private long getInputCount() {
        return calculateLength(mWantContentEt.getText().toString());
    }

    private long calculateLength(String c) {
        double len = 0;
        for (int i = 0; i < c.length(); i++) {
            int tmp = (int) c.charAt(i);
            if (tmp > 0 && tmp < 127) {
                len += 0.5;
            } else {
                len++;
            }
        }
        return Math.round(len);
    }

    public void toggleFunctionButton(boolean isReady) {
        mPostBtn.setEnabled(isReady);
        mPostBtn.setClickable(isReady);
        if (isReady) {
            mPostBtn.setBackground(getResources().getDrawable(R.drawable.rounded_rect_fill_accent));
        } else {
            mPostBtn.setBackground(getResources().getDrawable(R.drawable.rounded_rect_fill_grey));
        }
    }

    public void attemptToPost() {
        mWantContentEt.setError(null);
        mContactInfoEt.setError(null);
        mWantPriceEt.setError(null);
        final String detail = mWantContentEt.getText().toString();
        final String phoneNum = mContactInfoEt.getText().toString();
        final String price = mWantPriceEt.getText().toString();
        ImeUtils.hideIme(mCoordinatorLayout);
        mPresenter.post(detail, phoneNum, price);
    }

    @Override
    protected int getContentViewID() {
        return R.layout.want_view;
    }

    @Override
    public void showContactInfo(@NonNull String tel) {
        mContactInfoEt.setText(tel);
    }

    @Override
    public void onQueryFailed(String message) {

    }

    @Override
    public void onUserNotLogged() {

    }

    @Override
    public void onPostNeedSucceed() {
        ToastUtils.toastSlow(this, getString(R.string.want_hint_post_succeed));
        finish();
    }

    @Override
    public void onPostNeedFailed(String message) {
        ToastUtils.toastSlow(this, message);
    }

    @Override
    public void onPostNeedVolleyError(String message) {
        ToastUtils.toastSlow(this, message);
    }

    @Override
    public void onPostNeedParseError(String message) {
        ToastUtils.toastSlow(this, message);
    }
}
