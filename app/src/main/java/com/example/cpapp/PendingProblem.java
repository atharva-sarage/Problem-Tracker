package com.example.cpapp;

import android.util.Log;

import java.util.Date;
import java.util.UUID;

public class PendingProblem {
    private UUID mUid;
    private String mName="";
    private String mId="";
    private String mUrl="";
    private String mPlatform="";
    private String mNote="";
    private String mType;
    private int mPhotoCount=0;
    private static final String TAG = "Pending problem";
    public PendingProblem(){
        mUid=UUID.randomUUID();
    }
    public PendingProblem(UUID id){
        mUid = id;
    }
    public UUID getUid(){return mUid;}
    public void setName(String name){
        mName=name;
    }
    public String getName(){
        return mName;
    }
    public void setId(String id){
        mId=id;
    }
    public String getId(){
        return mId;
    }
    public void setNote(String note){
        mNote=note;
    }
    public String getNote(){
        return mNote;
    }
    public String getUrl(){
        Log.i(TAG,"get url called");
        if(mPlatform.equals("codeforces")){
            String ProblemCode = mId.substring(mId.length()-1);
            String ContestCode = mId.substring(0, mId.length() - 1);
            mUrl="https://codeforces.com/problemset/problem/"+ContestCode+"/"+ProblemCode;
        }else if(mPlatform.equals("codechef")){
            mUrl="https://www.codechef.com/problems/"+mId;
        } else if(mPlatform.equals("spoj")){
            mUrl="https://www.codechef.com/problems/"+mId;
        } else{
            mUrl=null;
        }
        return mUrl;
    }
    public void setPlatform(String platform){
        mPlatform=platform;
    }
    public String getPlatform(){
        return mPlatform;
    }
    public String getType(){
        return mType;
    }
    public void setType(String type){
        mType=type;
    }
    public String getFileName(){ // this has side effect need to fix this
        String path= "IMG_"+ getUid().toString()+ mPhotoCount +".jpg";
        Log.i(TAG,path);
        return path;
    }
    public void increasePhotoCount(){
        mPhotoCount++;
    }
    public String getBaseImgPath(){
        return "IMG_"+getUid().toString();
    }
    public String getImagePath(int idx){
        String path= "IMG_"+ getUid().toString()+ idx +".jpg";
        return path;
    }
    public int getPhotoCount(){
        return mPhotoCount;
    }
    public void setPhotoCount(int photoCount){
        mPhotoCount = photoCount;
    }

}
