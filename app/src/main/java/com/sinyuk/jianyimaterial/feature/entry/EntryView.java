package com.sinyuk.jianyimaterial.feature.entry;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewStub;
import android.widget.FrameLayout;

import com.sinyuk.jianyimaterial.R;
import com.sinyuk.jianyimaterial.feature.drawer.DrawerView;
import com.sinyuk.jianyimaterial.feature.home.HomeView;
import com.sinyuk.jianyimaterial.mvp.BaseActivity;
import com.sinyuk.jianyimaterial.utils.ScreenUtils;
import com.sinyuk.jianyimaterial.utils.ToastUtils;

import java.lang.ref.WeakReference;

import butterknife.Bind;

/**
 * Created by Sinyuk on 16.3.30.
 */
public class EntryView extends BaseActivity<EntryPresenterImpl> implements IEntryView {
    @Bind(R.id.root_view)
    FrameLayout mRootView;
    @Bind(R.id.view_stub)
    ViewStub mViewStub;
    private long attemptExitTime;

    @Override
    protected boolean isUseEventBus() {
        return false;
    }

    @Override
    protected void beforeInflate() {
        ScreenUtils.hideSystemyBar(this);
    }

    @Override
    protected EntryPresenterImpl createPresenter() {
        return new EntryPresenterImpl();
    }

    @Override
    protected boolean isNavAsBack() {
        return false;
    }


    @Override
    protected void lazyLoad() {
        mViewStub.inflate();
        mViewStub = null;
        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction().replace(R.id.container_menu, DrawerView.getInstance()).commit();
        fm.beginTransaction().replace(R.id.home_view, HomeView.getInstance()).commit();
    }

    @Override
    protected void onFinishInflate() {
        FragmentManager fm = getSupportFragmentManager();
        final SplashView splashView = new SplashView();
        fm.beginTransaction().add(R.id.root_view, splashView).commit();
        setLazyLoadDelay(800);
        myHandler.postDelayed(new RemoveSplashRunnable(this, splashView), 2000);
    }

    @Override
    protected int getContentViewID() {
        return R.layout.entry_view;
    }

    @Override
    public void onBackPressed() {
        if (confirmExit()) { super.onBackPressed(); }
    }

    private boolean confirmExit() {
        if ((System.currentTimeMillis() - attemptExitTime) > 1000) {
            ToastUtils.toastFast(this, getString(R.string.common_confirm_exit_application));
            attemptExitTime = System.currentTimeMillis();
            return false;
        }
        return true;
    }

    private class RemoveSplashRunnable implements Runnable {
        private final WeakReference<SplashView> fragmentRef;
        private final WeakReference<Context> contextRef;

        public RemoveSplashRunnable(Context context, SplashView splashView) {
            contextRef = new WeakReference<>(context);
            fragmentRef = new WeakReference<>(splashView);
        }

        @Override
        public void run() {
            AppCompatActivity context = (AppCompatActivity) contextRef.get();
            try {
                SplashView splashFragment = fragmentRef.get();
                context.getSupportFragmentManager().beginTransaction().remove(splashFragment).commit();
            } catch (NullPointerException e) {
                e.printStackTrace();
            } finally {
                getWindow().getDecorView().setBackgroundColor(getResources().getColor(R.color.window_background));
                ScreenUtils.showSystemyBar(EntryView.this);
            }

        }
    }
}
