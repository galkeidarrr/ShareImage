package com.example.shareimage.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shareimage.Models.CommentModel;
import com.example.shareimage.R;

import java.util.ArrayList;
import java.util.List;

public class CommentAdapter  extends RecyclerView.Adapter<CommentAdapter.ImageViewHolder>{

    private Context mContext;
    private ArrayList<CommentModel> mComment;
    private String postid;

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
