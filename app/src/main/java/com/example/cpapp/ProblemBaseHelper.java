package com.example.cpapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.cpapp.ProblemDbSchema.ProblemTable;
import com.example.cpapp.ProblemDbSchema.ProblemImages;

public class ProblemBaseHelper extends SQLiteOpenHelper {
    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "problem4.db";
    public ProblemBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table "+ ProblemTable.NAME + "(" +
                "_id integer primary key autoincrement,"+
                ProblemTable.Cols.UID+" unique , "+
                ProblemTable.Cols.ID+", "+
                ProblemTable.Cols.NAME+", "+
                ProblemTable.Cols.NOTE+", "+
                ProblemTable.Cols.URL + ", "+
                ProblemTable.Cols.DESPR +", "+
                ProblemTable.Cols.PLATFORM +", "+
                ProblemTable.Cols.TYPE +", "+
                ProblemTable.Cols.PHOTOCOUNT +
                ")"
        );
        db.execSQL("create table "+ ProblemDbSchema.ProblemImages.NAME + "(" +
                "_id integer primary key autoincrement," +
                ProblemImages.Cols.UID +", "+
                ProblemImages.Cols.FILENAME+", " +
                "FOREIGN KEY (" + ProblemImages.Cols.UID + ") REFERENCES "+
                ProblemTable.NAME +"("+ ProblemTable.Cols.UID + ")"+
                ")"
        );
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
    @Override
    public void onConfigure(SQLiteDatabase db){
        db.setForeignKeyConstraintsEnabled(true);
    }

}
