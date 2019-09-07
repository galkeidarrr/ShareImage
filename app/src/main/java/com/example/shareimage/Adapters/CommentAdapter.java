package com.example.shareimage.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.shareimage.Models.CommentModel;
import com.example.shareimage.Models.Repository;
import com.example.shareimage.Models.UserModel;
import com.example.shareimage.R;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;


public class CommentAdapter  extends RecyclerView.Adapter<CommentAdapter.ImageViewHolder>{

    private Context mContext;
    private ArrayList<CommentModel> mComment;
    private String postid;
    Repository repository;
    FirebaseUser firebaseUser;
    public CommentAdapter(Context context, ArrayList<CommentModel> comments, String postid){
        mContext = context;
        mComment = comments;
        this.postid = postid;
    }

    public void setmComment(ArrayList<CommentModel> mComment) {
        this.mComment = mComment;
    }

    @Override
    public int getItemCount() {
        return mComment.size();
    }//get comment count
    @NonNull
    @Override
    public CommentAdapter.ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {// initialize when adapter is created
        View view = LayoutInflater.from(mContext).inflate(R.layout.comment_row_item, parent, false);
        return new CommentAdapter.ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        final CommentModel comment = mComment.get(position);
        holder.comment.setText(comment.getComment());
        repository.instance.getUser(comment.getPublisher(), new Repository.GetUserListener() {
            @Override
            public void onComplete(UserModel userModel) {
                Glide.with(mContext).load(userModel.getImageUrl()).into(holder.image_profile);
                holder.username.setText(userModel.getUserName());
            }
        });
        //TODO: in the note down
/*
        holder.username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {//if click on user name go to the publisher profile

            }
        });
        holder.image_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {//if click on image profile go to the publisher profile
            }
        });
*/
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {//if long click -show delete dialog
                firebaseUser=repository.instance.getAuthInstance().getCurrentUser();
                if (comment.getPublisher().equals(firebaseUser.getUid())) {
                    //the delete dialog
                    AlertDialog alertDialog = new AlertDialog.Builder(mContext).create();
                    alertDialog.setTitle("Do you want to delete?");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "No",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    repository.instance.deleteComment(comment.getCommentId(), new Repository.DeleteCommentListener() {
                                        @Override
                                        public void onComplete(boolean success) {
                                            if(success) {
                                                Toast.makeText(mContext, "Deleted!", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
                }
                return true;
            }
        });


    }


    public class ImageViewHolder extends RecyclerView.ViewHolder {//ImageViewHolder class

        public ImageView image_profile;
        public TextView username, comment;

        public ImageViewHolder(View itemView) {
            super(itemView);

            image_profile = itemView.findViewById(R.id.image_profile);
            username = itemView.findViewById(R.id.username);
            comment = itemView.findViewById(R.id.comment);
        }
    }

}
