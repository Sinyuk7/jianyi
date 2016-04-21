package com.sinyuk.jianyimaterial.common.dialog;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;
import android.support.design.widget.BottomSheetDialog;
import android.view.View;
import android.widget.TextView;

import com.sinyuk.jianyimaterial.R;
import com.sinyuk.jianyimaterial.events.XUnShelfOptionEvent;

import org.greenrobot.eventbus.EventBus;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by Sinyuk on 16.4.21.
 */
public class UnShelfDialog extends BottomSheetDialog {
    public UnShelfDialog(@NonNull Context context) {
        super(context);
        setContentView(R.layout.view_unshelf_dialog);
        setCanceledOnTouchOutside(true);
        setCancelable(true);
    }

    public UnShelfDialog(@NonNull Context context, @StyleRes int theme) {
        super(context, theme);
    }

    @OnClick({R.id.option_has_sold, R.id.option_dont_want, R.id.option_other_reason})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.option_has_sold:
                EventBus.getDefault().post(new XUnShelfOptionEvent(0));
                break;
            case R.id.option_dont_want:
                EventBus.getDefault().post(new XUnShelfOptionEvent(1));
                break;
            case R.id.option_other_reason:
                EventBus.getDefault().post(new XUnShelfOptionEvent(2));
                break;
        }
    }
}
