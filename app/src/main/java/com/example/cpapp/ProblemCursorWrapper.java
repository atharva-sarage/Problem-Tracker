package com.example.cpapp;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.example.cpapp.ProblemDbSchema.ProblemTable;

import java.util.UUID;

public class ProblemCursorWrapper extends CursorWrapper {
    public ProblemCursorWrapper (Cursor cursor){
        super(cursor);
    }
    public PendingProblem getProblem(){
        String uId = getString(getColumnIndex(ProblemTable.Cols.UID));
        String Id = getString(getColumnIndex(ProblemTable.Cols.ID));
        String Name = getString(getColumnIndex(ProblemTable.Cols.NAME));
        String Note = getString(getColumnIndex(ProblemTable.Cols.NOTE));
        String Url = getString(getColumnIndex(ProblemTable.Cols.URL));
        String Platform = getString(getColumnIndex(ProblemTable.Cols.PLATFORM));
        String Type = getString(getColumnIndex(ProblemTable.Cols.TYPE));
        String PhotoCount = getString(getColumnIndex(ProblemTable.Cols.PHOTOCOUNT));

        PendingProblem problem = new PendingProblem(UUID.fromString(uId));
        problem.setId(Id);
        problem.setName(Name);
        problem.setPlatform(Platform);
        problem.setNote(Note);
        problem.setType(Type);
        problem.setPhotoCount(Integer.parseInt(PhotoCount));
        return problem;
    }
}
