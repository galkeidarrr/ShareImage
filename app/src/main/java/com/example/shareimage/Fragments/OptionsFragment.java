package com.example.shareimage.Fragments;


import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.shareimage.Activities.StartActivity;
import com.example.shareimage.Models.Repository;
import com.example.shareimage.R;
import com.google.firebase.auth.FirebaseAuth;


/**
 * A simple {@link Fragment} subclass.
 */
public class OptionsFragment extends Fragment {

    private static final String TAG = "OptionsFragment";
    TextView logout,settings;
    Repository repository;
    public OptionsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_options, container, false);
        Toolbar toolbar = view.findViewById(R.id.Options_toolbar);
        toolbar.setTitle("Options");


        //save the variables
        logout = view.findViewById(R.id.Options_logout);
        settings= view.findViewById(R.id.Options_settings);

        //go to edit profile
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: go to edit profile");
                Navigation.findNavController(view)
                        .navigate(R.id.action_optionsFragment_to_editProfileFragment);
            }
        });

        //log out from this account and go to beginning
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: log out from this account and go to beginning");
                repository.instance.logOut();
                getActivity().startActivity(new Intent(getActivity(), StartActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });

        return view;
    }

}
