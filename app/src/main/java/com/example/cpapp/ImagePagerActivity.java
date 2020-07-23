package com.example.cpapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public class ImagePagerActivity extends FragmentActivity {
    private ViewPager mViewPager;
    private List<Bitmap> mBitmapImages;
    private static String EXTRA_INDEX = "extra_index";
    private static String EXTRA_PROBLEM_ID = "extra_problem_id";
    private static String EXTRA_TOTAL_IMAGES = "extra_total_images";
    private static String TAG = "ImagePagerActivity";
    private static PendingProblem mProblem;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_pager_view);
        String index = getIntent().getStringExtra(EXTRA_INDEX);
        String problemId = (String)getIntent().getStringExtra(EXTRA_PROBLEM_ID);
        final String totalImages = (String)getIntent().getStringExtra(EXTRA_TOTAL_IMAGES);
        mProblem = ProblemDatabase2.get(getApplicationContext()).getProblem(problemId);
        Log.i(TAG,mProblem.getId());
        mViewPager = (ViewPager) findViewById(R.id.image_view_pager);
        try {
            mBitmapImages = ProblemDatabase2.get(getApplicationContext()).getPhotoFiles(mProblem,ImagePagerActivity.this);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.i(TAG,mBitmapImages.size() + "size total");
        FragmentManager fragmentManager = getSupportFragmentManager();
        mViewPager.setAdapter(new FragmentPagerAdapter(fragmentManager) {
            @Override
            public Fragment getItem(int i) {
                return ImageFragment.newInstance(mBitmapImages.get(i), String.valueOf(i+1),totalImages);
            }

            @Override
            public int getCount() {
                return mBitmapImages.size();
            }
        });
        mViewPager.setCurrentItem(Integer.parseInt(index));
    }

    public static Intent newInstance(Context packageContext, int index, UUID uid,int totalImages){
        Intent intent = new Intent(packageContext,ImagePagerActivity.class);
        intent.putExtra(EXTRA_PROBLEM_ID,uid.toString());
        intent.putExtra(EXTRA_INDEX,String.valueOf(index));
        intent.putExtra(EXTRA_TOTAL_IMAGES,String.valueOf(totalImages));
        return intent;
    }
}
