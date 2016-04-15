package com.sinyuk.jianyimaterial.fragments.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.sinyuk.jianyimaterial.R;
import com.sinyuk.jianyimaterial.events.XLocationSelectEvent;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by Sinyuk on 16.1.25.
 */
public class LocationSelectDialog extends DialogFragment {


    public static final String TAG = "LocationSelectDialog";


    public LocationSelectDialog() {
        // Required empty public constructor
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.MyAlertDialogTheme);
        // Build the dialog and set up the button click handlers
        builder.setTitle(R.string.hint_choose_school)
                .setItems(getContext().getResources().getStringArray(R.array.schools_sort), (dialog, which) -> {
                    EventBus.getDefault().post(new XLocationSelectEvent(which));
                });

        return builder.create();
    }


}
