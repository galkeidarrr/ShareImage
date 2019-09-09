package com.example.shareimage.Fragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.shareimage.Adapters.NotificationAdapter;
import com.example.shareimage.Adapters.PostAdapter;
import com.example.shareimage.Models.NotificationModel;
import com.example.shareimage.Models.PostModel;
import com.example.shareimage.Models.Repository;
import com.example.shareimage.R;
import com.example.shareimage.ViewModels.HomeViewModel;
import com.example.shareimage.ViewModels.NotificationViewModel;
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

    NotificationViewModel viewData;
    LiveData<ArrayList<NotificationModel>> NotificationListLD;

    private RecyclerView recyclerView;
    private NotificationAdapter notificationAdapter;
    //private ArrayList<NotificationModel> notificationList;

    public NotificationFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        viewData= ViewModelProviders.of(this).get(NotificationViewModel.class);
        NotificationListLD=viewData.getNotificationListLD();
        NotificationListLD.observe(this, new Observer<ArrayList<NotificationModel>>() {
            @Override
            public void onChanged(ArrayList<NotificationModel> notificationModels) {
                updateDisplay(notificationModels);
            }
        });
    }

    void updateDisplay(ArrayList<NotificationModel> n){
        if(n!=null && n.size()>0 && recyclerView!=null) {
            Collections.reverse(n);
            notificationAdapter = new NotificationAdapter(getContext(), n);
            recyclerView.setAdapter(notificationAdapter);
        }
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_notification, container, false);


        firebaseUser=repository.instance.getAuthInstance().getCurrentUser();

        //save the variables
        closeBtn=view.findViewById(R.id.notification_close);
        recyclerView = view.findViewById(R.id.notification_recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);


        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v)
                        .popBackStack();
            }
        });

        updateDisplay(NotificationListLD.getValue());

        return view;
    }

}
