package com.sinyuk.jianyimaterial.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

/**
 * Created by Sinyuk on 15.10.20.
 */
public class NumberTextView extends TextView {

    public NumberTextView(Context context) {
        super(context);
        formatNumber();
    }

    private void formatNumber() {
        CharSequence sequence = getText();
        if (sequence != null) {
            int length = sequence.length();
            String suffix = "";
            StringBuilder sb = new StringBuilder();
            Log.w("MyLog", "NumberTextView" + " length: " + length + " original: " + getText());
            for (int i = 0; i < length; i++) {
                if (sequence.charAt(i) < '0' || sequence.charAt(i) > '9')
                    throw new IllegalArgumentException("NumberTextView's text must only have numbers");
            }
            int invalidLength = 0;


            if (length >= 9) {
                invalidLength = 8;
                suffix = "bn";
            }else if (length >= 7) {
                invalidLength = 6;
                suffix = "m";
            }else if (length > 4) {
                invalidLength = 3;
                suffix = "k";
            }
            for (int i = 0; i < (length - invalidLength); i++) {
                sb.append(sequence.charAt(i));
            }
            if (invalidLength > 0) {
                sb.append('.');
                sb.append(sequence.charAt(length - invalidLength));
            }

            sb.append(suffix);
            setText(sb.toString());
        }
    }

    public NumberTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        formatNumber();
    }

    public NumberTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        formatNumber();

    }
}
