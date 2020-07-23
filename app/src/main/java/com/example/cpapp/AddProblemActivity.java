package com.example.cpapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;

import java.util.UUID;

public class AddProblemActivity extends SingleFragmentActivity {
    private static final String EXTRA_ID = "extra_id";
    private static final String EXTRA_TYPE = "extra_type";
    private static final String TAG = "addproblem";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected Fragment createFragment() {
        String problemId = (String)getIntent().getStringExtra(EXTRA_ID);
        String problemType = (String)getIntent().getStringExtra(EXTRA_TYPE);
        return addNewProblem.newInstance(problemId,problemType);
    }

    public static Intent newIntent(Context packageContext, String id,String type){
        Intent intent = new Intent(packageContext,AddProblemActivity.class);
        intent.putExtra(EXTRA_ID,id);
        intent.putExtra(EXTRA_TYPE,type);
        return intent;
    }
}
