package com.sinyuk.jianyimaterial.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageButton;
import android.widget.TextView;

import com.sinyuk.jianyimaterial.R;
import com.sinyuk.jianyimaterial.utils.StringUtils;
import com.sinyuk.jianyimaterial.widgets.ExpandableTextView;

import butterknife.Bind;
import butterknife.ButterKnife;

public class WidgetDemo extends AppCompatActivity {

    @Bind(R.id.expandable_text)
    TextView expandableText;
    @Bind(R.id.expand_collapse)
    ImageButton expandCollapse;
    @Bind(R.id.description_tv)
    ExpandableTextView descriptionTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_widget_demo);
        ButterKnife.bind(this);
        descriptionTv.setText(StringUtils.getRes(this,R.string.lorem));
    }
}
