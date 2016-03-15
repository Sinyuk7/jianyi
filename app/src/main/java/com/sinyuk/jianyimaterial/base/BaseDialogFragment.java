package com.sinyuk.jianyimaterial.base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.View;

import com.sinyuk.jianyimaterial.R;

import butterknife.ButterKnife;
import org.greenrobot.eventbus.EventBus;

/**
 * Created by Sinyuk on 16.2.3.
 */
public abstract class BaseDialogFragment extends DialogFragment {
    /**
     * context
     */
    protected Context mContext = null;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    protected AlertDialog.Builder getDialogBuilder() {
        return new AlertDialog.Builder(mContext, R.style.MyAlertDialogTheme);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (isNeedEventBus()) {
            EventBus.getDefault().unregister(this);
        }
    }

    protected abstract boolean isNeedEventBus();
}
