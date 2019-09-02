package com.example.shareimage.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.shareimage.Activities.MainActivity;
import com.example.shareimage.Models.Repository;
import com.example.shareimage.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment {
    private static final String TAG = "LoginFragment";

    EditText login_email;
    EditText login_password;
    Button loginBtn;
    Repository repository;
    public LoginFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v=inflater.inflate(R.layout.fragment_login, container, false);

        login_email=v.findViewById(R.id.login_email_ev);
        login_password=v.findViewById(R.id.login_password_ev);
        loginBtn=v.findViewById(R.id.login_loginBtn);


        //TODO: change to authentication and after go to home
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent i= new Intent(getActivity(),MainActivity.class);
                // getActivity().startActivity(i);
                login();
            }
        });

        return v;
    }

    private void login(){
        final String str_email = login_email.getText().toString();
        final String str_password = login_password.getText().toString();
        if (TextUtils.isEmpty(str_email) || TextUtils.isEmpty(str_password)){//if one or more fields are empty
            Toast.makeText(getActivity(), "All fields are required!", Toast.LENGTH_SHORT).show();
        }else {
            repository.instance.login(str_email, str_password, new Repository.LoginUserListener() {
                @Override
                public void onComplete(boolean success) {
                    if(success){
                        startActivity(new Intent(getActivity(), MainActivity.class));
                    }
                    else {
                        Toast.makeText(getActivity(), "something is wrong", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

}
