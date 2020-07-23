package com.example.cpapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import static android.support.v4.os.LocaleListCompat.create;

public class UserNameDialog extends DialogFragment {
    public static final String EXTRA_USERNAME = "username";
    public Dialog onCreateDialog(Bundle savedInstanceState){
        final EditText mTextView;
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.usernamedialog,null);
        mTextView = (EditText) v.findViewById(R.id.userName_text_view);
        return new AlertDialog.Builder(getActivity())
                .setView(v)
                .setTitle("UserName")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String userName= String.valueOf(mTextView.getText());
                        sendResult(Activity.RESULT_OK,userName);
                    }
                })
                .create();
    }

    public static UserNameDialog newInstance() {
        Bundle args = new Bundle();
        UserNameDialog fragment = new UserNameDialog();
        return fragment;
    }
    public void sendResult(int resultCode , String userName){
        if (getTargetFragment() == null) {
            return;
        }
        Intent intent = new Intent();
        intent.putExtra(EXTRA_USERNAME, userName);
        getTargetFragment()
                .onActivityResult(getTargetRequestCode(), resultCode, intent);
    }
}
