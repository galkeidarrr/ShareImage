package com.example.shareimage.Fragments;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.example.shareimage.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {

    ImageView image_profile, options;
    TextView posts, followers, following, fullname, bio, username;
    Button edit_profile;

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

        userInfo();
        getFollowers();
        getNrPosts();
        myFotos();
        mySaves();

        if (profileid.equals(firebaseUser.getUid())){//if the current user want to edit profile
            edit_profile.setText("Edit Profile");
        } else {//only the current can edit
            checkFollow();
            saved_fotos.setVisibility(View.GONE);
        }

        edit_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {//if click on edit profile
                String btn = edit_profile.getText().toString();

                if (btn.equals("Edit Profile")){//if click on edit go to edit
                    Intent intent=new Intent(getContext(), EditProfileActivity.class);
                    startActivity(intent);

                } else if (btn.equals("follow")){//if click on follow now the current user follow on other

                    FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
                            .child("following").child(profileid).setValue(true);
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(profileid)
                            .child("followers").child(firebaseUser.getUid()).setValue(true);
                    addNotification();
                } else if (btn.equals("following")){//if click on following now the current user unfollow on other

                    FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
                            .child("following").child(profileid).removeValue();
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(profileid)
                            .child("followers").child(firebaseUser.getUid()).removeValue();

                }
            }
        });

        options.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {//if click on options go to options activity
                startActivity(new Intent(getContext(), OptionsActivity.class));
            }
        });

        my_photos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {//if click on photos can see the post that published
                recyclerView.setVisibility(View.VISIBLE);
                recyclerView_saves.setVisibility(View.GONE);
            }
        });

        saved_photos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {//if click on saved can see the photos that saved
                recyclerView.setVisibility(View.GONE);
                recyclerView_saves.setVisibility(View.VISIBLE);
            }
        });


        followers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {//if click on followers can see how follow-in follow activity
                Intent intent = new Intent(getContext(), FollowersActivity.class);
                intent.putExtra("id", profileid);
                intent.putExtra("title", "followers");
                startActivity(intent);
            }
        });

        following.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {//if click on following can see the users that follow by the current user-in follow activity
                Intent intent = new Intent(getContext(), FollowersActivity.class);
                intent.putExtra("id", profileid);
                intent.putExtra("title", "following");
                startActivity(intent);
            }
        });

        return view;
    }

    //add notification about following
    private void addNotification(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notifications").child(profileid);

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("userid", firebaseUser.getUid());
        hashMap.put("text", "started following you");
        hashMap.put("postid", "");
        hashMap.put("ispost", false);

        reference.push().setValue(hashMap);
    }

    //get the user info for showing in profile
    private void userInfo(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(profileid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {//if something change
                if (getContext() == null){
                    return;
                }
                User user = dataSnapshot.getValue(User.class);

                Glide.with(getContext()).load(user.getImageurl()).into(image_profile);
                username.setText(user.getUsername());
                fullname.setText(user.getFullname());
                bio.setText(user.getBio());

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    //check if following of other users
    private void checkFollow(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Follow").child(firebaseUser.getUid()).child("following");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {//if something change
                if (dataSnapshot.child(profileid).exists()){
                    edit_profile.setText("following");
                } else{
                    edit_profile.setText("follow");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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

