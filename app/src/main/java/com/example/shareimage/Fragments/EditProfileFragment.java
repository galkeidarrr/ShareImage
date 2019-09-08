package com.example.shareimage.Fragments;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.shareimage.Models.Repository;
import com.example.shareimage.Models.UserModel;
import com.example.shareimage.R;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.nio.channels.ReadPendingException;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class EditProfileFragment extends Fragment {
    private static final String TAG = "EditProfileFragment";
    ImageView close, image_profile;
    TextView save, tv_change;
    MaterialEditText fullname, username, bio;
    Repository repository;
    FirebaseUser firebaseUser;
    UserModel us;

    private Uri mImageUri;
    String miUrlOk = "";
    private StorageTask uploadTask;
    StorageReference storageRef;
    public EditProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_edit_profile, container, false);

//save the variables
        close = view.findViewById(R.id.edit_close);
        image_profile = view.findViewById(R.id.edit_image_profile);
        save = view.findViewById(R.id.edit_save);
        tv_change = view.findViewById(R.id.edit_tv_change);
        fullname = view.findViewById(R.id.edit_fullname);
        username = view.findViewById(R.id.edit_username);
        bio = view.findViewById(R.id.edit_bio);
        firebaseUser=repository.instance.getAuthInstance().getCurrentUser();

        repository.instance.getUser(firebaseUser.getUid(), new Repository.GetUserListener() {
            @Override
            public void onComplete(UserModel userModel) {
                if(userModel!=null){
                    fullname.setText(userModel.getFullName());
                    username.setText(userModel.getUserName());
                    bio.setText(userModel.getBio());
                    Glide.with(getActivity().getApplicationContext()).load(userModel.getImageUrl()).into(image_profile);
                }
            }
        });

        //the user want to close the edit profile
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: close the edit profile");
                Navigation.findNavController(view)
                        .popBackStack(R.id.profileFragment,false);
            }
        });

        //the user want to save the new details
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: save all changes");
                repository.instance.updateProfile(fullname.getText().toString(),
                        username.getText().toString(),
                        bio.getText().toString(), new Repository.EditProfileListener() {
                            @Override
                            public void onComplete(boolean success) {
                                if (success){
                                    //after saving go to home fragment
                                    Navigation.findNavController(view).navigate(R.id.action_global_profileFragment);
                                }
                            }
                        });

            }
        });


        //if user click on edit picture text view
        tv_change.setOnClickListener(v1 -> {
            CropImage.activity()
                    .setAspectRatio(1,1)
                    .start(getContext(),this);
        });


        //if user click on the profile picture to change
        image_profile.setOnClickListener(v2 -> {
            CropImage.activity()
                    .setAspectRatio(1,1)
                    .start(getContext(),this);
        });

        return view;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            mImageUri = result.getUri();
            //An edit that shows the user the image as a profile picture

            repository.instance.getUser(firebaseUser.getUid(), new Repository.GetUserListener() {
                @Override
                public void onComplete(UserModel userModel) {
                    us=userModel;
                    repository.instance.uploadImage(mImageUri,us);
                    image_profile.setImageURI(mImageUri);
                }
            });

        } else {
            Toast.makeText(getActivity(), "Something gone wrong!", Toast.LENGTH_SHORT).show();

        }
    }
}
