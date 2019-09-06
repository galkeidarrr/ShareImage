package com.example.shareimage.Models;


import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.widget.Button;

import com.example.shareimage.ViewModels.MyApplication;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
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
        void onComplete(ArrayList<UserModel> data);
    }
    public void getAllUsers(GetAllUsersListener listener) {
        fireBaseModel.getAllUsers(listener);
    }
    public interface GetUserListener{
        void onComplete(UserModel userModel);
    }
    public void getUser(String id,GetUserListener listener){fireBaseModel.getUser(id,listener);}

    public interface AddUserListener {
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

    public interface GetNotifiListener {
        void onComplete(boolean success);
    }

    public void addLikeNotification(String userId,String postId,GetNotifiListener listener){
        fireBaseModel.addLikeNotification(userId,postId,listener);
    }
    public void addFollowNotification(String userId, GetNotifiListener listener){
        fireBaseModel.addFollowNotification(userId,listener);
    }
    public interface GetNewFollowListener {
        void onComplete(boolean success);
    }

    public void addFollow(UserModel userModel1,UserModel userModel2,GetNewFollowListener listener){

        fireBaseModel.addFollow(userModel1,userModel2,listener);
    }
    public interface DeleteFollowListener {
        void onComplete(boolean success);
    }
    public void deleteFollow(UserModel userModel1,UserModel userModel2,DeleteFollowListener listener){
        fireBaseModel.deleteFollow(userModel1,userModel2,listener);
    }

    public interface GetisFollowListener {
        void onComplete(boolean success);
    }
    public void isFollowing(final String userid, final Button button,GetisFollowListener listener){
        fireBaseModel.isFollowing(userid,button,listener);
    }

    public interface GetSearchUsersListener {
        void onComplete(ArrayList<UserModel> userModelArrayList);
    }
    public void searchUsers(String s,GetSearchUsersListener listener){
        fireBaseModel.searchUsers(s,listener);
    }
    //TODO: get all users and add user
    //public void addUser(UserModel user) {FireBaseModel.addUser(user); }

}

