package com.example.shareimage.Adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.app.NotificationCompat;
import androidx.navigation.NavAction;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.shareimage.Fragments.HomeFragmentDirections;
import com.example.shareimage.Models.PostModel;
import com.example.shareimage.Models.Repository;
import com.example.shareimage.Models.UserModel;
import com.example.shareimage.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ImageViewHolder>{
    private static final String TAG = "PostAdapter";
    private Context mContext;


    private List<PostModel> mPosts;

    Repository repository;
    private FirebaseUser firebaseUser;
    //Constractor with variables
    public PostAdapter(Context context, List<PostModel> posts){
        mContext = context;
        mPosts = posts;
    }
    public void setmPosts(List<PostModel> mPosts) {
        this.mPosts = mPosts;
    }

    @NonNull
    @Override
    public PostAdapter.ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {// initialize when adapter is created
        View view = LayoutInflater.from(mContext).inflate(R.layout.post_row_item, parent, false);
        return new PostAdapter.ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final PostAdapter.ImageViewHolder holder, final int position) {// where we will pass our data to our ViewHolder

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final PostModel post = mPosts.get(position);

        Glide.with(mContext).load(post.getPostImage())
                .apply(new RequestOptions().placeholder(R.drawable.person))
                .into(holder.post_image);

        if (post.getDescription().equals("")){//if not have description don't show
            holder.description.setVisibility(View.GONE);
        } else {
            holder.description.setVisibility(View.VISIBLE);
            holder.description.setText(post.getDescription());
        }

        publisherInfo(holder.image_profile, holder.username, holder.publisher, post.getPublisher());

        repository.instance.isLiked(post.getPostId(), holder.like, new Repository.GetisLikedListener() {
            @Override
            public void onComplete(boolean success) {
                Log.d(TAG, "onComplete: like button is change if liked");
            }
        });
        nrLikes(holder.likes,post.getPostId());


        //isSaved(post.getPostId(), holder.save);




        holder.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {//if click on like
                if (holder.like.getTag().equals("like")) {
                    repository.instance.addLike(post.getPostId(), new Repository.GetNewLikeListener() {
                        @Override
                        public void onComplete(boolean success) {
                            repository.instance.addLikeNotification(post.getPublisher(), post.getPostId(), new Repository.GetNotifiListener() {
                                @Override
                                public void onComplete(boolean success) {
                                    if (!success) {
                                        Log.d(TAG, "onComplete: failed to notify like ");
                                    }else {
                                        holder.like.setImageResource(R.drawable.ic_liked);
                                        holder.like.setTag("liked");
                                        nrLikes(holder.likes,post.getPostId());
                                    }
                                }
                            });
                        }
                    });
                }else {
                    repository.instance.deleteLike(post.getPostId(), new Repository.DeleteLikeListener() {
                        @Override
                        public void onComplete(boolean success) {
                            repository.instance.removeLikeNotification(post.getPublisher(), post.getPostId(), new Repository.GetNotifiListener() {
                                @Override
                                public void onComplete(boolean success) {
                                    if(!success){
                                        Log.d(TAG, "onComplete: failed to remove notify like ");
                                    }else {
                                        holder.like.setImageResource(R.drawable.ic_like);
                                        holder.like.setTag("like");
                                        nrLikes(holder.likes,post.getPostId());
                                    }
                                }
                            });
                        }
                    });

                }
            }
        });
/*
        holder.save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {//if click on save
                if (holder.save.getTag().equals("save")){
                    FirebaseDatabase.getInstance().getReference().child("Saves").child(firebaseUser.getUid())
                            .child(post.getPostid()).setValue(true);
                } else {
                    FirebaseDatabase.getInstance().getReference().child("Saves").child(firebaseUser.getUid())
                            .child(post.getPostid()).removeValue();
                }
            }
        });
*/
        holder.image_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {//if click on image profile go to publisher profile
                SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS", MODE_PRIVATE).edit();
                editor.putString("profileid", post.getPublisher());
                editor.apply();

                Navigation.findNavController(view)
                        .navigate(R.id.action_global_profileFragment);
            }
        });

        holder.username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {//if click on user name go to publisher profile
                SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS", MODE_PRIVATE).edit();
                editor.putString("profileid", post.getPublisher());
                editor.apply();

                Navigation.findNavController(view)
                        .navigate(R.id.action_global_profileFragment);
            }
        });

        holder.publisher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {//if click on publisher comment go to publisher profile
                SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS", MODE_PRIVATE).edit();
                editor.putString("profileid", post.getPublisher());
                editor.apply();

                Navigation.findNavController(view)
                        .navigate(R.id.action_global_profileFragment);
            }
        });


        //safeArgs
        HomeFragmentDirections.ActionHomeFragmentToCommentsFragment action=HomeFragmentDirections.actionHomeFragmentToCommentsFragment(post.getPostId(),post.getPublisher());

        //if click on comment go to comment activity- for left comment
        holder.comment.setOnClickListener(Navigation.createNavigateOnClickListener(action));
        //if click on comments go to comments activity -to see all the comments
        holder.comments.setOnClickListener(Navigation.createNavigateOnClickListener(action));

        holder.post_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {//if click on post image go to post detail fragment to see the post
                SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS", MODE_PRIVATE).edit();
                editor.putString("postid", post.getPostId());
                editor.apply();

                Navigation.findNavController(view)
                        .navigate(R.id.action_homeFragment_to_postFragment);
            }
        });
/*
        holder.likes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {//if click on likes go to followers activity to see all the followers that liked
                Intent intent = new Intent(mContext, FollowersActivity.class);
                intent.putExtra("id", post.getPostid());
                intent.putExtra("title", "likes");
                mContext.startActivity(intent);
            }
        });


        holder.more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {//if click more go to manu to chose if edit or remove
                PopupMenu popupMenu = new PopupMenu(mContext, view);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()){
                            case R.id.edit://edit and go to profile
                                editPost(post.getPostid());
                                ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                        new ProfileFragment()).commit();
                                return true;
                            case R.id.delete://delete and go to profile
                                final String id = post.getPostid();
                                FirebaseDatabase.getInstance().getReference("Posts")
                                        .child(post.getPostid()).removeValue()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()){
                                                    deleteNotifications(id, firebaseUser.getUid());
                                                }
                                            }
                                        });
                                ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                        new ProfileFragment()).commit();
                                return true;
                            default:
                                return false;
                        }
                    }
                });
                popupMenu.inflate(R.menu.post_menu);
                if (!post.getPublisher().equals(firebaseUser.getUid())){
                    popupMenu.getMenu().findItem(R.id.edit).setVisible(false);
                    popupMenu.getMenu().findItem(R.id.delete).setVisible(false);
                }
                popupMenu.show();
            }
        });
*/
    }

    @Override
    public int getItemCount() {
        return mPosts.size();
    }//get the posts count

    public class ImageViewHolder extends RecyclerView.ViewHolder {//ImageViewHolder class

        public ImageView image_profile, post_image, like, comment, save, more;
        public TextView username, likes, publisher, description, comments;

        public ImageViewHolder(View itemView) {
            super(itemView);

            image_profile = itemView.findViewById(R.id.prow_image_profile);
            username = itemView.findViewById(R.id.prow_username);
            post_image = itemView.findViewById(R.id.prow_post_image);
            like = itemView.findViewById(R.id.prow_like);
            comment = itemView.findViewById(R.id.prow_comment);
            save = itemView.findViewById(R.id.prow_save);
            likes = itemView.findViewById(R.id.prow_likes);
            publisher = itemView.findViewById(R.id.prow_publisher);
            description = itemView.findViewById(R.id.prow_description);
            comments = itemView.findViewById(R.id.prow_comments);
            more = itemView.findViewById(R.id.prow_more);
        }
    }



    //for showing who many do likes
    private void nrLikes(final TextView likes, String postId){
        repository.instance.getPost(postId, new Repository.GetPostListener() {
            @Override
            public void onComplete(PostModel postModel) {
                if(postModel!=null){
                    likes.setText(postModel.likes.size()+" likes");
                }
            }
        });
    }


    //get the details of the publisher
    private void publisherInfo(final ImageView image_profile, final TextView username, final TextView publisher, final String userid){
        repository.instance.getUser(userid, new Repository.GetUserListener() {
            @Override
            public void onComplete(UserModel userModel) {
                if(userModel!=null){
                    UserModel user=userModel;
                    Glide.with(mContext).load(user.getImageUrl()).into(image_profile);
                    username.setText(user.getUserName());
                    publisher.setText(user.getUserName());
                }
            }
        });

    }


   /*
    //for setting the button of saved
    private void isSaved(final String postid, final ImageView imageView){

        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Saves").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {//if something change
                if (dataSnapshot.child(postid).exists()){//if save
                    imageView.setImageResource(R.drawable.ic_save_black);
                    imageView.setTag("saved");
                } else{//if unsave
                    imageView.setImageResource(R.drawable.ic_savee_black);
                    imageView.setTag("save");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    //for more if the current user want to edit the post
    private void editPost(final String postid){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
        alertDialog.setTitle("Edit Post");

        final EditText editText = new EditText(mContext);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        editText.setLayoutParams(lp);
        alertDialog.setView(editText);

        getText(postid, editText);

        alertDialog.setPositiveButton("Edit",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("description", editText.getText().toString());

                        FirebaseDatabase.getInstance().getReference("Posts")
                                .child(postid).updateChildren(hashMap);
                    }
                });
        alertDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
        alertDialog.show();
    }

    //fer edit post get the text that already there
    private void getText(String postid, final EditText editText){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts")
                .child(postid);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {//if something change
                editText.setText(dataSnapshot.getValue(Post.class).getDescription());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
*/
}