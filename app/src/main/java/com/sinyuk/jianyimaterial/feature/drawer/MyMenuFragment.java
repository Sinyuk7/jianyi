package com.sinyuk.jianyimaterial.feature.drawer;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mxn.soul.flowingdrawer_core.MenuFragment;
import com.sinyuk.jianyimaterial.mvp.BasePresenter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.ButterKnife;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Sinyuk on 16.3.30.
 */
public abstract class MyMenuFragment<P extends BasePresenter> extends MenuFragment {
    protected static String TAG = "";
    protected P mPresenter;
    protected CompositeSubscription mCompositeSubscription;
    protected Context mContext;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        TAG = this.getClass().getSimpleName();

        mCompositeSubscription = new CompositeSubscription();

        EventBus.getDefault().register(this);

        beforeInflate();

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View mView = inflater.inflate(getContentViewID(), container, false);
        return setupReveal(mView);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        attachPresenter();
        onFinishInflate();
    }

    @Subscribe
    public void onEvent() {}

    protected abstract void beforeInflate();

    protected abstract int getContentViewID();

    void attachPresenter() {
        mPresenter = createPresenter();
        mPresenter.attachView(this);
    }

    protected abstract void onFinishInflate();

    protected abstract P createPresenter();

    @Override
    public void onDestroy() {
        super.onDestroy();
        detachPresenter();
        ButterKnife.unbind(this);
        if (!mCompositeSubscription.isUnsubscribed()) { mCompositeSubscription.unsubscribe(); }
        EventBus.getDefault().unregister(this);
    }

    void detachPresenter() {
        mPresenter.detachView();
        mPresenter = null;
    }
}
