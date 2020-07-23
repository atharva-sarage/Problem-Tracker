package com.example.cpapp;

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
    private static String ARG_BITMAP_IMAGE = "bitmap_image";
    private static String ARG_IMG_INDEX = "img_index";
    private static String ARG_TOTAL_COUNT = "photo_count";
    private static String TAG = "ImageFragment";
    private Bitmap mImageBitmap;
    private static TextView mImageIndex;
    private ZoomageView mImageView;
    private String mIndex;
    private static String mPhotoCount;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mImageBitmap = getArguments().getParcelable(ARG_BITMAP_IMAGE);
        mIndex = getArguments().getString(ARG_IMG_INDEX);
        mPhotoCount = getArguments().getString(ARG_TOTAL_COUNT);
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

        Log.i(TAG,mImageBitmap.toString());
        Log.i(TAG,mIndex);
        return view;
    }

    public static Fragment newInstance(Bitmap bitmapImage, String index, String photoCount){
        Bundle args = new Bundle();
        args.putParcelable(ARG_BITMAP_IMAGE,bitmapImage);
        args.putString(ARG_IMG_INDEX,index);
        args.putString(ARG_TOTAL_COUNT,photoCount);
        ImageFragment fragment = new ImageFragment();
        fragment.setArguments(args);
        return fragment;
    }
}
