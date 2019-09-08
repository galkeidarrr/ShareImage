package com.example.shareimage.Fragments;


import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shareimage.Models.PostModel;
import com.example.shareimage.Models.Repository;
import com.example.shareimage.R;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.theartofdev.edmodo.cropper.CropImage;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 */
public class ShareFragment extends Fragment {
    private static final String TAG = "ShareFragment";
    private Uri mImageUri;
    String miUrlOk = "";
    private StorageTask uploadTask;
    StorageReference storageRef;
    FirebaseUser firebaseUser;
    ImageView close, image_added;
    TextView post;
    EditText description;
    Repository repository;

    public ShareFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_share, container, false);
        firebaseUser=repository.instance.getAuthInstance().getCurrentUser();
        SharedPreferences.Editor editor = getContext().getSharedPreferences("PREFS", MODE_PRIVATE).edit();
        editor.putString("profileid", firebaseUser.getUid());
        editor.apply();
        //save the variables
        close = view.findViewById(R.id.share_closeBtn);
        image_added = view.findViewById(R.id.share_image_added);
        post = view.findViewById(R.id.share_postBtn);
        description = view.findViewById(R.id.share_description);

        //Close the post upload window and go to mainActivity
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: Close the post upload window and go to homeFragment");
                Navigation.findNavController(view)
                        .popBackStack(R.id.homeFragment,false);
            }
        });

        //when post click share and save the post
        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: save and share the post");
                //TODO: chack Post id
                String postId=mImageUri.getLastPathSegment();
                PostModel postModel=new PostModel(postId,mImageUri.toString(),description.getText().toString(),
                        repository.instance.getAuthInstance().getCurrentUser().getUid().toString());
                repository.instance.addPost(postModel, mImageUri, new Repository.AddPostListener() {
                    @Override
                    public void onComplete(boolean success) {
                        if (success){
                            Navigation.findNavController(view)
                                    .navigate(R.id.action_global_homeFragment);
                        }
                    }
                });


            }
        });
        CropImage.activity()
                .setAspectRatio(1,1)
                .start(getContext(),this);

        return view;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            mImageUri = result.getUri();
            //An edit that shows the user the image as a profile picture
            image_added.setImageURI(mImageUri);
        } else {
            Toast.makeText(getActivity(), "Something gone wrong!", Toast.LENGTH_SHORT).show();

        }
    }

}
