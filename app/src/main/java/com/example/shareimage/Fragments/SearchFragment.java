package com.example.shareimage.Fragments;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.example.shareimage.Adapters.UserAdapter;
import com.example.shareimage.Models.Repository;
import com.example.shareimage.Models.UserModel;
import com.example.shareimage.R;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchFragment extends Fragment {
    private static final String TAG = "SearchFragment";
    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private ArrayList<UserModel> userList;
    Repository repository;

    EditText search_bar;

    public SearchFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v=inflater.inflate(R.layout.fragment_search, container, false);

        recyclerView=v.findViewById(R.id.search_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        search_bar = v.findViewById(R.id.search_barET);
        repository.instance.getAllUsers(new Repository.GetAllUsersListener() {
            @Override
            public void onComplete(ArrayList<UserModel> data) {
                if(data!=null){
                    userList=data;
                    Log.d(TAG, "onComplete: get all users "+userList);
                    userAdapter = new UserAdapter(getContext(),userList, true);
                    recyclerView.setAdapter(userAdapter);
                }
            }

        });
        Log.d(TAG, "onCreateView: "+userList);


        search_bar.addTextChangedListener(new TextWatcher() {//search
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //go the function that search
                repository.instance.searchUsers(charSequence.toString().toLowerCase(), new Repository.GetSearchUsersListener() {
                    @Override
                    public void onComplete(ArrayList<UserModel> userModelArrayList) {
                        if(userModelArrayList!=null){
                            userList=userModelArrayList;
                            userAdapter.setmUsers(userList);
                            recyclerView.setAdapter(userAdapter);
                        }
                    }
                });
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        return v;
    }


}
