package com.example.cwmuser.cwmproject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

/**
 * Created by cwmuser on 03/06/2016.
 */
public class ConfirmationDialog extends DialogFragment {
    static ConfirmationDialog newInstance(String title, int position) {
        ConfirmationDialog fragment = new ConfirmationDialog();
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putInt("Position",position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String title = getArguments().getString("title");
        final int position =getArguments().getInt("Position");
        return new AlertDialog.Builder(getActivity())
                .setTitle(title)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int whichButton) {
                                ((list)
                                        getActivity()).doPositiveClick(position);
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int whichButton) {
                                ((list)
                                        getActivity()).doNegativeClick();
                            }
                        }).create();
    }

}
