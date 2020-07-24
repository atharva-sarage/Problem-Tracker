package com.example.cpapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.jsibbold.zoomage.ZoomageView;
import android.widget.ImageView;
import android.widget.TextView;

public class ImageFragment extends Fragment {
    private static final String ARG_PROBLEM_UID = "problem_uid";
    private static final String ARG_PROBLEM_TYPE = "problem_type";
    private static String ARG_BITMAP_IMAGE = "bitmap_image";
    private static String ARG_IMG_INDEX = "img_index";
    private static String ARG_TOTAL_COUNT = "photo_count";
    public static String DELETE_IMAGE_FLAG = "1";
    private static String TAG = "ImageFragment";
    private Bitmap mImageBitmap;
    private static TextView mImageIndex;
    private ZoomageView mImageView;
    private ImageView mDeleteImage;
    private String mIndex;
    private String mUid;
    private String mType;
    private static String mPhotoCount;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mImageBitmap = getArguments().getParcelable(ARG_BITMAP_IMAGE);
        mIndex = getArguments().getString(ARG_IMG_INDEX);
        mPhotoCount = getArguments().getString(ARG_TOTAL_COUNT);
        mUid = getArguments().getString(ARG_PROBLEM_UID);
        mType = getArguments().getString(ARG_PROBLEM_TYPE);
        Log.i(TAG,mIndex+": index");
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        Log.i(TAG, "on create view");
        View view = inflater.inflate(R.layout.image_fragment, container, false);
        mImageView = (ZoomageView)view.findViewById(R.id.problem_photo);
        mImageView.setImageBitmap(mImageBitmap);

        mImageIndex = (TextView) view.findViewById(R.id.pager_view_count_display);
        Log.i(TAG, mIndex+"/"+mPhotoCount);
        mImageIndex.setText(mIndex+"/"+ mPhotoCount);

        mDeleteImage = (ImageView) view.findViewById(R.id.delete_image_id);
        mDeleteImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = ImagePagerActivity.newInstance(getActivity(),DELETE_IMAGE_FLAG,mIndex,mUid, Integer.parseInt(mPhotoCount),mType);
                getActivity().startActivity(intent);
            }
        });
        Log.i(TAG,mImageBitmap.toString());
        Log.i(TAG,mIndex);
        return view;
    }

    public static Fragment newInstance(Bitmap bitmapImage, String index, String photoCount, String uid, String problemType){
        Bundle args = new Bundle();
        args.putParcelable(ARG_BITMAP_IMAGE,bitmapImage);
        args.putString(ARG_IMG_INDEX,index);
        args.putString(ARG_TOTAL_COUNT,photoCount);
        args.putString(ARG_PROBLEM_UID,uid);
        args.putString(ARG_PROBLEM_TYPE,problemType);
        ImageFragment fragment = new ImageFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Intent intent = AddProblemActivity.newIntent(getContext(),mUid,mType);
        startActivity(intent);
    }
}
