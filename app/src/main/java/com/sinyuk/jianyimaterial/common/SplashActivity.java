package com.sinyuk.jianyimaterial.common;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.sinyuk.jianyimaterial.R;
import com.sinyuk.jianyimaterial.feature.entry.EntryView;
import com.sinyuk.jianyimaterial.utils.ScreenUtils;

/**
 * Created by Sinyuk on 16.5.15.
 */
public class SplashActivity extends AppCompatActivity {
    private static final long LAZY_LOAD_DELAY = 0;
    private Handler myHandler = new Handler();
    private Runnable mLazyLoadRunnable;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ScreenUtils.hideSystemyBar(this);
        setContentView(R.layout.splash_view);

        mLazyLoadRunnable = this::runExitAnimation;

        if (savedInstanceState == null) {
            getWindow().getDecorView().post(() -> myHandler.postDelayed(mLazyLoadRunnable, LAZY_LOAD_DELAY));
        }
    }

    private void runExitAnimation() {
        startActivity(new Intent(SplashActivity.this, EntryView.class));
        finish();
    }
}
