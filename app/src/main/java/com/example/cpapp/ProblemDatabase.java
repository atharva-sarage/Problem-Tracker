package com.example.cpapp;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ProblemDatabase {
    public static final String TAG ="pdatabase";
    public static ProblemDatabase mproblemDatabase;
    public List<PendingProblem> mProblems;

    public static ProblemDatabase get(Context context){
        if(mproblemDatabase == null){
            mproblemDatabase = new ProblemDatabase(context);
        }
        return mproblemDatabase;
    }
    public ProblemDatabase(Context context){
        mProblems= new ArrayList<>();
        PendingProblem problem2= new PendingProblem();
        problem2.setName("-----");
        problem2.setId("RESQ");
        problem2.setNote("$$$$$$$$$$$$$$$$$$$$$");
        problem2.setPlatform("codechef");
        mProblems.add(problem2);
        for(int i=2;i<=5;i++){
            PendingProblem problem= new PendingProblem();
            problem.setId(""+i+"A");
            problem.setName("i"+i);
            problem.setNote("???????????????????????????????/");
            problem.setPlatform("codechef");
            Log.e(TAG,""+i);
            mProblems.add(problem);
        }
    }
    public  List<PendingProblem> getProblems(){
        return mProblems;
    }
    public  void setProblems(List<PendingProblem> problems){
        mProblems=problems;
    }
    public PendingProblem getProblem(String id ){
        for (PendingProblem problem : mProblems){
            if(problem.getId().equals(id))
                return problem;
        }
        return null;
    }
    public void updateProblem(PendingProblem updProblem){
        for (int i=0;i<mProblems.size();i++){
            if(mProblems.get(i).getId().equals(updProblem.getId())) {
                mProblems.set(i,updProblem);
                break;
            }
        }
    }
    public void addProblem(PendingProblem problem){
        mProblems.add(problem);
    }

    public List<PendingProblem> isSolved(List<PendingProblem> problems, JSONArray submissions, Set<String> codeChefProblems) throws JSONException {
        Log.i(TAG, String.valueOf(problems.size()));
        Set<PendingProblem>RemoveList=new HashSet<>();
        for(PendingProblem problem:problems){
            Log.i(TAG,problem.getId()+problem.getPlatform());
            if(problem.getPlatform().equals("codechef")){
                Log.i(TAG, String.valueOf(codeChefProblems.contains(problem.getId())));
                if(codeChefProblems.contains(problem.getId())){
                    Log.i(TAG,"removed");
                    RemoveList.add(problem);
                }
            }
            boolean isSolved=getProblemStatus(submissions,problem.getId());
        }
        for(PendingProblem removeProblem:RemoveList)
            problems.remove(removeProblem);
        return problems;
    }
    public boolean getProblemStatus(JSONArray userSubmissions,String id) throws JSONException {

        boolean temp=false;
//        for(int i=0;i<userSubmissions.length();i++){
//            try {
//                JSONObject submissionJsonObject = userSubmissions.getJSONObject(i);
//                Log.i(TAG,submissionJsonObject.toString());
//            }catch (JSONException e){
//                e.printStackTrace();
//            }
//        }
        return temp;
    }
}
