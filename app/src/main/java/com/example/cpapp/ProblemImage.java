package com.example.cpapp;

import java.util.UUID;

public class ProblemImage {
    private UUID mUid;
    private String mFileName="";
    private static final String TAG = "Problem Image";
    public ProblemImage(){

    }

    public ProblemImage(UUID uid, String filePath) {
        mUid=uid;
        mFileName = filePath;
    }

    public void setUid(String uId) {
        mUid = UUID.fromString(uId);
    }
    public UUID getUid(){
        return mUid;
    }

    public void setFileName(String fileName) {
        mFileName = fileName;
    }

    public String getFileName(){
        return mFileName;
    }
}
