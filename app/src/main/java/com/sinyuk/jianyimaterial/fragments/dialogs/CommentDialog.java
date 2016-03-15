package com.sinyuk.jianyimaterial.fragments.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.EditText;

import com.sinyuk.jianyimaterial.R;

/**
 * Created by Sinyuk on 16.3.6.
 */
public class CommentDialog extends DialogFragment {

    public static final String TAG = "CommentDialog";
    private View inputView;
    private EditText commentEt;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inputView = View.inflate(getContext(), R.layout.include_comment_input_layout, null);
        commentEt = (EditText) inputView.findViewById(R.id.comment_et);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.MyAlertDialogTheme);


        // Build the dialog and set up the button click handlers
        builder.setTitle("你想评论点什么")
                .setView(inputView)
                .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dismiss();
                    }
                }).setCancelable(false);

        return builder.create();
    }
}
