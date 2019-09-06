package com.example.shareimage.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.shareimage.Models.PostModel;
import com.example.shareimage.Models.Repository;
import com.example.shareimage.Models.UserModel;
import com.example.shareimage.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.HashMap;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ImageViewHolder>{
    private Context mContext;
    private List<PostModel> mPosts;

    Repository repository;
    private FirebaseUser firebaseUser;
    //Constractor with variables
    public PostAdapter(Context context, List<PostModel> posts){
        mContext = context;
        mPosts = posts;
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
        //isLiked(post.getPostId(), holder.like);
        //isSaved(post.getPostId(), holder.save);
        //nrLikes(holder.likes, post.getPostId());
        //getCommetns(post.getPostId(), holder.comments);

/*
        holder.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {//if click on like
                if (holder.like.getTag().equals("like")) {
                    FirebaseDatabase.getInstance().getReference().child("Likes").child(post.getPostid())
                            .child(firebaseUser.getUid()).setValue(true);
                    addNotification(post.getPublisher(), post.getPostid());
                } else {
                    FirebaseDatabase.getInstance().getReference().child("Likes").child(post.getPostid())
                            .child(firebaseUser.getUid()).removeValue();
                }
            }
        });

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

        holder.image_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {//if click on image profile go to publisher profile
                SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS", MODE_PRIVATE).edit();
                editor.putString("profileid", post.getPublisher());
                editor.apply();

                ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new ProfileFragment()).commit();
            }
        });

        holder.username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {//if click on user name go to publisher profile
                SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS", MODE_PRIVATE).edit();
                editor.putString("profileid", post.getPublisher());
                editor.apply();

                ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new ProfileFragment()).commit();
            }
        });

        holder.publisher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {//if click on publisher comment go to publisher profile
                SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS", MODE_PRIVATE).edit();
                editor.putString("profileid", post.getPublisher());
                editor.apply();

                ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new ProfileFragment()).commit();
            }
        });
//
        holder.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {//if click on comment go to comment activity- for left comment
                Intent intent = new Intent(mContext, CommentsActivity.class);
                intent.putExtra("postid", post.getPostid());
                intent.putExtra("publisherid", post.getPublisher());
                mContext.startActivity(intent);
            }
        });

        holder.comments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {//if click on comments go to comments activity -to see all the comments
                Intent intent = new Intent(mContext, CommentsActivity.class);
                intent.putExtra("postid", post.getPostid());
                intent.putExtra("publisherid", post.getPublisher());
                mContext.startActivity(intent);
            }
        });

        holder.post_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {//if click on post image go to post detail fragment to see the post
                SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS", MODE_PRIVATE).edit();
                editor.putString("postid", post.getPostid());
                editor.apply();

                ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new PostDetailFragment()).commit();
            }
        });

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

/*
    //add notification about likes posts
    private void addNotification(String userid, String postid){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notifications").child(userid);

        //save post details to map
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("userid", firebaseUser.getUid());
        hashMap.put("text", "liked your post");
        hashMap.put("postid", postid);
        hashMap.put("ispost", true);

        //set the map with the new details to database
        reference.push().setValue(hashMap);
    }

    //delete notification
    private void deleteNotifications(final String postid, String userid){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notifications").child(userid);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {//if something change
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){//get all the posts
                    if (snapshot.child("postid").getValue().equals(postid)){//if exist the specific post that need to delete-delete
                        snapshot.getRef().removeValue()
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Toast.makeText(mContext, "Deleted!", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    //for showing who many do likes
    private void nrLikes(final TextView likes, String postId){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Likes").child(postId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {//if somthing change
                likes.setText(dataSnapshot.getChildrenCount()+" likes");//count likes
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    //for showing who many comment of the specific post
    private void getCommetns(String postId, final TextView comments){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Comments").child(postId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {//if something change
                comments.setText("View All "+dataSnapshot.getChildrenCount()+" Comments");//comments count
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    */

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

    //for setting the button of likes
    private void isLiked(final String postid, final ImageView imageView){

        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Likes").child(postid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {//if something change
                if (dataSnapshot.child(firebaseUser.getUid()).exists()){//if like
                    imageView.setImageResource(R.drawable.ic_liked);
                    imageView.setTag("liked");
                } else{//if unlike
                    imageView.setImageResource(R.drawable.ic_like);
                    imageView.setTag("like");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
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