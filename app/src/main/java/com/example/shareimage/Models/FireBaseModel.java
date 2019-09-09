package com.example.shareimage.Models;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;

import com.example.shareimage.R;
import com.example.shareimage.ViewModels.MyApplication;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

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
    ArrayList<UserModel> searchList;
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
                    Log.d(TAG, "onComplete: "+task.getResult().getUser());
                    firebaseUser=task.getResult().getUser();
                    //firebaseAuth.updateCurrentUser(task.getResult().getUser());
                }
                l.onComplete(task.isSuccessful());
            }
        });
    }

    public void logOut() {
        getAuthInstance().signOut();
    }

    public void addPost(final PostModel post, final Uri mImageUri, final Repository.AddPostListener l){
        Log.d(TAG, "register: create new user");
        db.collection("posts").document(post.getPostId()).set(post).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    uploadPost(post,mImageUri);
                }
                l.onComplete(task.isSuccessful());
            }
        });

    }

    public void deletePost(final String postId,final Repository.DeletePostListener listener){
        db.collection("posts").document(postId).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                listener.onComplete(task.isSuccessful());
            }
        });
    }

    public void uploadPost(PostModel post,Uri mImageUri){
        Log.d(TAG, "uploadPost: upload new post");
        storageRef = FirebaseStorage.getInstance().getReference("posts");
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
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    Log.d(TAG, "onComplete: task complete");
                    if (task.isSuccessful()) {//if success
                        Uri downloadUri = task.getResult();
                        miUrlOk = downloadUri.toString();
                        post.setPostImage(miUrlOk);
                        db.collection("posts").document(post.getPostId()).set(post).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Log.d(TAG, "onComplete: final !!");
                            }
                        });
                    } else {//If did not succeed the task of upload post and save toast a message
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

    //Upload a profile picture and edit the user with the new one
    public void uploadImage(Uri mImageUri,UserModel userModel){
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

    private void addPictureToGallery(File imageFile){
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
                ArrayList<UserModel> data = new ArrayList<>();
                if (e != null) {
                    listener.onComplete(data);
                    return;
                }
                for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                    UserModel user= new UserModel(doc.getString("id"),doc.getString("email"),
                            doc.getString("password"),doc.getString("userName"),
                            doc.getString("fullName"),doc.getString("imageUrl"),
                            doc.getString("bio"));
                    data.add(user);
                }
                listener.onComplete(data);
            }
        });

    }


    public void getUser(String id, Repository.GetUserListener listener) {
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

    public void addLikeNotification(String userId,String postId,final Repository.GetNotifiListener listener){
        firebaseUser=getAuthInstance().getCurrentUser();
        String newPostId=postId.replace("cropped","");
        NotificationModel notificationModel=new NotificationModel(firebaseUser.getUid(),userId,"liked your post",postId,true);
        db.collection("Notifications").document(newPostId).set(notificationModel)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        listener.onComplete(task.isSuccessful());
                    }
                });
    }

    public void removeLikeNotification(String userId, String postId, final Repository.GetNotifiListener listener){
        String newPostId=postId.replace("cropped","");
        db.collection("Notifications").document(newPostId).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                listener.onComplete(task.isSuccessful());
            }
        });

    }

    public void addFollowNotification(String userId,final Repository.GetNotifiListener listener){
        firebaseUser=getAuthInstance().getCurrentUser();
        NotificationModel notificationModel=new NotificationModel(firebaseUser.getUid(),userId,"started following you","",false);
        db.collection("Notifications").document(userId).set(notificationModel)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        listener.onComplete(task.isSuccessful());
                    }
                });
    }

    public void removeFollowNotification(String userId,final Repository.GetNotifiListener listener){
        db.collection("Notifications").document(userId).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                listener.onComplete(task.isSuccessful());
            }
        });
    }

    public void getAllNotifications(final Repository.GetAllNotiListener listener){
        db.collection("Notifications").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                ArrayList<NotificationModel> data = new ArrayList<>();
                if (e != null) {
                    listener.onComplete(data);
                    return;
                }
                for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                    NotificationModel noti= doc.toObject(NotificationModel.class);
                    data.add(noti);
                }
                listener.onComplete(data);
            }
        });
    }

    public void addFollow(UserModel userModel1, UserModel userModel2, final Repository.GetNewFollowListener listener){
        firebaseUser=getAuthInstance().getCurrentUser();
        userModel1.getFollows().add(userModel2.getId());
        userModel2.getFollowers().add(userModel1.getId());
        db.collection("users").document(userModel1.getId()).set(userModel1)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        db.collection("users").document(userModel2.getId()).set(userModel2)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        listener.onComplete(task.isSuccessful());
                                    }
                                });
                    }
                });

    }

    public void deleteFollow(UserModel userModel1, UserModel userModel2,final Repository.DeleteFollowListener listener){
        firebaseUser=getAuthInstance().getCurrentUser();
        userModel1.getFollows().remove(userModel2.getId());
        userModel2.getFollowers().remove(userModel1.getId());
        db.collection("users").document(userModel1.getId()).set(userModel1)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        db.collection("users").document(userModel2.getId()).set(userModel2)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        listener.onComplete(task.isSuccessful());
                                    }
                                });
                    }
                });
    }

    UserModel mUser=null;
    public void isFollowing(final String userid, final Button button, final Repository.GetisFollowListener listener){
        firebaseUser=getAuthInstance().getCurrentUser();

        getUser(firebaseUser.getUid(), new Repository.GetUserListener() {
            @Override
            public void onComplete(UserModel userModel) {
                if(userModel!=null) {
                    mUser = userModel;
                    if(mUser.getFollowers().contains(userid)){
                        button.setText("following");
                    }else{
                        button.setText("follow");
                    }
                    listener.onComplete(true);
                }
            }
        });
        listener.onComplete(false);
    }

    public void searchUsers(String s, Repository.GetSearchUsersListener listener){
        searchList=new ArrayList<>();
        db.collection("users").orderBy("userName").startAt(s).endAt(s+"\uf8ff")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        //call when searching is succeeded
                        Log.d(TAG, "onComplete: search do good job!"+task.getResult().getDocuments());
                        searchList.clear();
                        for (DocumentSnapshot doc: task.getResult()){
                            UserModel userModel= new UserModel(doc.getString("id"),doc.getString("email"),
                                    doc.getString("password"),doc.getString("userName"),
                                    doc.getString("fullName"),doc.getString("imageUrl"),
                                    doc.getString("bio"));
                            searchList.add(userModel);
                        }
                        listener.onComplete(searchList);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //when there is any error
                Log.d(TAG, "onFailure: error: "+e);

            }
        });

    }


    public void getAllPost(final Repository.GetAllPostsListener listener){
        db.collection("posts").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                ArrayList<PostModel> data = new ArrayList<>();
                if (e != null) {
                    listener.onComplete(data);
                    return;
                }
                for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                    PostModel post= doc.toObject(PostModel.class);
                    data.add(post);
                }
                listener.onComplete(data);
            }
        });
        /*
        db.collection("posts").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    ArrayList<PostModel> list = new ArrayList<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        PostModel post= document.toObject(PostModel.class);
                        list.add(post);
                    }
                    listener.onComplete(list);
                    Log.d(TAG, list.toString());
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
                listener.onComplete(null);
            }
        });
*/

    }

    public void getPost(final String postid,final Repository.GetPostListener listener){
        db.collection("posts").document(postid).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot snapshot = task.getResult();
                            PostModel postModel = snapshot.toObject(PostModel.class);
                            listener.onComplete(postModel);
                            return;
                        }
                        listener.onComplete(null);
                    }
                });
    }

    public void addLike(final String postid, final Repository.GetNewLikeListener listener){
        firebaseUser=getAuthInstance().getCurrentUser();
        getPost(postid, new Repository.GetPostListener() {
            @Override
            public void onComplete(PostModel postModel) {
                if(postModel!=null){
                    postModel.likes.add(firebaseUser.getUid());
                    db.collection("posts").document(postid).set(postModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            listener.onComplete(task.isSuccessful());
                        }
                    });
                }
            }
        });
    }

    public void deleteLike(final String postid, final Repository.DeleteLikeListener listener){
        firebaseUser=getAuthInstance().getCurrentUser();
        getPost(postid, new Repository.GetPostListener() {
            @Override
            public void onComplete(PostModel postModel) {
                if(postModel!=null){
                    postModel.likes.remove(firebaseUser.getUid());
                    db.collection("posts").document(postid).set(postModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            listener.onComplete(task.isSuccessful());
                        }
                    });
                }
            }
        });
    }

    PostModel mPost=null;
    public void isLiked(final String postid, final ImageView imageView, final Repository.GetisLikedListener listener){
        firebaseUser=getAuthInstance().getCurrentUser();
        getPost(postid, new Repository.GetPostListener() {
            @Override
            public void onComplete(PostModel postModel) {
                if(postModel!=null) {
                    mPost=postModel;
                    if (mPost.likes.contains(firebaseUser.getUid())) {
                        imageView.setImageResource(R.drawable.ic_liked);
                        imageView.setTag("liked");
                    } else {
                        imageView.setImageResource(R.drawable.ic_like);
                        imageView.setTag("like");
                    }
                    listener.onComplete(true);
                }
                listener.onComplete(false);
            }
        });
    }

    public void addSave(final String postid, final Repository.GetNewSaveListener listener){
        firebaseUser=getAuthInstance().getCurrentUser();
        getUser(firebaseUser.getUid(), new Repository.GetUserListener() {
            @Override
            public void onComplete(UserModel userModel) {
                if(userModel!=null){
                    userModel.saves.add(postid);
                    db.collection("users").document(firebaseUser.getUid()).set(userModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            listener.onComplete(task.isSuccessful());
                        }
                    });
                }
            }
        });


    }

    public void deleteSave(final String postid, final Repository.DeleteLikeListener listener){
        firebaseUser=getAuthInstance().getCurrentUser();
        getUser(firebaseUser.getUid(), new Repository.GetUserListener() {
            @Override
            public void onComplete(UserModel userModel) {
                if(userModel!=null){
                    userModel.saves.remove(postid);
                    db.collection("users").document(firebaseUser.getUid()).set(userModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            listener.onComplete(task.isSuccessful());
                        }
                    });
                }
            }
        });

    }

    UserModel mP=null;
    public void isSaved(final String postid, final ImageView imageView, final Repository.GetisLikedListener listener){
        firebaseUser=getAuthInstance().getCurrentUser();
        getUser(firebaseUser.getUid(), new Repository.GetUserListener() {
            @Override
            public void onComplete(UserModel userModel) {
                if(userModel!=null) {
                    mP=userModel;
                    if (mP.saves.contains(postid)) {
                        imageView.setImageResource(R.drawable.ic_save_black);
                        imageView.setTag("saved");
                    } else {
                        imageView.setImageResource(R.drawable.ic_savee_black);
                        imageView.setTag("save");
                    }
                    listener.onComplete(true);
                }
                listener.onComplete(false);
            }
        });


    }

    public void addComment(final String comment,final String publisherid,final String postId, final Repository.AddCommentListener listener){

        CommentModel c=new CommentModel(comment,publisherid,"");
        db.collection("comments").add(c).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                c.setCommentId(documentReference.getId());
                db.collection("comments").document(documentReference.getId()).set(c).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Repository.instance.getPost(postId, new Repository.GetPostListener() {
                            @Override
                            public void onComplete(PostModel postModel) {
                                if (postModel != null) {
                                    Log.d(TAG, "onComplete: !!" + documentReference.getId());
                                    postModel.comments.add(documentReference.getId());
                                    db.collection("posts").document(postId).set(postModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            listener.onComplete(documentReference.getId());
                                        }
                                    });
                                }
                            }
                        });
                    }
                });
            }
        });
    }

    public void deleteComment(final String commentId,final String postId, final Repository.DeleteCommentListener listener){
        db.collection("comments").document(commentId).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Repository.instance.getPost(postId, new Repository.GetPostListener() {
                    @Override
                    public void onComplete(PostModel postModel) {
                        if (postModel != null) {
                            postModel.comments.remove(commentId);
                            db.collection("posts").document(postId).set(postModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    listener.onComplete(task.isSuccessful());
                                }
                            });
                        }
                    }
                });

            }
        });
    }

    String commentL;
    public void addCommentNotification(String commentId,String publisherid,String comment,String postId,final Repository.GetNotifiListener listener){
        firebaseUser=getAuthInstance().getCurrentUser();
        commentL="commented: "+comment;
        NotificationModel notificationModel=new NotificationModel(firebaseUser.getUid(),publisherid,commentL,postId,true);
        db.collection("Notifications").document(commentId).set(notificationModel)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        listener.onComplete(task.isSuccessful());
                    }
                });
    }

    public void removeCommentNotification(String commentId,final Repository.GetNotifiListener listener){
        db.collection("Notifications").document(commentId).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                listener.onComplete(task.isSuccessful());
            }
        });
    }

    public void getAllComments(final Repository.GetAllCommentsListener listener){
        db.collection("comments").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    ArrayList<CommentModel> list = new ArrayList<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        CommentModel comment= document.toObject(CommentModel.class);
                        list.add(comment);
                    }
                    listener.onComplete(list);
                    Log.d(TAG, list.toString());
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
                listener.onComplete(null);
            }
        });

    }


    public void editPost(final String postId, final String description, final Repository.EditPostListener listener){
        getPost(postId, new Repository.GetPostListener() {
            @Override
            public void onComplete(PostModel postModel) {
                if(postModel!=null){
                    PostModel p=postModel;
                    p.setDescription(description);
                    db.collection("posts").document(postId).set(p).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            listener.onComplete(task.isSuccessful());
                        }
                    });
                }
            }
        });
    }

    UserModel u;
    public void updateProfile(final String fullName, final String userName, final String bio,final Repository.EditProfileListener listener){
        firebaseUser=getAuthInstance().getCurrentUser();
        getUser(firebaseUser.getUid(), new Repository.GetUserListener() {
            @Override
            public void onComplete(UserModel userModel) {
                if (userModel!=null){
                    u=userModel;
                    u.setFullName(fullName);
                    u.setUserName(userName);
                    u.setBio(bio);
                    db.collection("users").document(firebaseUser.getUid()).set(u).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            listener.onComplete(task.isSuccessful());
                        }
                    });
                }
            }
        });
    }

    public void saveImageToFile(Bitmap imageBitmap, String imageFileName, final Repository.UploadFileListener listener){
        try {
            File dir = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File imageFile = new File(dir,imageFileName);
            imageFile.createNewFile();
            OutputStream out = new FileOutputStream(imageFile);
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.close();
            addPictureToGallery(imageFile);
            listener.onComplete(true);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
