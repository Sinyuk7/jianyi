package com.sinyuk.jianyimaterial.mvp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.jakewharton.rxbinding.support.v7.widget.RxToolbar;
import com.sinyuk.jianyimaterial.R;

import butterknife.ButterKnife;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Sinyuk on 16.3.16.
 */
public abstract class BaseActivity
        extends AppCompatActivity {

    protected CompositeSubscription mCompositeSubscription;

    protected static String TAG = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TAG = this.getClass().getSimpleName();

        mCompositeSubscription = new CompositeSubscription();

        if (getContentViewID() != 0) {
            setContentView(getContentViewID());
        } else {
            throw new IllegalArgumentException(TAG + " -> contentView can not been set");
        }
        attachPresenter();
        ButterKnife.bind(this);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        if (toolbar != null) {
            // TODO: Set back arrow as default navigation button
            if (isNavAsBack()) {
                setSupportActionBar(toolbar);
                toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
                mCompositeSubscription.add(RxToolbar.navigationClicks(toolbar).subscribe(this::onNavigationClick));
            }
        }

        onFinishInflate();
    }

    public void onNavigationClick(Void v) {
        finish();
    }

    protected abstract boolean isNavAsBack();

    protected abstract void onFinishInflate();

    protected abstract int getContentViewID();

    protected abstract void attachPresenter();

    @Override
    protected void onDestroy() {
        super.onDestroy();
        detachPresenter();
        ButterKnife.unbind(this);
        if (!mCompositeSubscription.isUnsubscribed())
            mCompositeSubscription.unsubscribe();
    }

    protected abstract void detachPresenter();
}
