package com.example.shareimage.Fragments;


import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.shareimage.Adapters.UserAdapter;
import com.example.shareimage.Models.PostModel;
import com.example.shareimage.Models.Repository;
import com.example.shareimage.Models.UserModel;
import com.example.shareimage.R;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class FollowersFragment extends Fragment {
    private static final String TAG = "FollowersFragment";
    String id;
    String title;
    Repository repository;
    FirebaseUser firebaseUser;

    private ArrayList<String> idList;

    RecyclerView recyclerView;
    UserAdapter userAdapter;
    ArrayList<UserModel> userList;

    public FollowersFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_followers, container, false);

        id= FollowersFragmentArgs.fromBundle(getArguments()).getId();
        title=FollowersFragmentArgs.fromBundle(getArguments()).getTitle();

        Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle(title);

        //save the variables
        recyclerView = view.findViewById(R.id.followers_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        userList = new ArrayList<>();
        userAdapter = new UserAdapter(getContext(), userList, false);
        recyclerView.setAdapter(userAdapter);
        firebaseUser=repository.instance.getAuthInstance().getCurrentUser();
        idList = new ArrayList<>();

        //get information about the followers
        switch (title) {
            case "likes":
                repository.instance.getPost(id, new Repository.GetPostListener() {
                    @Override
                    public void onComplete(PostModel postModel) {
                        if (postModel != null) {
                            idList.clear();
                            idList = postModel.likes;
                            repository.instance.getAllUsers(new Repository.GetAllUsersListener() {
                                @Override
                                public void onComplete(ArrayList<UserModel> data) {
                                    if (data != null) {
                                        userList.clear();
                                        for (UserModel u : data) {
                                            for (String id : idList) {
                                                if (u.getId().equals(id)) {
                                                    userList.add(u);
                                                }
                                            }
                                        }
                                        userAdapter = new UserAdapter(getContext(), userList, false);
                                        recyclerView.setAdapter(userAdapter);
                                    }
                                }
                            });
                        }
                    }
                });
                break;
            case "following":
                repository.instance.getUser(id, new Repository.GetUserListener() {
                    @Override
                    public void onComplete(UserModel userModel) {
                        if (userModel != null) {
                            idList.clear();
                            idList = userModel.followers;
                            repository.instance.getAllUsers(new Repository.GetAllUsersListener() {
                                @Override
                                public void onComplete(ArrayList<UserModel> data) {
                                    if (data != null) {
                                        userList.clear();
                                        for (UserModel u : data) {
                                            for (String id : idList) {
                                                if (u.getId().equals(id)) {
                                                    userList.add(u);
                                                }
                                            }
                                        }
                                        userAdapter = new UserAdapter(getContext(), userList, false);
                                        recyclerView.setAdapter(userAdapter);
                                    }
                                }
                            });
                        }
                    }
                });
                break;
            case "followers":
                repository.instance.getUser(id, new Repository.GetUserListener() {
                    @Override
                    public void onComplete(UserModel userModel) {
                        if (userModel != null) {
                            idList.clear();
                            idList = userModel.follows;
                            repository.instance.getAllUsers(new Repository.GetAllUsersListener() {
                                @Override
                                public void onComplete(ArrayList<UserModel> data) {
                                    if (data != null) {
                                        userList.clear();
                                        for (UserModel u : data) {
                                            for (String id : idList) {
                                                if (u.getId().equals(id)) {
                                                    userList.add(u);
                                                }
                                            }
                                        }
                                        userAdapter = new UserAdapter(getContext(), userList, false);
                                        recyclerView.setAdapter(userAdapter);
                                    }
                                }
                            });
                        }
                    }
                });
                break;
        }


        return view;
    }

}
