package com.example.shareimage.Adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.shareimage.Models.PostModel;
import com.example.shareimage.R;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;


public class MyPhotosAdapter extends RecyclerView.Adapter<MyPhotosAdapter.ImageViewHolder> {

    private Context mContext;
    private ArrayList<PostModel> mPosts;
    //Constractor with variables
    public MyPhotosAdapter(Context context, ArrayList<PostModel> posts){
        mContext = context;
        mPosts = posts;
    }

    @NonNull
    @Override
    public MyPhotosAdapter.ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {// initialize when adapter is created
        View view = LayoutInflater.from(mContext).inflate(R.layout.photos_item, parent, false);
        return new MyPhotosAdapter.ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyPhotosAdapter.ImageViewHolder holder, final int position) {// where we will pass our data to our ViewHolder

        final PostModel post = mPosts.get(position);

        Glide.with(mContext).load(post.getPostImage()).into(holder.post_image);

        holder.post_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {//if click on post image go to the post details
                SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS", MODE_PRIVATE).edit();
                editor.putString("postid", post.getPostId());
                editor.apply();

                Navigation.findNavController(view)
                        .navigate(R.id.action_profileFragment_to_postFragment);

            }
        });

    }

    @Override
    public int getItemCount() {
        return mPosts.size();
    }//get count of my posts

    public class ImageViewHolder extends RecyclerView.ViewHolder {//ImageViewHolder

        public ImageView post_image;


        public ImageViewHolder(View itemView) {
            super(itemView);

            post_image = itemView.findViewById(R.id.post_image);

        }
    }

}
