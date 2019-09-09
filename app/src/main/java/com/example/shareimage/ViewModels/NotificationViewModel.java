package com.example.shareimage.ViewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.shareimage.Adapters.NotificationAdapter;
import com.example.shareimage.Models.NotificationModel;
import com.example.shareimage.Models.Repository;

import java.util.ArrayList;
import java.util.Collections;

public class NotificationViewModel  extends ViewModel {



    MutableLiveData<ArrayList<NotificationModel>> NotificationListLD = new MutableLiveData<ArrayList<NotificationModel>>();

    public NotificationViewModel(){
        Repository.instance.getAllNotifications(new Repository.GetAllNotiListener() {
            @Override
            public void onComplete(ArrayList<NotificationModel> data) {
                if(data!=null){
                    ArrayList<NotificationModel> notificationList=new ArrayList<>();
                    for (NotificationModel n:data){
                        if(n.getUserIdTo().equals(Repository.instance.getAuthInstance().getCurrentUser().getUid())){
                            notificationList.add(n);
                        }
                    }
                    NotificationListLD.setValue(notificationList);
                }
            }
        });
    }

    public LiveData<ArrayList<NotificationModel>> getNotificationListLD() {
        return NotificationListLD;
    }


}
