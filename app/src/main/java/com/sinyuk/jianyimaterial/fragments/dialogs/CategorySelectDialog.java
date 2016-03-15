package com.sinyuk.jianyimaterial.fragments.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.sinyuk.jianyimaterial.R;
import com.sinyuk.jianyimaterial.events.BaseEvent;
import com.sinyuk.jianyimaterial.events.CategorySelectEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by Sinyuk on 16.2.19.
 */
public class CategorySelectDialog extends DialogFragment {

    public static final String TAG = "CategorySelectDialog";

    public CategorySelectDialog() {
        // Required empty public constructor

    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Build the dialog and set up the button click handlers
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.MyAlertDialogTheme);
        builder.setTitle(R.string.hint_select_category)
                .setCancelable(true)
                .setItems(getActivity().getResources().getStringArray(R.array.category_menu_items), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // The 'which' argument contains the index position
                        // of the selected item
                        EventBus.getDefault().post(new CategorySelectEvent(which));
                    }
                });
        return builder.create();
    }



}
