package com.example.shareimage.Adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.shareimage.Models.NotificationModel;
import com.example.shareimage.Models.PostModel;
import com.example.shareimage.Models.Repository;
import com.example.shareimage.Models.UserModel;
import com.example.shareimage.R;

import java.util.ArrayList;


import static android.content.Context.MODE_PRIVATE;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ImageViewHolder>{

    private Context mContext;
    private ArrayList<NotificationModel> mNotification;
    Repository repository;

    //Constractor with variables
    public NotificationAdapter(Context context, ArrayList<NotificationModel> notification){
        mContext = context;
        mNotification = notification;
    }


    @NonNull
    @Override
    public NotificationAdapter.ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {// initialize when adapter is created
        View view = LayoutInflater.from(mContext).inflate(R.layout.notification_row_item, parent, false);
        return new NotificationAdapter.ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final NotificationAdapter.ImageViewHolder holder, final int position) {// where we will pass our data to our ViewHolder

        final NotificationModel notification = mNotification.get(position);

        holder.text.setText(notification.getText());

        //get the user info for sending notification to the publisher
        repository.instance.getUser(notification.getUserIdFrom(), new Repository.GetUserListener() {
            @Override
            public void onComplete(UserModel userModel) {
                if(userModel!=null){
                    Glide.with(mContext).load(userModel.getImageUrl()).into(holder.image_profile);
                    holder.username.setText(userModel.getUserName());
                }
            }
        });


        if (notification.getIsPost()) {
            holder.post_image.setVisibility(View.VISIBLE);
            repository.instance.getPost(notification.getPostId(), new Repository.GetPostListener() {
                @Override
                public void onComplete(PostModel postModel) {
                    if(postModel!=null){
                        Glide.with(mContext).load(postModel.getPostImage()).into(holder.post_image);
                    }
                }
            });
        } else {
            holder.post_image.setVisibility(View.GONE);
        }
        holder.image_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS", MODE_PRIVATE).edit();
                editor.putString("profileid", notification.getUserIdFrom());
                editor.putString("other", "true");
                editor.apply();

                Navigation.findNavController(v)
                        .navigate(R.id.action_global_profileFragment);
            }
        });

        holder.username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS", MODE_PRIVATE).edit();
                editor.putString("profileid", notification.getUserIdFrom());
                editor.putString("other", "true");
                editor.apply();

                Navigation.findNavController(v)
                        .navigate(R.id.action_global_profileFragment);
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {//if click on item view
                if (notification.getIsPost()) {//if it is post go to detail post
                    SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS", MODE_PRIVATE).edit();
                    editor.putString("postid", notification.getPostId());
                    editor.apply();

                    Navigation.findNavController(view)
                            .navigate(R.id.action_notificationFragment_to_postFragment);
                } else {//else go to the publisher profile
                    SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS", MODE_PRIVATE).edit();
                    editor.putString("profileid", notification.getUserIdFrom());
                    editor.putString("other", "true");
                    editor.apply();

                    Navigation.findNavController(view)
                            .navigate(R.id.action_global_profileFragment);
                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return mNotification.size();
    }//get count of notification

    public class ImageViewHolder extends RecyclerView.ViewHolder {//ImageViewHolder class

        public ImageView image_profile, post_image;
        public TextView username, text;

        public ImageViewHolder(View itemView) {
            super(itemView);

            image_profile = itemView.findViewById(R.id.image_profile);
            post_image = itemView.findViewById(R.id.post_image);
            username = itemView.findViewById(R.id.username);
            text = itemView.findViewById(R.id.comment);
        }
    }



}
