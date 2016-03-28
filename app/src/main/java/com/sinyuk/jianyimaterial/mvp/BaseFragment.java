package com.sinyuk.jianyimaterial.mvp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Sinyuk on 16.3.27.
 * Base fragment for mvp architecture
 */
public abstract class BaseFragment extends Fragment {

    protected CompositeSubscription mCompositeSubscription;

    protected static String TAG = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TAG = this.getClass().getSimpleName();

        mCompositeSubscription = new CompositeSubscription();

        beforeInflate();

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(getContentViewID(), container);
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

    protected abstract void attachPresenter();

    @Override
    public void onDestroy() {
        super.onDestroy();
        detachPresenter();
        ButterKnife.unbind(this);
        if (!mCompositeSubscription.isUnsubscribed())
            mCompositeSubscription.unsubscribe();
    }

    protected abstract void detachPresenter();
}
