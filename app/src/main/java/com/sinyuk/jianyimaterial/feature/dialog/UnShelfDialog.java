package com.sinyuk.jianyimaterial.feature.dialog;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;
import android.support.design.widget.BottomSheetDialog;
import android.view.View;

import com.sinyuk.jianyimaterial.R;
import com.sinyuk.jianyimaterial.events.XUnShelfOptionEvent;
import com.sinyuk.jianyimaterial.utils.LogUtils;

import org.greenrobot.eventbus.EventBus;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Sinyuk on 16.4.21.
 */
public class UnShelfDialog extends BottomSheetDialog {
    private String mId;

    public UnShelfDialog(@NonNull Context context, String id) {
        super(context);
        setContentView(R.layout.view_unshelf_dialog);
        setCanceledOnTouchOutside(true);
        setCancelable(true);
        this.mId = id;
    }

    public UnShelfDialog(@NonNull Context context, @StyleRes int theme) {
        super(context, theme);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.option_has_sold, R.id.option_dont_want, R.id.option_other_reason})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.option_has_sold:
                EventBus.getDefault().post(new XUnShelfOptionEvent(0,mId));
                LogUtils.simpleLog(UnShelfDialog.class, "Click -> " + 0);
                break;
            case R.id.option_dont_want:
                EventBus.getDefault().post(new XUnShelfOptionEvent(1, mId));
                LogUtils.simpleLog(UnShelfDialog.class, "Click -> " + 1);
                break;
            case R.id.option_other_reason:
                EventBus.getDefault().post(new XUnShelfOptionEvent(2, mId));
                LogUtils.simpleLog(UnShelfDialog.class, "Click -> " + 2);
                break;
        }
        dismiss();
    }
}
