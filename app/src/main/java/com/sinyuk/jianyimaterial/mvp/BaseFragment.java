package com.sinyuk.jianyimaterial.mvp;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.ButterKnife;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Sinyuk on 16.3.27.
 * Base fragment for mvp architecture
 */
public abstract class BaseFragment<P extends BasePresenter> extends Fragment {
    protected static String TAG = "";
    protected P mPresenter;
    protected CompositeSubscription mCompositeSubscription;
    protected Context mContext;

    protected Handler sMyHandler = new Handler();

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        TAG = this.getClass().getSimpleName();

        if (isUseEventBus()) { EventBus.getDefault().register(this); }

        mCompositeSubscription = new CompositeSubscription();

        beforeInflate();

    }

    @Subscribe
    public void onEvent() {}

    protected abstract boolean isUseEventBus();

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(getContentViewID(), container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        attachPresenter();
        onFinishInflate();
    }

    protected abstract void beforeInflate();

    protected abstract void onFinishInflate();

    protected abstract int getContentViewID();

    void attachPresenter() {
        mPresenter = createPresenter();
        mPresenter.attachView(this);
    }

    protected abstract P createPresenter();

    @Override
    public void onDestroy() {
        super.onDestroy();
        detachPresenter();
        ButterKnife.unbind(this);
        if (!mCompositeSubscription.isUnsubscribed()) { mCompositeSubscription.unsubscribe(); }
        if (isUseEventBus()) { EventBus.getDefault().unregister(this); }
        sMyHandler.removeCallbacksAndMessages(null);
    }

    void detachPresenter() {
        mPresenter.detachView();
        mPresenter = null;
    }
}
