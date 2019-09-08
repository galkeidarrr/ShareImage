package com.example.shareimage.Fragments;




import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.shareimage.Models.Repository;
import com.example.shareimage.R;
import com.google.firebase.auth.FirebaseUser;

import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 */
public class BaseFragment extends Fragment {

    private static final String TAG = "BaseFragment";


    public BaseFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_base, container, false);

        //go to login fragment from base
        Button toLoginBtn=view.findViewById(R.id.base_login_btn);
        toLoginBtn.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_baseFragment_to_loginFragment));

        //go to register fragment from base
        Button toRegisterBtn=view.findViewById(R.id.base_register_btn);
        toRegisterBtn.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_baseFragment_to_registerFragment));

        //TODO: check if any user is connected go to home fragment

        return view;
    }
}
