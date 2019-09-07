package com.example.shareimage.Fragments;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.shareimage.Adapters.PostAdapter;
import com.example.shareimage.Models.PostModel;
import com.example.shareimage.Models.Repository;
import com.example.shareimage.Models.UserModel;
import com.example.shareimage.R;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private PostAdapter postAdapter;
    private List<PostModel> postList;

    private RecyclerView recyclerView_story;
    //private StoryAdapter storyAdapter;
    //private List<Story> storyList;

    Repository repository;


    ProgressBar progress_circular;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v=inflater.inflate(R.layout.fragment_home, container, false);
        recyclerView = v.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(mLayoutManager);
        postList = new ArrayList<>();
        postAdapter = new PostAdapter(getContext(), postList);
        recyclerView.setAdapter(postAdapter);

        //recyclerView_story = view.findViewById(R.id.recycler_view_story);
        //recyclerView_story.setHasFixedSize(true);
        //LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(),
                //LinearLayoutManager.HORIZONTAL, false);
       // recyclerView_story.setLayoutManager(linearLayoutManager);
        //storyList = new ArrayList<>();
        //storyAdapter = new StoryAdapter(getContext(), storyList);
        //recyclerView_story.setAdapter(storyAdapter);

        progress_circular = v.findViewById(R.id.progress_circular);

        checkFollowing();



        return v;
    }
    private void checkFollowing(){

        //TODO: do its better
        //read post of followers posts
        repository.instance.getUser(repository.instance.getAuthInstance().getCurrentUser().getUid().toString(), new Repository.GetUserListener() {
            @Override
            public void onComplete(UserModel userModel) {
                if(userModel!=null){
                    repository.instance.getAllPost(new Repository.GetAllPostsListener() {
                        @Override
                        public void onComplete(ArrayList<PostModel> data) {
                            if(data!=null){
                                for (PostModel p:data) {
                                    for (String id : userModel.getFollowers()) {
                                        if (p.getPublisher().equals(id)) {
                                            postList.add(p);
                                        }
                                    }
                                }
                                postAdapter.setmPosts(postList);
                                recyclerView.setAdapter(postAdapter);

                            }
                            if (postList.isEmpty()){
                                progress_circular.setVisibility(View.VISIBLE);
                            }else {
                                progress_circular.setVisibility(View.GONE);
                            }
                        }
                    });
                }
            }
        });

    }


}
