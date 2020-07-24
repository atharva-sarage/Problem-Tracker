package com.example.cpapp;

import android.database.Cursor;
import android.database.CursorWrapper;

public class ProblemImageCursorWrapper extends CursorWrapper {
    private static final String TAG = "image cursor wrapper";
    public ProblemImageCursorWrapper(Cursor cursor){
        super(cursor);
    }
    public ProblemImage getImage(){
        String uId = getString(getColumnIndex(ProblemDbSchema.ProblemImages.Cols.UID));
        String fileName = getString(getColumnIndex(ProblemDbSchema.ProblemImages.Cols.FILENAME));

        ProblemImage problemImage = new ProblemImage();
        problemImage.setUid(uId);
        problemImage.setFileName(fileName);
        return problemImage;
    }
}
