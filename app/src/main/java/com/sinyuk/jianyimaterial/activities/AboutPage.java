package com.sinyuk.jianyimaterial.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sinyuk.jianyimaterial.R;
import com.sinyuk.jianyimaterial.utils.AnimUtils;
import com.sinyuk.jianyimaterial.utils.LogUtils;
import com.sinyuk.jianyimaterial.widgets.CircleRevealView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cimi.com.easeinterpolator.EaseSineOutInterpolator;

public class AboutPage extends AppCompatActivity {

    @Bind(R.id.icon)
    ImageView icon;
    @Bind(R.id.jianyi)
    TextView jianyi;
    @Bind(R.id.email_tv)
    TextView emailTv;
    @Bind(R.id.phone_tv)
    TextView phoneTv;
    @Bind(R.id.foreground)
    LinearLayout foreground;
    @Bind(R.id.background)
    CircleRevealView background;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        ButterKnife.bind(this);

        final int[] location;

        location = getIntent().getIntArrayExtra("tap_location");

        LogUtils.simpleLog(AboutPage.class, "x: " + location[0] + " y: " + location[1]);
        for (int i = 0; i < foreground.getChildCount(); i++) {
            View child = foreground.getChildAt(i);
            child.setAlpha(0f);
        }


        background.setFillPaintColor(getResources().getColor(R.color.colorAccent));
        background.setOnStateChangeListener(new CircleRevealView.OnStateChangeListener() {
            @Override
            public void onStateChange(int state) {
                if (state == CircleRevealView.STATE_FILL_STARTED)
                    animateViews();
            }
        });


        background.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {

            @Override

            public boolean onPreDraw() {

                //必须remove掉 不然会重复调用

                background.getViewTreeObserver().removeOnPreDrawListener(this);

                background.startFromLocation(location);

                return false;

            }
        });
    }

    private void animateViews() {
        for (int i = 0; i < foreground.getChildCount(); i++) {

            View child = foreground.getChildAt(i);
            int delay = i * 100;
            if (i == 0)
                delay = 400;
            child.animate().alphaBy(1f)
                    .setDuration(AnimUtils.ANIMATION_TIME_SHORT)
                    .setInterpolator(new EaseSineOutInterpolator())
                    .setStartDelay(delay).start();
        }

    }

    @OnClick({R.id.email_tv, R.id.phone_tv})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.email_tv:
                break;
            case R.id.phone_tv:
                break;
        }
    }
}
