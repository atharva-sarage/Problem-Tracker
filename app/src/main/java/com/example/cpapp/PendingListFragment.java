package com.example.cpapp;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class PendingListFragment extends Fragment {
    private static final String TAG = "pending list fragment";
    private static final String NOTES="notes";
    private static final String SOLVED_ARRAY="codechef_problem_array";
    private static final int SETNOTES=1;
    private static final int REQUEST_USERNAME = 2;
    private static final String SETUSERNAME="username";
    private static String mUserName=null;
    private RecyclerView mRecyclerView;
    private ProblemAdapter mAdapter;
    private Button mclickToAdd;
    public JSONArray mSubmissions;
    private SwipeRefreshLayout mpullToRefresh;
    private Set<String> mCodechefPoblems=null;
    private Boolean firstRefresh=false;
    public static Fragment newInstance(){
        return new PendingListFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG,"saved problems");
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        if(savedInstanceState!=null){
            Log.i(TAG,"codechef problems set now");
            mCodechefPoblems=(Set)savedInstanceState.getSerializable(SOLVED_ARRAY);
        }
        else if(prefs.getStringSet("PROBLEMS",null)!=null){
            Log.i(TAG,"problems loaded");
            mCodechefPoblems = prefs.getStringSet("PROBLEMS",null);
        }
        else {
            if (!prefs.getBoolean("firstTime", false)) {
                Log.i(TAG, "set user name");
                FragmentManager manager = getFragmentManager();
                UserNameDialog mUserNameDialog = UserNameDialog.newInstance();
                mUserNameDialog.setTargetFragment(PendingListFragment.this, REQUEST_USERNAME);
                mUserNameDialog.show(manager, SETUSERNAME);
                SharedPreferences.Editor editor = prefs.edit();
                editor.commit();
            }else{
                new ScrapeItemsTask().execute();
            }
        }

        setHasOptionsMenu(true);
    }
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == REQUEST_USERNAME) {
            String username = data.getStringExtra(UserNameDialog.EXTRA_USERNAME);
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
            SharedPreferences.Editor editor = prefs.edit();
            if(!username.equals("") && username!=null) {
                editor.putBoolean("firstTime", true);
                editor.putString("userName", username);
            }
            Log.i(TAG,"user name value obtained");
            editor.commit();
            mUserName=prefs.getString("userName","");
            Log.i(TAG,mUserName+"???????####");
            String url="https://www.codechef.com/users/"+mUserName;
            new ScrapeItemsTask().execute(url);
        }
    }
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putSerializable(SOLVED_ARRAY, (Serializable) mCodechefPoblems);
        Log.i(TAG,"mCodechefProblems added");
    }

    @Override
    public void onPause() {
        super.onPause();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        SharedPreferences.Editor editor = prefs.edit();
        editor.putStringSet("PROBLEMS",mCodechefPoblems);
        editor.commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_new_problem:
                PendingProblem problem = new PendingProblem();
                ProblemDatabase2.get(getActivity()).addProblem(problem);
                Intent intent = AddProblemActivity
                        .newIntent(getActivity(), problem.getUid().toString(),"pending");
                startActivity(intent);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.pending_fragment,container,false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.pending_list_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        BottomNavigationView bottomNavigationView = (BottomNavigationView) view.findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_pending:
                        break;
                    case R.id.action_favorites:
                        FragmentManager manager = getFragmentManager();
                        Fragment  fragment = new FavouriteFragment();
                        manager.beginTransaction()
                                .replace(R.id.fragment_container,fragment)
                                .commit();
                        break;
                     }
                return true;
            }
        });
        try {
            Log.i(TAG,""+firstRefresh);
            System.out.println(mCodechefPoblems);
            UpdateUI(false);
        } catch (JSONException  e) {
            e.printStackTrace();
        }
        mpullToRefresh = view.findViewById(R.id.pullToRefresh);
        mpullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                try {
                    UpdateUI(true);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {if(mpullToRefresh.isRefreshing()) {
                        mpullToRefresh.setRefreshing(false);
                    }        }
                }, 500);
            }
        });
        try {
            UpdateUI(false);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return view;
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        Log.i(TAG,"????");
        inflater.inflate(R.menu.fragment_list_menu, menu);

    }


    private class ProblemHolder extends RecyclerView.ViewHolder{
        private Button mOpenButton;
        private Button mNotes;
        private TextView mTextView;
        private TextView mIdView;
        private PendingProblem mProblem;

        public ProblemHolder(View itemView){
            super(itemView);
            mNotes = (Button) itemView.findViewById(R.id.open_notes);
            mNotes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FragmentManager manager = getFragmentManager();
                    NotesDialog dialog = NotesDialog
                            .newInstance(mProblem.getNote().toString());
                    dialog.setTargetFragment(PendingListFragment.this,SETNOTES);
                    dialog.show(manager,NOTES);
                }
            });
            mTextView = (TextView) itemView.findViewById(R.id.problem_name);
            mTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = AddProblemActivity
                            .newIntent(getActivity(), ""+mProblem.getUid(),"pending"); // how to get id here
                    startActivity(intent);
                }
            });
            mOpenButton = (Button) itemView.findViewById(R.id.open_problem_button);
            mOpenButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i(TAG,mProblem.getUrl());
                    Intent webIntent= new Intent(Intent.ACTION_VIEW, Uri.parse(mProblem.getUrl()));
                    startActivity(webIntent);
                }
            });
        }
        public void BindProblem(PendingProblem problem){
            mProblem=problem;
            mTextView.setText(problem.getName());
        }

    }
    private class ProblemAdapter extends RecyclerView.Adapter<ProblemHolder>{
        private List<PendingProblem> mProblems;

        public ProblemAdapter(List<PendingProblem> problems) {
            mProblems = problems;
            for(PendingProblem problem:mProblems)
                Log.i(TAG,problem.getName());
        }

        @Override
        public ProblemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater
                    .inflate(R.layout.pending_list_item, parent, false);
            return new ProblemHolder(view);
        }

        @Override
        public void onBindViewHolder(ProblemHolder holder, int i) {
            PendingProblem prob = mProblems.get(i);
            holder.BindProblem(prob);
        }
        @Override
        public int getItemCount() {
            return mProblems.size();
        }
        public void setProblems(List <PendingProblem> problems){
            mProblems = problems;
        }
    }
    private void UpdateUI(boolean flag) throws JSONException {
        ProblemDatabase2 problemDb = ProblemDatabase2.get(getActivity());
        List<PendingProblem> problems = problemDb.getProblems("pending");
        if(flag) {
            Log.i(TAG,"update ui");
            problemDb.isSolved(problems, mSubmissions,mCodechefPoblems);
        }
        problems = problemDb.getProblems("pending");
        Log.i(TAG,"problems"+ problems.size());
        if (mAdapter == null) {
            mAdapter = new ProblemAdapter(problems);
            mRecyclerView.setAdapter(mAdapter);
        } else {
            Log.i(TAG,"set problems");
            mAdapter.setProblems(problems);
            mAdapter.notifyDataSetChanged();
        }
    }
    private class FetchItemsTask extends AsyncTask<Void,Void,JSONArray> {
        @Override
        protected JSONArray doInBackground(Void... params) {
            mSubmissions=new Fetcher().fetchItems();
            return mSubmissions;
        }
    }
    private class ScrapeItemsTask extends AsyncTask<String,Void,Set<String>> {
        @Override
        protected Set<String> doInBackground(String... params) {
            Set<String>problems = new HashSet<>();
            try {
                for(String parameter:params) {
                    Log.i(TAG, "scrape items task");
                    problems = new Scrapper().codechefSolved(parameter);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            ;
            return problems;
        }
        @Override
        protected void onPostExecute(Set<String> strings) {
            super.onPostExecute(strings);
            mCodechefPoblems = strings;
            Log.i(TAG,"on post execute");
        }
    }
}
