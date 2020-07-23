package com.example.cpapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import java.util.Date;

public class NotesDialog extends DialogFragment {
    private static final String TEXT = "text" ;
    private TextView mTextView;
    public Dialog onCreateDialog(Bundle savedInstanceState){
        String text = (String) getArguments().getSerializable(TEXT);
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.notes_dialog,null);
        mTextView = v.findViewById(R.id.notes_text_view);
        mTextView.setText(text);
        return new AlertDialog.Builder(getActivity())
                .setView(v)
                .setTitle("Notes")
                .create();
    }
    public static NotesDialog newInstance(String text) {
        Bundle args = new Bundle();
        args.putSerializable(TEXT, text);
        NotesDialog fragment = new NotesDialog();
        fragment.setArguments(args);
        return fragment;
    }
}
