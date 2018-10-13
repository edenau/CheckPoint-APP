package com.example.cwmuser.cwmproject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.widget.EditText;

/**
 * Created by cwmuser on 03/06/2016.
 */
public class EditDialog extends DialogFragment {
    static EditDialog newInstance(String title, int position) {
        EditDialog fragment = new EditDialog();
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
        final EditText input = new EditText(getActivity());
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        return new AlertDialog.Builder(getActivity())
                .setTitle(title)
                .setView(input)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int whichButton) {
                                ((list)
                                        getActivity()).doEditName(position,input.getText().toString());
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