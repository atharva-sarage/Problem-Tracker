package com.example.cpapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.File;
import java.util.List;
import java.util.UUID;

public class addNewProblem extends Fragment {

    private static final String TAG = "addNewProb";
    private static final String EXTRA_ID = "extra_id";
    private static final String EXTRA_TYPE="extra_type";
    private static final int REQUEST_PHOTO = 0;
    private PendingProblem mProblem;
    private EditText mNameText;
    private EditText mIdText;
    private EditText mUrl;
    private EditText mNote;
    private Spinner mSpinner;
    private Button mButton;
    private RecyclerView mImageGridView;
    private Button mAddImageButton;
    private File mPhotoFile;
    private Intent mCaptureImage;
    private ImageAdaptor mImageAdaptor;
    private static String basePath;

    public static addNewProblem newInstance(String problemId,String problemType) {
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_ID, problemId);
        args.putSerializable(EXTRA_TYPE, problemType);
        addNewProblem fragment = new addNewProblem();
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String problemUId = (String)getArguments().getSerializable(EXTRA_ID);
        String problemType = (String)getArguments().getSerializable(EXTRA_TYPE);
        mProblem = ProblemDatabase2.get(getActivity()).getProblem(problemUId);
        mProblem.setType(problemType);
        basePath = ProblemDatabase2.get(getActivity()).getImageGalleryBasePath();

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_problem,container,false);
        mIdText = (EditText) view.findViewById(R.id.addId);
        mIdText.setText(mProblem.getId());
        mNameText = (EditText) view.findViewById(R.id.addName);
        mNameText.setText(mProblem.getName());
        mNote = (EditText) view.findViewById(R.id.addNote);
        mNote.setText(mProblem.getNote());
        mSpinner = (Spinner) view.findViewById(R.id.judge_selector);
        selectSpinnerValue(mSpinner,mProblem.getPlatform());
        mButton= (Button) view.findViewById(R.id.done_button);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mProblem.getType().equals("pending")){
                    Intent intent = MainActivity.newIntent(getContext(),"pending");
                    startActivity(intent);
                }else if(mProblem.getType().equals("favourite")){
                    Intent intent = MainActivity.newIntent(getContext(),"favourite");
                    startActivity(intent);
                }
            }
        });
        mImageGridView = (RecyclerView) view.findViewById(R.id.image_grid_recycler_view);
        mImageGridView.setLayoutManager(new GridLayoutManager(getActivity(),3));

        mAddImageButton = (Button) view.findViewById(R.id.add_image_button);
        mAddImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCaptureImage = getCaptureImageIntent();
                startActivityForResult(mCaptureImage,REQUEST_PHOTO);
            }
        });
        mImageAdaptor = new ImageAdaptor(ProblemDatabase2.get(getActivity()).getPhotoFiles(mProblem, getActivity()));
        mImageGridView.setAdapter(mImageAdaptor);
        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        PendingProblem problemDelete=new PendingProblem();
        problemDelete=mProblem;
        Log.i(TAG,"on pause updated");
        mProblem.setName(""+mNameText.getText());
        mProblem.setPlatform(""+mSpinner.getSelectedItem());
        mProblem.setNote(""+mNote.getText());
        mProblem.setId(""+mIdText.getText());
        Log.i(TAG,mProblem.getId().length()+"$$$");
        if(mProblem.getId().length()>0)
            ProblemDatabase2.get(getActivity()).updateProblem(mProblem);
        else
            ProblemDatabase2.get(getActivity()).deleteProblem(mProblem);
    }
    private void selectSpinnerValue(Spinner spinner, String myString)
    {
        int index = 0;
        for(int i = 0; i < spinner.getCount(); i++){
            if(spinner.getItemAtPosition(i).toString().equals(myString)){
                spinner.setSelection(i);
                break;
            }
        }
    }

    private class ImageHolder extends RecyclerView.ViewHolder{
        private ImageView mImage;
        private Bitmap mImageBitmap;
        public ImageHolder(View itemView) {
            super(itemView);
            mImage = itemView.findViewById(R.id.problem_photo);
        }
        public void bind(Bitmap imageBitmap, final int imageIndex){
            mImageBitmap = imageBitmap;
            mImage.setImageBitmap(mImageBitmap);
            Log.i(TAG, String.valueOf(mImageBitmap));
            mImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    File photoFile = new File(basePath+mProblem.getImagePath(imageIndex).toString());
                    Uri uri = FileProvider.getUriForFile(getContext(),
                            BuildConfig.APPLICATION_ID + ".provider",
                            photoFile);
                    Log.i(TAG,photoFile.toString());
                    intent.setDataAndType(uri, "image/*");
                    startActivity(intent);
                }
            });
        }
    }
    private class ImageAdaptor extends RecyclerView.Adapter<ImageHolder>{

        private List<Bitmap> mImageBitmaps;

        public ImageAdaptor(List<Bitmap> imageBitmaps){
            Log.i(TAG, String.valueOf(imageBitmaps.size())+"image Files count");
            mImageBitmaps = imageBitmaps;
        }
        @Override
        public ImageHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater
                    .inflate(R.layout.image_fragment, parent, false);
            return new ImageHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ImageHolder imageHolder, int i) {
            Bitmap imageFile = mImageBitmaps.get(i);
            imageHolder.bind(imageFile,i);
        }

        public void setImageBitmaps(List<Bitmap> imageBitmaps){
            mImageBitmaps=imageBitmaps;
        }

        @Override
        public int getItemCount() {
            return mImageBitmaps.size();
        }
    }

    public Intent getCaptureImageIntent(){
        Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        mPhotoFile = ProblemDatabase2.get(getActivity()).getPhotoFile(mProblem);
        Log.i(TAG,mPhotoFile.getPath());
        boolean canTakePhoto = mPhotoFile != null;
        mAddImageButton.setEnabled(canTakePhoto);
        if(canTakePhoto){
            Uri uri = FileProvider.getUriForFile(getContext(),
                    BuildConfig.APPLICATION_ID + ".provider",
                    mPhotoFile);
            captureImage.putExtra(MediaStore.EXTRA_OUTPUT,uri);
        }
        return captureImage;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == REQUEST_PHOTO) {
            mImageAdaptor.setImageBitmaps(ProblemDatabase2.get(getActivity()).getPhotoFiles(mProblem, getActivity()));
            mImageAdaptor.notifyDataSetChanged();
        }
    }
}
