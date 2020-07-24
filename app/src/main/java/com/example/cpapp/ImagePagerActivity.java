package com.example.cpapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.os.Bundle;
import android.os.Parcelable;
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
    private static final String DELETE_IMAGE_INDEX = "delete_image_index";
    private static final String CALLER_FLAG = "caller_flag";
    private static final String EXTRA_PROBLEM_TYPE = "problem_type";
    private ViewPager mViewPager;
    private List<Bitmap> mBitmapImages;
    private static String EXTRA_INDEX = "extra_index";
    private static String EXTRA_PROBLEM_ID = "extra_problem_id";
    private static String EXTRA_TOTAL_IMAGES = "extra_total_images";
    private static String TAG = "ImagePagerActivity";
    private static PendingProblem mProblem;
    private String totalImages;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_pager_view);
        String callerFlag = getIntent().getStringExtra(CALLER_FLAG);
        String index = getIntent().getStringExtra(EXTRA_INDEX);
        String problemId = (String)getIntent().getStringExtra(EXTRA_PROBLEM_ID);
        final String problemType = (String)getIntent().getStringExtra(EXTRA_PROBLEM_TYPE);
        totalImages = (String)getIntent().getStringExtra(EXTRA_TOTAL_IMAGES);
        mProblem = ProblemDatabase2.get(getApplicationContext()).getProblem(problemId);
        Log.i(TAG,mProblem.getId());
        mViewPager = (ViewPager) findViewById(R.id.image_view_pager);
        if(callerFlag.equals(ImageFragment.DELETE_IMAGE_FLAG)) {
            // remove that file from db and recompute mBitmapImages
            ProblemDatabase2 mDatabase=ProblemDatabase2.get(getApplicationContext());
            List<ProblemImage> problemImages=mDatabase.getProblemImages(problemId);
            Log.i(TAG, String.valueOf(Integer.parseInt(index)-1));
            ProblemImage deleteImage=problemImages.get(Integer.parseInt(index)-1);
            mDatabase.deleteImage(deleteImage);
            totalImages = String.valueOf((Integer.parseInt(totalImages)-1));
            Log.i(TAG,totalImages);
            if(totalImages.equals("0"))
                onBackPressed();
        }
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
                return ImageFragment.newInstance(mBitmapImages.get(i), String.valueOf(i+1),totalImages,mProblem.getUid().toString(),problemType);
            }

            @Override
            public int getCount() {
                return mBitmapImages.size();
            }


        });
        mViewPager.setCurrentItem(Integer.parseInt(index));

    }

    @Override
    protected void onSaveInstanceState(Bundle oldInstanceState) {
        super.onSaveInstanceState(oldInstanceState);
        oldInstanceState.clear();
    }

    public static Intent newInstance(Context packageContext, String callerFlag,String index, String uid,int totalImages, String problemType){
        Intent intent = new Intent(packageContext,ImagePagerActivity.class);
        intent.putExtra(CALLER_FLAG,String.valueOf(callerFlag));
        intent.putExtra(EXTRA_PROBLEM_ID, uid.toString());
        intent.putExtra(EXTRA_INDEX, String.valueOf(index));
        intent.putExtra(EXTRA_TOTAL_IMAGES, String.valueOf(totalImages));
        intent.putExtra(EXTRA_PROBLEM_TYPE,problemType);
        return intent;
    }

    @Override
    public void onBackPressed() {
        Intent intent=AddProblemActivity.newIntent(getApplicationContext(), String.valueOf(mProblem.getUid()),mProblem.getType());
        startActivity(intent);
    }
}
