package com.example.shareimage.Fragments;



import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.example.shareimage.Activities.MainActivity;
import com.example.shareimage.Models.Repository;
import com.example.shareimage.Models.UserModel;
import com.example.shareimage.R;
import com.theartofdev.edmodo.cropper.CropImage;


import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class RegisterFragment extends Fragment {
    private static final String TAG = "RegisterFragment";
    EditText RegUserName, RegFullName, RegEmail, RegPassword;
    Button RegisterBtn;
    TextView RegToLoginTV;
    Repository repository;
    CircleImageView userImg;

    private static final int REUQUEST_CODE =1 ;
    private static final int  CAMERA_REQUEST_CODE =1;

    private Uri mImageUri;
    private int requestCode;
    private int resultCode;
    private Intent data;

    public RegisterFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        // Inflate the layout for this fragment
        View v=inflater.inflate(R.layout.fragment_register, container, false);

        verifyPermissions();
        //TODO: check if any user is connected go to home fragment

        //save the variables
        RegUserName = v.findViewById(R.id.reg_username_et);
        RegEmail = v.findViewById(R.id.reg_email_et);
        RegFullName = v.findViewById(R.id.reg_fullname_et);
        RegPassword = v.findViewById(R.id.reg_password_et);
        RegisterBtn = v.findViewById(R.id.reg_register_btn);
        RegToLoginTV = v.findViewById(R.id.reg_to_login_tv);
        userImg= v.findViewById(R.id.reg_UserImage);

        Log.d(TAG, "onClick: the user want to register");
        userImg.setOnClickListener(v1 -> {
            CropImage.activity()
                    .setAspectRatio(1,1)
                    .start(getContext(),this);
        });
        RegisterBtn.setOnClickListener(view -> {
            Log.d(TAG, "onClick: the user want to register");

            String str_userName = RegUserName.getText().toString();
            String str_fullName = RegFullName.getText().toString();
            String str_email = RegEmail.getText().toString();
            String str_password = RegPassword.getText().toString();
            String str_userImage;
            if(mImageUri!=null){
                str_userImage=mImageUri.toString();
            }
            else {
                str_userImage="https://firebasestorage.googleapis.com/v0/b/shareimage-ff623.appspot.com/o/person.png?alt=media&token=4bf4885d-19e8-4975-9f19-3d48ec7d9295";
            }
            //one or more fields are empty
            if (TextUtils.isEmpty(str_userName) || TextUtils.isEmpty(str_fullName) || TextUtils.isEmpty(str_email) || TextUtils.isEmpty(str_password)){
                Toast.makeText(getActivity(), "All fields are required!", Toast.LENGTH_SHORT).show();
            } else //everything is ok
                if(str_password.length() < 9){//the password length in less then 9
                    Toast.makeText(getActivity(), "Password must have 9 characters!", Toast.LENGTH_SHORT).show();
                } else
                    repository.instance.register(new UserModel("",str_email, str_password, str_userName, str_fullName, str_userImage, ""), mImageUri, new Repository.AddUserListener() {
                        @Override
                        public void onComplete(boolean success) {
                            if (success) {
                                getActivity().startActivity(new Intent(getActivity(), MainActivity.class));
                            } else {
                                Toast.makeText(getActivity(), "You can't register with this email or password", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        });

        return v;
    }

    //User permission to access files on the device
    private void verifyPermissions(){
        Log.d("RegisterActivity","verifyPermissions: asking user for permissions");
        String[] permissions={Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.CAMERA,Manifest.permission.INTERNET};
        if(ContextCompat.checkSelfPermission(getActivity().getApplicationContext(),
                permissions[0])== PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(getActivity().getApplicationContext(),
                permissions[1])== PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(getActivity().getApplicationContext(),
                permissions[2])== PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(getActivity().getApplicationContext(),
                permissions[3])== PackageManager.PERMISSION_GRANTED){


        }
        else {
            ActivityCompat.requestPermissions(getActivity(),permissions,REUQUEST_CODE);
        }

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        verifyPermissions();
    }
    /*
    public void showAlertDialogButtonClicked(View view) {
        // setup the alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Choose an animal");
        // add a list
        String[] options = {"open Gallery", "open Camera","close"};
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 0);
                        return;
                    case 1:
                        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
                        return;
                    case 2:

                }
            }
        });
        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    */



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            mImageUri = result.getUri();
            //An edit that shows the user the image as a profile picture
            userImg.setImageURI(mImageUri);
        } else {
            Toast.makeText(getActivity(), "Something gone wrong!", Toast.LENGTH_SHORT).show();

        }
    }
}
