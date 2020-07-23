package com.example.cpapp;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.cpapp.ProblemDbSchema.ProblemTable;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
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

    private static ContentValues getContentValues(PendingProblem problem) {
        ContentValues values = new ContentValues();
        values.put(ProblemTable.Cols.UID, problem.getUid().toString());
        values.put(ProblemTable.Cols.ID, problem.getId().toString());
        values.put(ProblemTable.Cols.NAME, problem.getName());
        values.put(ProblemTable.Cols.NOTE, problem.getNote());
        values.put(ProblemTable.Cols.URL, problem.getUrl());
        values.put(ProblemTable.Cols.PLATFORM, problem.getPlatform());
        values.put(ProblemTable.Cols.TYPE,problem.getType());
        values.put(ProblemTable.Cols.PHOTOCOUNT,problem.getPhotoCount());
        return values;
    }

    public void addProblem(PendingProblem pb) {
        ContentValues values = getContentValues(pb);
        mDatabase.insert(ProblemTable.NAME, null, values);
    }

    public void deleteProblem(PendingProblem c) {
        ContentValues values = getContentValues(c);
        mDatabase.delete(ProblemTable.NAME, ProblemTable.Cols.UID + " = ?",
                new String[]{c.getUid().toString()});
    }

    public void updateProblem(PendingProblem c) {
        ContentValues values = getContentValues(c);
        mDatabase.update(ProblemTable.NAME, values,ProblemTable.Cols.UID + " = ?",
                new String[]{c.getUid().toString()});
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
        for (PendingProblem removeProblem : RemoveList)
            deleteProblem(removeProblem);
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
    public List<Bitmap> getPhotoFiles(PendingProblem problem, Activity activity){
        List<Bitmap>photoBitmapList = new ArrayList<Bitmap>();
        Log.i(TAG,problem.getPhotoCount()+"&&&&&&&&&&&&&&");
        for(int i=1;i<=problem.getPhotoCount();i++){
            String path = problem.getBaseImgPath()+i+".jpg";
            Bitmap img= PictureUtils.getScaledBitmap(getImageGalleryBasePath()+path,activity);
            if(img!=null)
                photoBitmapList.add(img);
        }
        Log.i(TAG,photoBitmapList.size()+"%%%%%%%%%%%%%%%%%%%%%%%%%%%");
        return photoBitmapList;
    }
    public String getImageGalleryBasePath(){
        File externalFilesDir = mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return externalFilesDir.getPath()+"/";
    }
}
