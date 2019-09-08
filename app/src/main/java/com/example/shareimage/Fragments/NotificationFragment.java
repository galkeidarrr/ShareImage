package com.example.shareimage.Fragments;


import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.shareimage.Adapters.NotificationAdapter;
import com.example.shareimage.Models.NotificationModel;
import com.example.shareimage.Models.Repository;
import com.example.shareimage.R;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 */
public class NotificationFragment extends Fragment {

    ImageView closeBtn;
    FirebaseUser firebaseUser;
    Repository repository;

    private RecyclerView recyclerView;
    private NotificationAdapter notificationAdapter;
    private ArrayList<NotificationModel> notificationList;

    public NotificationFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_notification, container, false);

        firebaseUser=repository.instance.getAuthInstance().getCurrentUser();
        SharedPreferences.Editor editor = getContext().getSharedPreferences("PREFS", MODE_PRIVATE).edit();
        editor.putString("profileid", firebaseUser.getUid());
        editor.apply();

        //save the variables
        closeBtn=view.findViewById(R.id.notification_close);
        recyclerView = view.findViewById(R.id.notification_recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        notificationList = new ArrayList<>();
        notificationAdapter = new NotificationAdapter(getContext(), notificationList);
        recyclerView.setAdapter(notificationAdapter);

        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v)
                        .popBackStack();
            }
        });
        repository.instance.getAllNotifications(new Repository.GetAllNotiListener() {
            @Override
            public void onComplete(ArrayList<NotificationModel> data) {
                if(data!=null){
                    notificationList.clear();
                    for (NotificationModel n:data){
                        if(n.getUserIdTo().equals(firebaseUser.getUid())){
                            notificationList.add(n);
                        }
                    }
                    Collections.reverse(notificationList);
                    notificationAdapter = new NotificationAdapter(getContext(), notificationList);
                    recyclerView.setAdapter(notificationAdapter);
                }
            }
        });


        return view;
    }

}
