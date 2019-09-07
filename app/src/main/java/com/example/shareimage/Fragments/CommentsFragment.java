package com.example.shareimage.Fragments;


import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.shareimage.Adapters.CommentAdapter;
import com.example.shareimage.Models.CommentModel;
import com.example.shareimage.Models.Repository;
import com.example.shareimage.Models.UserModel;
import com.example.shareimage.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class CommentsFragment extends Fragment {
    private static final String TAG = "CommentsFragment";
    private RecyclerView recyclerView;
    private CommentAdapter commentAdapter;
    private ArrayList<CommentModel> commentList;

    Repository repository;
    ImageView closeBtn;
    EditText addcomment;
    ImageView image_profile;
    TextView post;

    String postid;
    String publisherid;
    String commentId;
    String commentLefted;
    FirebaseUser firebaseUser;

    public CommentsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_comments, container, false);
        Toolbar toolbar = view.findViewById(R.id.comment_toolbar);
        toolbar.setTitle("Comments");


        postid = CommentsFragmentArgs.fromBundle(getArguments()).getPostId();
        publisherid = CommentsFragmentArgs.fromBundle(getArguments()).getPublisherId();

        //save the variables
        recyclerView = view.findViewById(R.id.comment_recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        commentList = new ArrayList<>();
        commentAdapter = new CommentAdapter(getContext(), commentList, postid);
        recyclerView.setAdapter(commentAdapter);



        post = view.findViewById(R.id.comment_post);
        addcomment = view.findViewById(R.id.comment_add_comment);
        image_profile = view.findViewById(R.id.comment_image_profile);
        closeBtn=view.findViewById(R.id.comment_closeBtn);

        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(view)
                        .popBackStack(R.id.homeFragment,false);
            }
        });

        //add new comment
        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: share");
                commentLefted=addcomment.getText().toString();
                if (commentLefted.equals("")){//if the message is empty
                    Log.d(TAG, "onClick: the user try post empty message");
                    Toast.makeText(getActivity(), "You can't send empty message", Toast.LENGTH_SHORT).show();
                }else {
                    repository.instance.addComment(commentLefted, firebaseUser.getUid(), new Repository.AddCommentListener() {

                        @Override
                        public void onComplete(String commentid) {
                            if(commentid!=null) {
                                commentId = commentid;
                                Toast.makeText(getActivity(), "posted!", Toast.LENGTH_SHORT).show();
                                //add new notification to the user whose post about the comment
                                repository.instance.addCommentNotification(commentId, publisherid, commentLefted, postid, new Repository.GetNotifiListener() {
                                    @Override
                                    public void onComplete(boolean success) {
                                        if (!success) {
                                            Log.d(TAG, "onComplete: failed to notify about comment");
                                        }
                                    }
                                });
                            }
                        }
                    });
                }
                addcomment.setText("");
            }
        });
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        //get the current user image that left comment
        repository.instance.getUser(firebaseUser.getUid(), new Repository.GetUserListener() {
            @Override
            public void onComplete(UserModel userModel) {
                if(userModel!=null){
                    Glide.with(getContext().getApplicationContext()).load(userModel.getImageUrl()).into(image_profile);
                }
            }
        });

        //get all comments from specific post
        repository.instance.getAllComments(new Repository.GetAllCommentsListener() {
            @Override
            public void onComplete(ArrayList<CommentModel> data) {
                if(data!=null){
                    Log.d(TAG, "onComplete: get all comments");
                    commentList=data;
                    commentAdapter.setmComment(commentList);
                    recyclerView.setAdapter(commentAdapter);
                }
            }
        });

        return view;
    }


}
