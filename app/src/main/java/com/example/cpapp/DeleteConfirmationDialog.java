package com.example.cpapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

public class DeleteConfirmationDialog extends DialogFragment {
    private static final String TAG = "delete dialog";
    public static String EXTRA_RESULT = "result";
    public static String PROBLEM = "problem";
    public Dialog onCreateDialog(Bundle savedInstanceState){
        final String problemId = getArguments().getString(PROBLEM);
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.delete_confirmation,null);
        return new AlertDialog.Builder(getActivity())
                .setView(v)
                .setTitle("Confirm Delete")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sendResult(Activity.RESULT_OK,true,problemId);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sendResult(Activity.RESULT_OK,false,problemId);
                    }
                })
                .create();
    }
    public static DeleteConfirmationDialog newInstance(String problemId) {
        Bundle args = new Bundle();
        args.putString(PROBLEM,problemId);
        DeleteConfirmationDialog fragment = new DeleteConfirmationDialog();
        fragment.setArguments(args);
        return fragment;
    }

    public void sendResult(int resultCode , Boolean result, String problemId){
        if (getTargetFragment() == null) {
            return;
        }
        Intent intent = new Intent();
        intent.putExtra(EXTRA_RESULT, result.toString());
        intent.putExtra(PROBLEM, problemId);
        Log.i(TAG,problemId + result.toString());
        getTargetFragment()
                .onActivityResult(getTargetRequestCode(), resultCode, intent);
    }
}
