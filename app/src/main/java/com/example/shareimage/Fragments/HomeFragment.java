package com.example.shareimage.Fragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.shareimage.Adapters.PostAdapter;
import com.example.shareimage.HomeViewModel;
import com.example.shareimage.Models.PostModel;
import com.example.shareimage.Models.Repository;
import com.example.shareimage.Models.UserModel;
import com.example.shareimage.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    HomeViewModel viewData;
    private RecyclerView recyclerView;
    private PostAdapter postAdapter;
    //private ArrayList<PostModel> postList;
    LiveData<ArrayList<PostModel>> postListLD;

    FirebaseUser firebaseUser;




    Repository repository;


    ProgressBar progress_circular;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        viewData= ViewModelProviders.of(this).get(HomeViewModel.class);
        postListLD=viewData.getPostList();
        postListLD.observe(this, new Observer<ArrayList<PostModel>>() {
            @Override
            public void onChanged(ArrayList<PostModel> postModels) {
                updateDisplay();
            }
        });
    }

    void updateDisplay(){
        ArrayList<PostModel> postList = postListLD.getValue();
        if(postList!=null && postList.size()>0 && recyclerView!=null) {
            postAdapter = new PostAdapter(getContext(), postList);
            recyclerView.setAdapter(postAdapter);
            progress_circular.setVisibility(View.GONE);
        }else {
            progress_circular.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v=inflater.inflate(R.layout.fragment_home, container, false);

        firebaseUser=repository.instance.getAuthInstance().getCurrentUser();
        SharedPreferences.Editor editor = getContext().getSharedPreferences("PREFS", MODE_PRIVATE).edit();
        editor.putString("fragFrom","home");
        editor.apply();

        recyclerView = v.findViewById(R.id.home_recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(mLayoutManager);


        progress_circular = v.findViewById(R.id.progress_circular);

        updateDisplay();
        return v;
    }



}
