package com.example.shareimage.Models;


import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.shareimage.ViewModels.MyApplication;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Date;
import java.util.LinkedList;

import javax.annotation.Nullable;

public class FireBaseModel {
    private static final String TAG = "FireBaseModel";
    final public static FireBaseModel instance = new FireBaseModel();
    FirebaseFirestore db;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    String miUrlOk = "";
    private StorageTask uploadTask;
    StorageReference storageRef;
    public FireBaseModel(){
        db=FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new
                FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(false)
                .build();
        db.setFirestoreSettings(settings);
        firebaseAuth=FirebaseAuth.getInstance();


    }

    public FirebaseAuth getAuthInstance(){
        return FirebaseAuth.getInstance();
    }

    public void register(final UserModel user, final Uri mImageUri, final Repository.AddUserListener l){
        Log.d(TAG, "register: create new user");
        firebaseAuth = getAuthInstance();
        firebaseAuth.createUserWithEmailAndPassword(user.getEmail(),user.getPassword())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull final Task<AuthResult> task) {
                        //TODO: add profile picture
                        if(task.isSuccessful()){
                            firebaseUser = firebaseAuth.getCurrentUser();
                            String userID = firebaseUser.getUid();
                            user.setId(userID);
                            db.collection("users").document(user.getId()).set(user);
                            uploadImage(mImageUri,user);
                            l.onComplete(task.isSuccessful());
                        }
                        else{
                            l.onComplete(task.isSuccessful());
                        }
                    }
                });
    }

    public void login(String email, String password,final Repository.LoginUserListener l) {
        firebaseAuth = getAuthInstance();
        firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    firebaseAuth.updateCurrentUser(task.getResult().getUser());
                }
                l.onComplete(task.isSuccessful());
            }
        });
    }

    public void logOut() {
        getAuthInstance().signOut();
    }

    //Upload a profile picture and edit the user with the new one
    private void uploadImage(Uri mImageUri,UserModel userModel){
        //Save a new folder in the storage of profile pictures
        Log.d(TAG, "uploadImage: Upload a profile picture and edit the user with the new one");
        storageRef = FirebaseStorage.getInstance().getReference("profileImage");
        if (mImageUri != null){//If the selected or taken image is not null
            //Save in storage with this path
            final StorageReference fileReference = storageRef.child(System.currentTimeMillis()
                    + "." + mImageUri.getLastPathSegment());

            uploadTask = fileReference.putFile(mImageUri);
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    Log.d(TAG, "then: task of upload the file(image) to the storage");
                    if (!task.isSuccessful()) {//If the save in the storage is Unsuccessful
                        throw task.getException();
                    }
                    return fileReference.getDownloadUrl();//if successful return the URI
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {//add to the successful task
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    Log.d(TAG, "onComplete: task complete");
                    if (task.isSuccessful()) {//if success
                        Log.d(TAG, "onComplete: task succeed");
                        Uri downloadUri = task.getResult();
                        miUrlOk = downloadUri.toString();//save the URI in variable
                        userModel.setImageUrl(miUrlOk);
                        db.collection("users").document(userModel.getId()).set(userModel);

                    } else {//If did not succeed the task of upload profile image and save toast a message
                        Log.d(TAG, "onComplete: task not succeed");
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {//if Did not succeed toast a exception message
                    Log.d(TAG, "onComplete: task not succeed and not complete");
                }
            });

        } else {//The user did not choose to upload a photo
            Log.d(TAG, "uploadImage: The user did not choose to upload a photo ");
        }
    }




    public void saveImage(Bitmap imageBitmap, final Repository.SaveImageListener listener) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        // Create a storage reference from our app
        StorageReference storageRef = storage.getReference();

        Date d = new Date();
        // Create a reference to "mountains.jpg"
        final StorageReference imageStorageRef = storageRef.child("image_" + d.getTime() + ".jpg");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = imageStorageRef.putBytes(data);

        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                // Continue with the task to get the download URL
                return imageStorageRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    listener.onComplete(downloadUri.toString());
                } else {
                    listener.onComplete(null);
                }
            }
        });
    }

    private void addPicureToGallery(File imageFile){
        //add the picture to the gallery so we dont need to manage the cache size
        Intent mediaScanIntent = new
                Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri contentUri = Uri.fromFile(imageFile);
        mediaScanIntent.setData(contentUri);
        MyApplication.getAppContext().sendBroadcast(mediaScanIntent);
    }


    public void getAllUsers(final Repository.GetAllUsersListener listener) {
        db.collection("users").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                LinkedList<UserModel> data = new LinkedList<>();
                if (e != null) {
                    listener.onComplete(data);
                    return;
                }
                for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                    UserModel user= doc.toObject(UserModel.class);
                    data.add(user);
                }
                listener.onComplete(data);
            }
        });
    }
    interface GetUserListener {
        void onComplete(UserModel user);
    }

    public void getUser(String id, final GetUserListener listener) {
        db.collection("users").document(id).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot snapshot = task.getResult();
                            UserModel user = snapshot.toObject(UserModel.class);
                            listener.onComplete(user);
                            return;
                        }
                        listener.onComplete(null);
                    }
                });
    }


}
