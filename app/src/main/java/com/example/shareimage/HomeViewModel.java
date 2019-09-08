package com.example.shareimage;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.shareimage.Models.PostModel;
import com.example.shareimage.Models.Repository;
import com.example.shareimage.Models.UserModel;

import java.util.ArrayList;

public class HomeViewModel extends ViewModel {

    MutableLiveData<ArrayList<PostModel>> postListLD = new MutableLiveData<>();


    public HomeViewModel() {
        Repository.instance.getUser(Repository.instance.getAuthInstance().getCurrentUser().getUid().toString(), new Repository.GetUserListener() {
            @Override
            public void onComplete(UserModel userModel) {
                if(userModel!=null){
                    Repository.instance.getAllPost(new Repository.GetAllPostsListener() {
                        @Override
                        public void onComplete(ArrayList<PostModel> data) {
                            if(data!=null){
                                ArrayList<PostModel> postList=new ArrayList<>();
                                for (PostModel p:data) {
                                    for (String id : userModel.getFollowers()) {
                                        if (p.getPublisher().equals(id)) {
                                            postList.add(p);
                                        }
                                    }
                                }
                                postListLD.setValue(postList);
                            }
                        }
                    });
                }
            }
        });
    }


    public LiveData<ArrayList<PostModel>> getPostList() {
        return postListLD;
    }

}
