package com.example.shareimage.Models;


import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.widget.Button;
import android.widget.ImageView;

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
    public void logOut(){
        fireBaseModel.logOut();
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

    public void removeLikeNotification(String userId,String postId,GetNotifiListener listener){
        fireBaseModel.removeLikeNotification(userId,postId,listener);
    }

    public void removeFollowNotification(String userId,GetNotifiListener listener){
        fireBaseModel.removeFollowNotification(userId,listener);
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

    public interface AddPostListener {
        void onComplete(boolean success);
    }
    public void addPost(final PostModel post, final Uri mImageUri, final Repository.AddPostListener l){
        fireBaseModel.addPost(post,mImageUri,l);
    }
    public interface DeletePostListener {
        void onComplete(boolean success);
    }
    public void deletePost(final String postId,DeletePostListener listener){
        fireBaseModel.deletePost(postId,listener);
    }

    public interface GetAllPostsListener{
        void onComplete(ArrayList<PostModel> data);
    }
    public void getAllPost(final GetAllPostsListener listener){
        fireBaseModel.getAllPost(listener);
    }
    public interface GetPostListener{
        void onComplete(PostModel postModel);
    }
    public void getPost(final String postid,GetPostListener listener){
        fireBaseModel.getPost(postid,listener);
    }

    public interface GetisLikedListener {
        void onComplete(boolean success);
    }
    public void isLiked(final String postid, final ImageView imageView,GetisLikedListener listener){
        fireBaseModel.isLiked(postid,imageView,listener);
    }
    public interface GetNewSaveListener {
        void onComplete(boolean success);
    }
    public void addSave(final String postid, GetNewSaveListener listener){
        fireBaseModel.addSave(postid,listener);
    }

    public void deleteSave(final String postid, DeleteLikeListener listener){
        fireBaseModel.deleteSave(postid,listener);
    }

    public void isSaved(final String postid, final ImageView imageView, GetisLikedListener listener){
        fireBaseModel.isSaved(postid,imageView,listener);
    }

    public interface GetNewLikeListener {
        void onComplete(boolean success);
    }
    public void addLike(final String postid,GetNewLikeListener listener){
        fireBaseModel.addLike(postid,listener);
    }

    public interface DeleteLikeListener {
        void onComplete(boolean success);
    }

    public  void deleteLike(final String postid,DeleteLikeListener listener){
        fireBaseModel.deleteLike(postid,listener);
    }
    public interface AddCommentListener {
        void onComplete(String commentid);
    }
    public void addComment(final String comment,final String publisherid,AddCommentListener listener){
        fireBaseModel.addComment(comment,publisherid,listener);
    }
    public interface DeleteCommentListener {
        void onComplete(boolean success);
    }
    public void deleteComment(final String commentId,DeleteCommentListener listener){
        fireBaseModel.deleteComment(commentId,listener);
    }

    public void addCommentNotification(String commentId,String publisherid,String comment,String postId,GetNotifiListener listener){
        fireBaseModel.addCommentNotification(commentId,publisherid,comment,postId,listener);
    }
    public void removeCommentNotification(String commentId,GetNotifiListener listener){
        fireBaseModel.removeCommentNotification(commentId,listener);
    }
    public interface GetAllCommentsListener{
        void onComplete(ArrayList<CommentModel> data);
    }
    public void getAllComments(GetAllCommentsListener listener){
        fireBaseModel.getAllComments(listener);
    }
    public interface EditPostListener {
        void onComplete(boolean success);
    }
    public void editPost(final String postId,final String description,EditPostListener listener){
        fireBaseModel.editPost(postId,description,listener);
    }
    public interface EditProfileListener {
        void onComplete(boolean success);
    }

    public void updateProfile(final String fullName,final String userName,final String bio,EditProfileListener listener){
        fireBaseModel.updateProfile(fullName,userName,bio,listener);
    }

    public void uploadImage(Uri mImageUri,UserModel userModel){
        fireBaseModel.uploadImage(mImageUri,userModel);
    }

}

