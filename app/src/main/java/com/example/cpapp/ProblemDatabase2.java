package com.example.cpapp;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.util.Log;

import com.example.cpapp.ProblemDbSchema.ProblemTable;
import com.example.cpapp.ProblemDbSchema.ProblemImages;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.UUID;

public class ProblemDatabase2 {

    public static final String TAG = "pdatabase2";
    private static final String SOLVED ="solved";
    public static ProblemDatabase2 mproblemDatabase;
    private Context mContext;
    private SQLiteDatabase mDatabase;

    private ProblemDatabase2(Context context) {
        mContext = context.getApplicationContext();
        mDatabase = new ProblemBaseHelper(mContext).getWritableDatabase();
        Log.i(TAG,mDatabase.toString());
    }

    public static ProblemDatabase2 get(Context context) {
        if (mproblemDatabase == null) {
            Log.i(TAG,"First");
            mproblemDatabase = new ProblemDatabase2(context);
        }
        return mproblemDatabase;
    }

    private ProblemCursorWrapper queryProblems(String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(
                ProblemTable.NAME,
                null, // Columns - null selects all columns
                whereClause,
                whereArgs,
                null, // groupBy
                null, // having
                null // orderBy
        );
        return new ProblemCursorWrapper(cursor);
    }

    private ProblemImageCursorWrapper queryImages(String whereClause, String[] whereArgs){
        Cursor cursor = mDatabase.query(
                ProblemImages.NAME,
                null, // Columns - null selects all columns
                whereClause,
                whereArgs,
                null, // groupBy
                null, // having
                null // orderBy
        );
        return new ProblemImageCursorWrapper(cursor);
    }

    private static ContentValues getContentValues(PendingProblem problem,String problemCount) {
        ContentValues values = new ContentValues();
        values.put(ProblemTable.Cols.UID, problem.getUid().toString());
        values.put(ProblemTable.Cols.ID, problem.getId().toString());
        values.put(ProblemTable.Cols.NAME, problem.getName());
        values.put(ProblemTable.Cols.NOTE, problem.getNote());
        values.put(ProblemTable.Cols.URL, problem.getUrl());
        values.put(ProblemTable.Cols.PLATFORM, problem.getPlatform());
        values.put(ProblemTable.Cols.TYPE,problem.getType());
        values.put(ProblemTable.Cols.PHOTOCOUNT,problemCount);
        return values;
    }

    private static ContentValues getContentValues(ProblemImage problemImage){
        ContentValues values = new ContentValues();
        values.put(ProblemImages.Cols.UID,problemImage.getUid().toString());
        values.put(ProblemImages.Cols.FILENAME,problemImage.getFileName());
        return values;
    }

    public void addProblem(PendingProblem pb) {
        ContentValues values = getContentValues(pb, String.valueOf(pb.getPhotoCount(mContext)));
        mDatabase.insert(ProblemTable.NAME, null, values);
    }

    public void deleteProblem(PendingProblem c) {
        ContentValues values = getContentValues(c, String.valueOf(c.getPhotoCount(mContext)));
        mDatabase.delete(ProblemTable.NAME, ProblemTable.Cols.UID + " = ?",
                new String[]{c.getUid().toString()});
    }

    public void updateProblem(PendingProblem c) {
        Log.i(TAG,"adding problem"+c.getUid());
        ContentValues values = getContentValues(c, String.valueOf(c.getPhotoCount(mContext)));
        mDatabase.update(ProblemTable.NAME, values,ProblemTable.Cols.UID + " = ?",
                new String[]{c.getUid().toString()});
    }

    public void addProblemImage(ProblemImage pb){
        ContentValues values = getContentValues(pb);
        mDatabase.insert(ProblemImages.NAME,null,values);
    }

    public void deleteImage(ProblemImage c) {
        ContentValues values = getContentValues(c);
        mDatabase.delete(ProblemImages.NAME, ProblemImages.Cols.FILENAME + " = ?",
                new String[]{c.getFileName().toString()});
    }

    public List<PendingProblem> getProblems(String type) {
        List<PendingProblem> problems = new ArrayList<>();
        Log.i(TAG,type+"???");
        ProblemCursorWrapper cursor = queryProblems(ProblemTable.Cols.TYPE+" = ?", new String[]{type});
        Log.i(TAG, String.valueOf(cursor));
        try {
            if(cursor!=null ) {
                if (cursor.moveToFirst()) {
                    do{
                        problems.add(cursor.getProblem());
                    }while(cursor.moveToNext());
                }
            }
        } finally {
            cursor.close();
        }
        for(PendingProblem pb:problems)
            Log.i(TAG,"???"+pb.getId());
        return problems;
    }

    public PendingProblem getProblem(String id) {
         ProblemCursorWrapper cursor = queryProblems(
                ProblemTable.Cols.UID + " = ?",
                new String[]{id}
        );
        try {
            if (cursor.getCount() == 0) {
                return null;
            }
            cursor.moveToFirst();
            return cursor.getProblem();
        } finally {
            cursor.close();
        }
    }

    public List<ProblemImage> getProblemImages(String uId){
        List<ProblemImage> problemImages = new ArrayList<>();
        ProblemImageCursorWrapper cursor = queryImages(ProblemImages.Cols.UID+" = ?", new String[]{uId});
        Log.i(TAG, String.valueOf(cursor));
        try {
            if(cursor!=null ) {
                if (cursor.moveToFirst()) {
                    do{
                        problemImages.add(cursor.getImage());
                    }while(cursor.moveToNext());
                }
            }
        } finally {
            cursor.close();
        }
        for(ProblemImage pb:problemImages)
            Log.i(TAG,"???"+pb.getFileName());
        return problemImages;
    }

    public void isSolved(List<PendingProblem> problems, JSONArray submissions, Set<String> codeChefProblems) throws JSONException {
        Log.i(TAG, String.valueOf(problems.size()));
        Set<PendingProblem> RemoveList = new HashSet<>();
        for (PendingProblem problem : problems) {
            Log.i(TAG, problem.getId() + problem.getPlatform()+codeChefProblems.toString());
            if (problem.getPlatform().equals("codechef")) {
                Log.i(TAG, String.valueOf(codeChefProblems.contains(problem.getId())));
                if (codeChefProblems.contains(problem.getId())) {
                    Log.i(TAG, "removed");
                    RemoveList.add(problem);
                }
            }
            boolean isSolved = getProblemStatus(submissions, problem.getId());
        }
        for (PendingProblem removeProblem : RemoveList){
            setStatusSolved(removeProblem);
        }
    }

    private void setStatusSolved(PendingProblem problem){
        problem.setType(SOLVED);
        ContentValues values = getContentValues(problem, String.valueOf(problem.getPhotoCount(mContext)));
        mDatabase.update(ProblemTable.NAME, values,ProblemTable.Cols.UID + " = ?",
                new String[]{problem.getUid().toString()});
    }
    public boolean getProblemStatus(JSONArray userSubmissions, String id) throws JSONException {
        return false;
    }

    public File getPhotoFile ( PendingProblem problem){
        File externalFilesDir = mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if(externalFilesDir == null)
            return null;
        problem.increasePhotoCount();
        return new File(externalFilesDir,problem.getFileName());
    }
    public List<Bitmap> getPhotoFiles(PendingProblem problem, Activity activity) throws IOException {
        List<Bitmap>photoBitmapList = new ArrayList<Bitmap>();
        File externalFilesDir = mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        Log.i(TAG,problem.getPhotoCount(mContext)+"");
        List<ProblemImage> problemImages = new ArrayList<>();
        Log.i(TAG,problem.getUid().toString());
        problemImages = getProblemImages(String.valueOf(problem.getUid()));
        Log.i(TAG,"problem images: "+ problemImages.size());
        for(ProblemImage problemImage : problemImages){
            Bitmap img= PictureUtils.getScaledBitmap(getImageGalleryBasePath()+problemImage.getFileName(),activity);
            File mImg = new File(externalFilesDir, problemImage.getFileName());
            Uri mImgURI = FileProvider.getUriForFile(mContext, mContext.getPackageName() + ".provider", mImg);
            Log.i(TAG,mImgURI.toString());
            Log.i(TAG,problemImage.getFileName());
            img = PictureUtils.rotateImageIfRequired(mContext,img, mImgURI);
            Log.i(TAG, String.valueOf(img)+"img rotated");
            if(img!=null)
                photoBitmapList.add(img);
        }
        Log.i(TAG,photoBitmapList.size()+"");
        return photoBitmapList;
    }
    public String getImageGalleryBasePath(){
        File externalFilesDir = mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return externalFilesDir.getPath()+"/";
    }

    public int getProblemImageCount(PendingProblem problem) {
        return getProblemImages(String.valueOf(problem.getUid())).size();
    }
}
