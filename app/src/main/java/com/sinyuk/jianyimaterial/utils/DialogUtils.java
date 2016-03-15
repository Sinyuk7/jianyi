package com.sinyuk.jianyimaterial.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;

import com.sinyuk.jianyimaterial.R;

/**
 * Created by Sinyuk on 16.2.15.
 */
public class DialogUtils {


    /**
     * This method shows dialog with given title & message.
     * Also there is an option to pass onClickListener for positive & negative button.
     *
     * @param title                         - dialog title
     * @param message                       - dialog message
     * @param onPositiveButtonClickListener - listener for positive button
     * @param positiveText                  - positive button text
     * @param onNegativeButtonClickListener - listener for negative button
     * @param negativeText                  - negative button text
     */
    public static AlertDialog simpleConfirmDialog(@NonNull Context context, @Nullable String title, @Nullable String message,
                                                  @Nullable DialogInterface.OnClickListener onPositiveButtonClickListener,
                                                  @NonNull String positiveText,
                                                  @Nullable DialogInterface.OnClickListener onNegativeButtonClickListener,
                                                  @NonNull String negativeText) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.MyAlertDialogTheme);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setCancelable(true);
        builder.setPositiveButton(positiveText, onPositiveButtonClickListener);
        builder.setNegativeButton(negativeText, onNegativeButtonClickListener);

        return builder.create();
    }

    public static ProgressDialog simpleProgressDialog(@NonNull Context context, @Nullable String title, @Nullable String message,
                                                      @Nullable DialogInterface.OnCancelListener listener) {
        ProgressDialog progressDialog = new ProgressDialog(context, R.style.MyAlertDialogTheme);
        if (message != null)
            progressDialog.setMessage(message);
        if (title != null)
            progressDialog.setTitle(title);
        progressDialog.setIndeterminate(true);
        progressDialog.setOnCancelListener(listener);
        progressDialog.setCancelable(true);

        return progressDialog;
    }
}
