package com.example.shareimage.Models;


import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;

import com.example.shareimage.ViewModels.MyApplication;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class Repository {//singleton model to manage all the information (sqlite ,firebase,storage,files)
    final public static Repository instance = new Repository();

    private static Context context;
    FireBaseModel fireBaseModel;
    SqlModel sqlModel;

    public Repository() {
        context = MyApplication.getAppContext();
        fireBaseModel=new FireBaseModel();
        sqlModel=new SqlModel();
    }
    public interface GetAllUsersListener{
        void onComplete(List<UserModel> data);
    }
    public void getAllUsers(GetAllUsersListener listener) {
        fireBaseModel.getAllUsers(listener);
    }

    public interface AddUserListener{
        void onComplete(boolean success);
    }
    public void register(final UserModel user, Uri mImageUri, AddUserListener l){
        fireBaseModel.register(user,mImageUri,l);
    }

    public interface LoginUserListener{
        void onComplete(boolean success);
    }
    public void login(String email, String password,LoginUserListener l){
        fireBaseModel.login(email,password,l);
    }
    public interface SaveImageListener{
        void onComplete(String url);
    }
    public void saveImage(Bitmap imageBitmap, SaveImageListener listener) {
        fireBaseModel.saveImage(imageBitmap, listener);
    }

    public FirebaseAuth getAuthInstance(){
        return fireBaseModel.firebaseAuth;
    }


    //TODO: get all users and add user
    //public void addUser(UserModel user) {FireBaseModel.addUser(user); }

}

