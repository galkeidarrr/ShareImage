package com.example.shareimage.Adapters;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.shareimage.Activities.MainActivity;
import com.example.shareimage.Models.Repository;
import com.example.shareimage.Models.UserModel;
import com.example.shareimage.ProfileFragment;
import com.example.shareimage.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Context.MODE_PRIVATE;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ImageViewHolder>{
    private static final String TAG = "UserAdapter";
    private Context mContext;
    private List<UserModel> mUsers;
    private boolean isFragment;
    Repository repository;
    private FirebaseUser firebaseUser;

    //Constractor with variables
    public UserAdapter(Context context, List<UserModel> users, boolean isFragment){
        mContext = context;
        repository.getAllUsers(new Repository.GetAllUsersListener() {
            @Override
            public void onComplete(List<UserModel> data) {
                if(data!=null){
                    mUsers=data;
                }
            }
        });
        this.isFragment = isFragment;
    }

    @NonNull
    @Override
    public UserAdapter.ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.user_row_item, parent, false);
        return new UserAdapter.ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserAdapter.ImageViewHolder holder, int position) {

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        final UserModel user = mUsers.get(position);

        holder.btn_follow.setVisibility(View.VISIBLE);
        //isFollowing(user.getId(), holder.btn_follow);

        holder.username.setText(user.getUserName());
        holder.fullname.setText(user.getFullName());
        Glide.with(mContext).load(user.getImageUrl()).into(holder.image_profile);

        if (user.getId().equals(firebaseUser.getUid())){//can't fallow after yourself
            holder.btn_follow.setVisibility(View.GONE);
        }

        //if user click on other user go to his profile
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isFragment) {
                    SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS", MODE_PRIVATE).edit();
                    editor.putString("profileid", user.getId());
                    editor.apply();
                    //TODO: check if visit another profile
                    Navigation.createNavigateOnClickListener(R.id.action_global_profileFragment);

                } else {
                    Intent intent = new Intent(mContext, MainActivity.class);
                    intent.putExtra("publisherid", user.getId());
                    mContext.startActivity(intent);
                }
            }
        });


        //TODO: do followers function
        final UserModel[] userModel1 = new UserModel[1];
        //if the user want to follow after other user change the button and update in database
        holder.btn_follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                repository.getUser(user.getId(), new Repository.GetUserListener() {
                    @Override
                    public void onComplete(UserModel userModel) {
                        if(userModel!=null){
                            userModel1[0] =userModel;
                        }
                    }
                });
                repository.getUser(firebaseUser.getUid(), new Repository.GetUserListener() {
                    @Override
                    public void onComplete(UserModel userModel) {
                        if(userModel!=null){
                            userModel1[1] =userModel;
                        }
                    }
                });
                if (holder.btn_follow.getText().toString().equals("follow")) {
                    repository.addFollow(userModel1[0], userModel1[1], new Repository.GetNewFollowListener() {
                        @Override
                        public void onComplete(boolean success) {
                            repository.addFollowNotification(user.getId(),new Repository.GetNotifiListener(){
                                @Override
                                public void onComplete(boolean success) {
                                    if(!success){
                                        Log.d(TAG, "onComplete: faild to add follow");
                                    }
                                }
                            });
                        }
                    });

                } else {//If the user does not want to follow you return the button to the first option
                    // and remove it from the followers in Data Base
                   repository.deleteFollow(userModel1[0], userModel1[1], new Repository.DeleteFollowListener() {
                       @Override
                       public void onComplete(boolean success) {
                           if(!success){
                               Log.d(TAG, "onComplete: faild to remove follow");
                           }
                       }
                   });
                }
            }

        });


}


    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {//ImageViewHolder class

        public TextView username;
        public TextView fullname;
        public CircleImageView image_profile;
        public Button btn_follow;

        public ImageViewHolder(View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.urow_username);
            fullname = itemView.findViewById(R.id.urow_fullname);
            image_profile = itemView.findViewById(R.id.urow_image_profile);
            btn_follow = itemView.findViewById(R.id.urow_btn_follow);
        }
    }
}
