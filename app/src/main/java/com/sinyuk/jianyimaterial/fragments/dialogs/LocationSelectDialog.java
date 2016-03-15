package com.sinyuk.jianyimaterial.fragments.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.sinyuk.jianyimaterial.R;
import com.sinyuk.jianyimaterial.events.LocationSelectEvent;

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
        builder.setTitle(R.string.pick_location)
                .setItems(getContext().getResources().getStringArray(R.array.schools_sort), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // The 'which' argument contains the index position
                        // of the selected item
                        EventBus.getDefault().post(new LocationSelectEvent(which));
                    }
                });

        return builder.create();
    }


}
