package com.sinyuk.jianyimaterial.managers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.TextView;

import com.sinyuk.jianyimaterial.R;
import com.sinyuk.jianyimaterial.activities.WidgetDemo;
import com.sinyuk.jianyimaterial.feature.login.LoginView;
import com.sinyuk.jianyimaterial.utils.StringUtils;

/**
 * Created by Sinyuk on 15.12.23.
 */
public class SnackBarFactory {


    public static Snackbar requestLogin(final Activity context, View view) {
        Snackbar snackbar = Snackbar.make(view,
                StringUtils.getRes(context, R.string.common_hint_plz_login_first), Snackbar.LENGTH_SHORT);
        Snackbar.SnackbarLayout layout = (Snackbar.SnackbarLayout) snackbar.getView();
        layout.setBackgroundColor(context.getResources().getColor(R.color.white));
        snackbar.setActionTextColor(context.getResources().getColor(R.color.colorAccent));
        snackbar.setAction("现在登录", v -> {
            context.startActivity(new Intent(context, LoginView.class));
        });
        return snackbar;
    }


    public static Snackbar loginFailed(@NonNull final Context context, @NonNull View view, @NonNull String message) {
        Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG);
        Snackbar.SnackbarLayout layout = (Snackbar.SnackbarLayout) snackbar.getView();
        final TextView hintText = (TextView) layout.findViewById(R.id.snackbar_text);

        if (hintText != null) {
            hintText.setTextColor(context.getResources().getColor(R.color.themeError));
        }

        layout.setBackgroundColor(context.getResources().getColor(R.color.white));
        snackbar.setActionTextColor(context.getResources().getColor(R.color.textColorDark));
        snackbar.setAction("帮助", v -> {});
        return snackbar;
    }

    public static Snackbar networkError(@NonNull final Context context, @NonNull View view, @NonNull String message) {
        Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG);
        Snackbar.SnackbarLayout layout = (Snackbar.SnackbarLayout) snackbar.getView();
        final TextView hintText = (TextView) layout.findViewById(R.id.snackbar_text);

        if (hintText != null) {
            hintText.setTextColor(context.getResources().getColor(R.color.themeError));
        }

        layout.setBackgroundColor(context.getResources().getColor(R.color.white));
        snackbar.setActionTextColor(context.getResources().getColor(R.color.textColorDark));


        snackbar.setAction("检查网络连接", v -> {});
        return snackbar;
    }


    public static Snackbar errorNoAction(@NonNull final Context context, @NonNull View view, @NonNull String message) {
        Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG);
        Snackbar.SnackbarLayout layout = (Snackbar.SnackbarLayout) snackbar.getView();
        final TextView hintText = (TextView) layout.findViewById(R.id.snackbar_text);

        if (hintText != null) {
            hintText.setTextColor(context.getResources().getColor(R.color.themeError));
        }

        layout.setBackgroundColor(context.getResources().getColor(R.color.white));

        return snackbar;
    }

    public static Snackbar succeedNoAction(@NonNull final Context context, @NonNull View view, @NonNull String message) {
        Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG);
        Snackbar.SnackbarLayout layout = (Snackbar.SnackbarLayout) snackbar.getView();
        final TextView hintText = (TextView) layout.findViewById(R.id.snackbar_text);

        if (hintText != null) {
            hintText.setTextColor(context.getResources().getColor(R.color.colorAccent));
        }

        layout.setBackgroundColor(context.getResources().getColor(R.color.white));

        return snackbar;
    }


    public static Snackbar errorWithHelp(@NonNull final Context context, @NonNull View view, @NonNull String message) {
        Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG);
        Snackbar.SnackbarLayout layout = (Snackbar.SnackbarLayout) snackbar.getView();
        layout.setBackgroundColor(context.getResources().getColor(R.color.white));
        snackbar.setActionTextColor(context.getResources().getColor(R.color.colorAccent));
        snackbar.setAction("获取帮助", v -> {
            context.startActivity(new Intent(context, WidgetDemo.class));
        });
        return snackbar;
    }

}
