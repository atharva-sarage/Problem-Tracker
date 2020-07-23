package com.example.cpapp;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
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
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;

import java.util.List;
import java.util.UUID;

public class FavouriteFragment extends Fragment {
    private static final String TAG = "Favourite Fragment";
    private static final String NOTES="notes";
    private static final String CONFIRMDELETESTRING = "confirmdelete";
    private static final int CONFIRMDELETE=2;
    private static final int SETNOTES=1;
    private static final int FAVOURITE_MENU_ID=1;
    private RecyclerView mRecyclerView;
    private ProblemAdapter mAdapter;
    public static Fragment newInstance(){
        return new FavouriteFragment();
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        Log.i(TAG,"on create");
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        Log.i(TAG,"on create view");
        View view = inflater.inflate(R.layout.favourite_list,container,false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.favourite_list_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        final BottomNavigationView bottomNavigationView = (BottomNavigationView) view.findViewById(R.id.bottom_navigation);
        initBottomNavigationMenuBar(FAVOURITE_MENU_ID,bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                removeColor(bottomNavigationView);
                item.setChecked(true);
                Log.i(TAG, String.valueOf(item));
                switch (item.getItemId()) {
                    case R.id.action_pending:
                        FragmentManager manager = getFragmentManager();
                        Fragment fragment = new PendingListFragment();
                        manager.beginTransaction()
                                .replace(R.id.fragment_container,fragment)
                                .commit();
                        break;
                    case R.id.action_favorites:
                        break;
                    case R.id.action_solved:
                        FragmentManager manager1 = getFragmentManager();
                        Fragment fragment1 = new SolvedFragment();
                        manager1.beginTransaction()
                                .replace(R.id.fragment_container,fragment1)
                                .commit();
                }
                return true;
            }
        });
        UpdateUI();
        Log.i(TAG,"favourite list problems");
        return  view;
    }

    private void initBottomNavigationMenuBar(int index, BottomNavigationView bottomNavigationView) {
        removeColor(bottomNavigationView);
        bottomNavigationView.getMenu().getItem(index).setChecked(true);
    }

    private void removeColor(BottomNavigationView view) {
        for (int i = 0; i < view.getMenu().size(); i++) {
            MenuItem item = view.getMenu().getItem(i);
            item.setChecked(false);
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
           case R.id.menu_item_new_problem:
               PendingProblem problem = new PendingProblem();
               ProblemDatabase2.get(getActivity()).addProblem(problem);
               Intent intent = AddProblemActivity
                       .newIntent(getActivity(), problem.getUid().toString(),"favourite");
               startActivity(intent);
               return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_list_menu, menu);
    }
    private class ProblemHolder extends RecyclerView.ViewHolder{
        private Button mOpenButton;
        private Button mNotes;
        private TextView mTextView;
        private TextView mIdView;
        private PendingProblem mProblem;
        private ImageView mDeleteButton;

        public ProblemHolder(View itemView){
            super(itemView);
            mNotes = (Button) itemView.findViewById(R.id.open_notes);
            mNotes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FragmentManager manager = getFragmentManager();
                    NotesDialog dialog = NotesDialog
                            .newInstance(mProblem.getNote().toString());
                    dialog.setTargetFragment(FavouriteFragment.this,SETNOTES);
                    dialog.show(manager,NOTES);
                }
            });
            mTextView = (TextView) itemView.findViewById(R.id.problem_name);
            mTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = AddProblemActivity
                            .newIntent(getActivity(), ""+mProblem.getUid(),"favourite"); // how to get id here
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

            mDeleteButton = (ImageView) itemView.findViewById(R.id.delete_problem);
            mDeleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FragmentManager manager = getFragmentManager();
                    DeleteConfirmationDialog dialog = DeleteConfirmationDialog.newInstance(mProblem.getUid().toString());
                    dialog.setTargetFragment(FavouriteFragment.this,CONFIRMDELETE);
                    dialog.show(manager,CONFIRMDELETESTRING);
                    //DeleteProblemAndUpdateUI(mProblem);
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
                    .inflate(R.layout.favourite_list_item, parent, false);
            return new ProblemHolder(view);
        }

        @Override
        public void onBindViewHolder(FavouriteFragment.ProblemHolder holder, int i) {
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == CONFIRMDELETE){
            String res = data.getStringExtra(DeleteConfirmationDialog.EXTRA_RESULT);
            String problemId = data.getStringExtra(DeleteConfirmationDialog.PROBLEM);
            if(res.equals("true")){
                ProblemDatabase2 problemDb = ProblemDatabase2.get(getActivity());
                Log.i(TAG,"problemId: "+problemId);
                PendingProblem deleteProblem = problemDb.getProblem(problemId);
                DeleteProblemAndUpdateUI(deleteProblem);
            }
        }
    }
    private void UpdateUI(){
        ProblemDatabase2 problemDb = ProblemDatabase2.get(getActivity());
        List<PendingProblem> problems = problemDb.getProblems("favourite");
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
    private void DeleteProblemAndUpdateUI(PendingProblem deleteProblem){
        ProblemDatabase2 problemDb = ProblemDatabase2.get(getActivity());
        problemDb.deleteProblem(deleteProblem);
        UpdateUI();
    }
}
