package com.example.shareimage.Fragments;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.shareimage.Adapters.MyPhotosAdapter;
import com.example.shareimage.Models.PostModel;
import com.example.shareimage.Models.Repository;
import com.example.shareimage.Models.UserModel;
import com.example.shareimage.R;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {
    private static final String TAG = "ProfileFragment";
    ImageView image_profile, options;
    TextView posts, followers, following, fullname, bio, username;
    Button edit_profile;

    FirebaseUser firebaseUser;
    Repository repository;
    private List<String> mySaves;


    String profileid;

    private RecyclerView recyclerView;
    private MyPhotosAdapter myPhotosAdapter;
    private ArrayList<PostModel> postList;

    private RecyclerView recyclerView_saves;
    private MyPhotosAdapter myPhotosAdapter_saves;
    private ArrayList<PostModel> postList_saves;

    ImageButton my_photos, saved_photos;

    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_profile, container, false);
        SharedPreferences prefs = getContext().getSharedPreferences("PREFS", MODE_PRIVATE);
        profileid = prefs.getString("profileid", "none");

        //save the variables
        image_profile = view.findViewById(R.id.profile_image_profile);
        posts = view.findViewById(R.id.profile_posts);
        followers = view.findViewById(R.id.profile_followers);
        following = view.findViewById(R.id.profile_following);
        fullname = view.findViewById(R.id.profile_fullname);
        bio = view.findViewById(R.id.profile_bio);
        edit_profile = view.findViewById(R.id.profile_edit_profile);
        username = view.findViewById(R.id.profile_username);
        my_photos = view.findViewById(R.id.profile_my_photos);
        saved_photos = view.findViewById(R.id.profile_saved_photos);
        options = view.findViewById(R.id.profile_options);


        recyclerView = view.findViewById(R.id.profile_recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new GridLayoutManager(getContext(), 3);
        recyclerView.setLayoutManager(mLayoutManager);
        postList = new ArrayList<>();
        myPhotosAdapter = new MyPhotosAdapter(getContext(), postList);
        recyclerView.setAdapter(myPhotosAdapter);

        recyclerView_saves = view.findViewById(R.id.profile_recycler_view_save);
        recyclerView_saves.setHasFixedSize(true);
        LinearLayoutManager mLayoutManagers = new GridLayoutManager(getContext(), 3);
        recyclerView_saves.setLayoutManager(mLayoutManagers);
        postList_saves = new ArrayList<>();
        myPhotosAdapter_saves = new MyPhotosAdapter(getContext(), postList_saves);
        recyclerView_saves.setAdapter(myPhotosAdapter_saves);

        recyclerView.setVisibility(View.VISIBLE);
        recyclerView_saves.setVisibility(View.GONE);

        firebaseUser=repository.instance.getAuthInstance().getCurrentUser();

        mySaves=new ArrayList<>();
        repository.instance.getUser(profileid, new Repository.GetUserListener() {
            @Override
            public void onComplete(UserModel userModel) {
                if(userModel!=null){
                    Glide.with(getContext()).load(userModel.getImageUrl()).into(image_profile);
                    username.setText(userModel.getUserName());
                    fullname.setText(userModel.getFullName());
                    bio.setText(userModel.getBio());
                    //TODO: chack follows and followers true or need change
                    followers.setText(userModel.followers.size());
                    following.setText(userModel.follows.size());
                }
            }
        });
        repository.instance.getAllPost(new Repository.GetAllPostsListener() {
            @Override
            public void onComplete(ArrayList<PostModel> data) {
                int i=0;
                postList.clear();
                if(data!=null) {
                    for (PostModel p : data) {
                        if (p.getPublisher().equals(profileid)) {
                            postList.add(p);
                            i++;
                        }
                    }
                }
                posts.setText(""+i);
                Collections.reverse(postList);
                myPhotosAdapter.notifyDataSetChanged();
            }
        });

        myFotos();
        mySaves();

        if (profileid.equals(firebaseUser.getUid())){//if the current user want to edit profile
            edit_profile.setText("Edit Profile");
        } else {//only the current can edit
            edit_profile.setText("follow");
            repository.instance.isFollowing(profileid, edit_profile, new Repository.GetisFollowListener() {
                @Override
                public void onComplete(boolean success) {
                    Log.d(TAG, "onComplete: following button is change if follow");
                }
            });
            saved_photos.setVisibility(View.GONE);
        }

        final UserModel[] userModel1 = new UserModel[2];

        edit_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {//if click on edit profile
                String btn = edit_profile.getText().toString();

                if (btn.equals("Edit Profile")){//if click on edit go to edit
                    Navigation.findNavController(view)
                            .navigate(R.id.action_profileFragment_to_editProfileFragment);

                } else if (btn.equals("follow")){//if click on follow now the current user follow on other
                    repository.instance.getUser(profileid, new Repository.GetUserListener() {
                        @Override
                        public void onComplete(UserModel userModel) {
                            if (userModel != null) {
                                userModel1[0] = userModel;
                                repository.instance.getUser(firebaseUser.getUid(), new Repository.GetUserListener() {
                                    @Override
                                    public void onComplete(UserModel userModel) {
                                        if (userModel != null) {
                                            userModel1[1] = userModel;
                                            Log.d(TAG, "onClick: " + userModel1[0]);
                                            repository.instance.addFollow(userModel1[0], userModel1[1], new Repository.GetNewFollowListener() {
                                                @Override
                                                public void onComplete(boolean success) {
                                                    repository.instance.addFollowNotification(userModel1[0].getId(), new Repository.GetNotifiListener() {
                                                        @Override
                                                        public void onComplete(boolean success) {
                                                            if (!success) {
                                                                Log.d(TAG, "onComplete: failed to add follow");
                                                            }
                                                        }
                                                    });
                                                }
                                            });
                                        }
                                    }
                                });
                            }
                        }
                    });
                } else if (btn.equals("following")){//if click on following now the current user unfollow on other
                    repository.instance.getUser(profileid, new Repository.GetUserListener() {
                        @Override
                        public void onComplete(UserModel userModel) {
                            if (userModel != null) {
                                userModel1[0] = userModel;
                                repository.instance.getUser(firebaseUser.getUid(), new Repository.GetUserListener() {
                                    @Override
                                    public void onComplete(UserModel userModel) {
                                        if (userModel != null) {
                                            userModel1[1] = userModel;
                                            Log.d(TAG, "onClick: " + userModel1[0]);
                                            repository.instance.deleteFollow(userModel1[0], userModel1[1], new Repository.DeleteFollowListener() {
                                                @Override
                                                public void onComplete(boolean success) {
                                                    repository.instance.removeFollowNotification(userModel1[0].getId(), new Repository.GetNotifiListener() {
                                                        @Override
                                                        public void onComplete(boolean success) {
                                                            if (!success) {
                                                                Log.d(TAG, "onComplete: failed to remove notify follow");
                                                            }
                                                        }
                                                    });
                                                }
                                            });
                                        }
                                    }
                                });
                            }
                        }
                    });
                }
            }
        });

        //if click on options go to options activity
        options.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_profileFragment_to_optionsFragment));

        //if click on photos can see the post that published
        my_photos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {//if click on photos can see the post that published
                recyclerView.setVisibility(View.VISIBLE);
                recyclerView_saves.setVisibility(View.GONE);
            }
        });

        //if click on saved can see the photos that saved
        saved_photos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {//if click on saved can see the photos that saved
                recyclerView.setVisibility(View.GONE);
                recyclerView_saves.setVisibility(View.VISIBLE);
            }
        });

        //safeArgs
        ProfileFragmentDirections.ActionProfileFragmentToFollowersFragment action=ProfileFragmentDirections.actionProfileFragmentToFollowersFragment(profileid,"followers");
        //if click on followers can see how follow-in follow activity
        followers.setOnClickListener(Navigation.createNavigateOnClickListener(action));

        //safeArgs2
        ProfileFragmentDirections.ActionProfileFragmentToFollowersFragment action2=ProfileFragmentDirections.actionProfileFragmentToFollowersFragment(profileid,"following");
        //if click on following can see the users that follow by the current user-in follow activity
        following.setOnClickListener(Navigation.createNavigateOnClickListener(action2));


        return view;
    }






    //get the followers and following count to show in profile
    private void getFollowers(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Follow").child(profileid).child("followers");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                followers.setText(""+dataSnapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("Follow").child(profileid).child("following");
        reference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                following.setText(""+dataSnapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    //get the post count of current user to show in profile
    private void getNrPosts(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {//if something change
                int i = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Post post = snapshot.getValue(Post.class);
                    if (post.getPublisher().equals(profileid)){
                        i++;
                    }
                }
                posts.setText(""+i);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    //get the images that the current user posts to show in photos
    private void myFotos(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {//if something change
                postList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){//get all posts
                    Post post = snapshot.getValue(Post.class);
                    if (post.getPublisher().equals(profileid)){
                        postList.add(post);
                    }
                }
                Collections.reverse(postList);
                myFotosAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    //get the images that the current user save to show in saves
    private void mySaves(){
        mySaves = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Saves").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {//if something change
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){//get all keys posts that saved
                    mySaves.add(snapshot.getKey());
                }
                readSaves();//function that read all the saves
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    //for showing all the save need to read them first
    private void readSaves(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {//if something change
                postList_saves.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){//get all the posts that saved by the current user
                    Post post = snapshot.getValue(Post.class);

                    for (String id : mySaves) {//get from the keys the posts
                        if (post.getPostid().equals(id)) {
                            postList_saves.add(post);
                        }
                    }
                }
                myFotosAdapter_saves.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}

