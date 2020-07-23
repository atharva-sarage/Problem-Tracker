package com.example.cpapp;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private static final String TYPE = "typeFragment";
    private static final String TAG = "Main Activity";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);
        Log.i(TAG,"on create");
        String type = getIntent().getStringExtra(TYPE);
        if(type==(null))
            type="";
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragment_container);
        if (fragment == null) {
            fragment = createFragment(type);
            fm.beginTransaction()
                    .add(R.id.fragment_container, fragment)
                    .commit();
        }
    }
    public Fragment createFragment(String type)
    {
        if(type.equals("favourite"))
            return FavouriteFragment.newInstance();
        else if(type.equals("solved"))
            return SolvedFragment.newInstance();
        else
            return PendingListFragment.newInstance();

    }
    public static  Intent newIntent(Context packageContext,String type){
        Intent intent = new Intent(packageContext,MainActivity.class);
        intent.putExtra(TYPE,type);
        return intent;
    }
}
